package com.qik.agent.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.Ordered;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 批处理数据统计 Advisor
 *
 * @author : Qik 2025/8/27 11:41
 */
@Slf4j
public class BatchProcessingAdvisor implements BaseAdvisor {

    public static final String BATCH_NUM = "batch_num";
    public static final String BATCH_SIZE = "batch_size";
    public static final String START_TIME = "start_time";

    private final TotalBatch totalBatch = TotalBatch.INSTANCE;

    private final int order;

    private BatchProcessingAdvisor(Builder builder) {
        this.order = builder.order;
    }

    @NotNull
    @Override
    public ChatClientRequest before(ChatClientRequest request, @NotNull AdvisorChain chain) {
        Map<String, Object> context = request.context();

        // 批次标记
        int batchNum = totalBatch.batchNum.addAndGet(1);
        context.put(BATCH_NUM, batchNum);

        // 批次大小
        int batchSize = -1;
        Object o = context.get(BATCH_SIZE);
        if (o != null) {
            batchSize = (int) o;
        }

        // 开始计时
        long startTime = System.currentTimeMillis();
        if (totalBatch.startTime == 0) {
            totalBatch.startTime = startTime;
        }
        context.put(START_TIME, startTime);

        log.info("开始处理批次 {}, 处理数据量: {}", batchNum, batchSize);
        return request;
    }

    @NotNull
    @Override
    public ChatClientResponse after(ChatClientResponse response, @NotNull AdvisorChain chain) {
        ChatResponse chatResponse = response.chatResponse();
        int batchNum = (int) response.context().get(BatchProcessingAdvisor.BATCH_NUM);
        long startTime = (long) response.context().get(BatchProcessingAdvisor.START_TIME);
        long endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;

        if (chatResponse != null) {
            Usage usage = chatResponse.getMetadata().getUsage();
            Integer promptTokens = usage.getPromptTokens();
            Integer completionTokens = usage.getCompletionTokens();
            Integer totalTokens = usage.getTotalTokens();

            // 记录Token使用统计
            log.info("批次 {} 处理完成, 耗时: {}ms, Token消耗: in[{}]/out[{}]/total[{}]",
                    batchNum, processingTime, promptTokens, completionTokens, totalTokens);

            // 统计
            totalBatch.endTime = endTime;
            totalBatch.promptTokens.addAndGet(totalTokens);
            totalBatch.completionTokens.addAndGet(totalTokens);
        } else {
            log.error("处理批次 {} 时结果为空, 耗时: {}ms", batchNum, processingTime);
        }

        return response;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private int order = Ordered.LOWEST_PRECEDENCE - 8000;

        private Builder() {
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public BatchProcessingAdvisor build() {
            return new BatchProcessingAdvisor(this);
        }
    }

    /**
     * 统计多次会话数据
     */
    public enum TotalBatch {
        INSTANCE;

        private final AtomicInteger batchNum = new AtomicInteger(0);
        private long startTime = 0;
        private long endTime = 0;
        private final AtomicLong promptTokens = new AtomicLong(0);
        private final AtomicLong completionTokens = new AtomicLong(0);

        public TotalBatchData getDataAndClear() {
            TotalBatchData data = new TotalBatchData(
                    endTime - startTime,
                    promptTokens.get(),
                    completionTokens.get()
            );

            clearTokens();
            return data;
        }

        private void clearTokens() {
            startTime = 0;
            endTime = 0;
            promptTokens.set(0);
            completionTokens.set(0);
        }
    }

    public record TotalBatchData(long time, long promptTokens, long completionTokens) {
    }
}
