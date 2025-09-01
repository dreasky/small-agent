# 简易自定义智能体

## 服务器运维工具 - [onePanel](https://1panel.cn/)

```bash
# 安装
bash -c "$(curl -sSL https://resource.fit2cloud.com/1panel/package/v2/quick_start.sh)"
```

## 会话记忆 - 基于Redis [memory](src/main/java/com/qik/agent/ai/memory)

### 部署：

在1panel管理页面应用商店安装redis容器

### 依赖引入

```xml

<dependencies>
    <!-- springAi - alibaba -->
    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-autoconfigure-memory</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-starter-memory-redis</artifactId>
    </dependency>
    <!-- redis客户端依赖 -->
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>5.2.0</version>
    </dependency>
</dependencies>
```

## 向量储存 - pgVector

### docker部署

```bash
# 拉取镜像 
docker pull pgvector/pgvector:0.8.0-pg17-bookworm

# 创建容器
docker run -d --name pgvector -e POSTGRES_USER=[yourName] -e POSTGRES_PASSWORD=[yourPassword] -p 5432:5432 [imageName]
```

### 依赖引入

```xml

<dependencies>
    <!-- pgVector 基于postgresql的向量储存-->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-advisors-vector-store</artifactId>
    </dependency>
</dependencies>
```

### 模型默认输出维度

| 模型平台      | 模型名称                                   | 默认输出维度 |
|-----------|----------------------------------------|--------|
| dashscope | multimodal-embedding-v1                | 1024   |
| ollama    | MadMind/Qwen3-Embedding-8B-GGUF-Q4_K_M | 4096   |

## Utility

### [reptiles](src/main/java/com/qik/agent/utility/reptiles)

简易自定义html抓取工具，包括三部分：

- TargetHandle（负责获取网页内容）
- CleanRule（负责清洗内容）
- ResultHandle（负责处理输出结果）

### [translation](src/main/java/com/qik/agent/utility/translation)、

腾讯云翻译