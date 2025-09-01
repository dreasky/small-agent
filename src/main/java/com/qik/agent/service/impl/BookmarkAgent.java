package com.qik.agent.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.qik.agent.ai.advisor.BatchProcessingAdvisor;
import com.qik.agent.common.exception.BusinessException;
import com.qik.agent.common.exception.ErrorCode;
import com.qik.agent.model.bookmark.Bookmark;
import com.qik.agent.model.bookmark.CategoryNode;
import com.qik.agent.model.bookmark.CategoryTemplate;
import com.qik.agent.model.bookmark.GroupedBookmark;
import com.qik.agent.service.BookmarkAgentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : Qik 2025/8/27 11:00
 */
@Service
@Slf4j
public class BookmarkAgent implements BookmarkAgentService {

    @Resource
    private ChatClient bookmarkChatClient;

    @Resource
    private PromptTemplate optimizeCategoryTemplate;

    @Resource
    private PromptTemplate groupBookmarksTemplate;

    // 配置常量
    private static final int DEFAULT_BATCH_SIZE = 50;
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;
    private static final int DEFAULT_TIMEOUT_SECONDS = 600; // 10分钟超时

    @Override
    public CategoryTemplate optimizeCategoryTemplate(CategoryTemplate categoryTemplate) {
        if (categoryTemplate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分组模板不能为空");
        }

        return bookmarkChatClient.prompt()
                .user(user -> user.text(optimizeCategoryTemplate.getTemplate())
                        .param("categoryTemplate", categoryTemplate))
                .call()
                .entity(CategoryTemplate.class);
    }

    @Override
    public List<GroupedBookmark> groupBookmarks(List<Bookmark> bookmarks, CategoryTemplate categoryTemplate) {
        // 数据校验
        if (bookmarks == null || bookmarks.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "书签不能为空");
        }
        if (categoryTemplate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分组模板不能为空");
        }

        log.info("开始对 {} 个书签进行AI分类", bookmarks.size());

        // 使用try-with-resources确保线程池正确关闭
        try (ExecutorService executorService = createOptimizedThreadPool()) {
            return processBookmarksInBatches(bookmarks, executorService, categoryTemplate);
        } catch (Exception e) {
            log.error("AI分类处理过程中发生严重错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI分类处理失败");
        } finally {
            BatchProcessingAdvisor.TotalBatchData data =
                    BatchProcessingAdvisor.TotalBatch.INSTANCE.getDataAndClear();
            log.info("总耗时: {}, Token消耗(in/out): {}/{}, 总Token消耗: {}",
                    data.time(), data.promptTokens(), data.completionTokens(), data.promptTokens() + data.completionTokens());

        }
    }

    /**
     * 分批处理书签
     */
    private List<GroupedBookmark> processBookmarksInBatches(
            List<Bookmark> bookmarks,
            ExecutorService executorService,
            CategoryTemplate categoryTemplate
    ) {
        // 单个批次大小
        int batchSize = calculateOptimalBatchSize(bookmarks.size());
        // 拆分原数据
        List<List<Bookmark>> partition = ListUtil.partition(bookmarks, batchSize);
        // 批次总数
        int totalBatches = partition.size();

        log.info("使用批次大小: {}, 总批次数: {}", batchSize, totalBatches);

        // 创建所有批次的Future
        List<CompletableFuture<List<GroupedBookmark>>> futures = new ArrayList<>();
        AtomicInteger processedBatches = new AtomicInteger(0);
        AtomicInteger successfulBatches = new AtomicInteger(0);
        AtomicInteger failedBatches = new AtomicInteger(0);

        for (List<Bookmark> batch : partition) {
            CompletableFuture<List<GroupedBookmark>> future = CompletableFuture
                    .supplyAsync(() -> processBatch(batch, categoryTemplate), executorService)
                    .whenComplete((result, throwable) -> {
                        processedBatches.incrementAndGet();
                        if (throwable != null) {
                            failedBatches.incrementAndGet();
                            log.error("处理失败: {}", throwable.getMessage());
                        } else {
                            successfulBatches.incrementAndGet();
                            log.debug("处理成功，结果数量: {}", result.size());
                        }
                    });

            futures.add(future);
        }

        // 等待所有批次完成，设置超时
        try {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );

            allFutures.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // 收集所有结果
            List<GroupedBookmark> allResults = futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            log.error("获取批次结果时发生错误: {}", e.getMessage());
                            return new ArrayList<GroupedBookmark>();
                        }
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            log.info("批量分类完成 - 总批次数: {}, 成功: {}, 失败: {}, 总结果数: {}",
                    totalBatches, successfulBatches.get(), failedBatches.get(), allResults.size());

            return allResults;

        } catch (TimeoutException e) {
            log.error("AI分类处理超时，已处理 {} 个批次", processedBatches.get());
            // 取消未完成的Future
            futures.forEach(future -> future.cancel(true));
            throw new BusinessException(ErrorCode.TIMEOUT, "AI分类处理超时");
        } catch (Exception e) {
            log.error("等待批次完成时发生错误: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI分类处理失败");
        }
    }

    /**
     * 处理单个批次
     */
    private List<GroupedBookmark> processBatch(
            List<Bookmark> batch,
            CategoryTemplate categoryTemplate
    ) {
        String templateName = categoryTemplate.getTemplateName();
        String description = categoryTemplate.getDescription();
        List<CategoryNode> children = categoryTemplate.getRootNode().getChildren();
        try {
            List<GroupedBookmark> response = bookmarkChatClient.prompt()
                    .user(user -> user.text(groupBookmarksTemplate.getTemplate())
                            .param("templateName", templateName)
                            .param("description", description)
                            .param("categoryTemplate", JSONUtil.toJsonStr(children))
                            .param("bookmarkList", JSONUtil.toJsonStr(batch)))
                    .advisors(spec -> spec.param(BatchProcessingAdvisor.BATCH_SIZE, batch.size()))
                    .call()
                    .entity(new ParameterizedTypeReference<>() {
                    });

            // 响应为空
            if (response == null) return List.of();

            log.info("书签输入/输出: {} / {}", batch.size(), response.size());
            return response;
        } catch (Exception e) {
            log.error("发生错误，书签数量: {}, 错误: {}",
                    batch.size(), e.getMessage(), e);

            // 返回空结果而不是抛出异常，避免影响其他批次
            return new ArrayList<>();
        }
    }

    /**
     * 创建优化的线程池
     */
    private ExecutorService createOptimizedThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                DEFAULT_THREAD_POOL_SIZE,
                DEFAULT_THREAD_POOL_SIZE * 2, // 允许更多线程处理突发负载
                60L, // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), // 更大的队列容量
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：调用者运行
        );

        // 设置线程名称前缀，便于调试
        executor.setThreadFactory(r -> {
            Thread t = new Thread(r);
            t.setName("ai-categorization-" + t.getName());
            return t;
        });

        return executor;
    }

    /**
     * 计算最优批次大小
     */
    private int calculateOptimalBatchSize(int totalBookmarks) {
        return Math.min(DEFAULT_BATCH_SIZE, totalBookmarks);
    }

}
