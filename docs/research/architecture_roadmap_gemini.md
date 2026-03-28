# Zenith Flow 2025 架构与路线图规划

**日期**: 2025-12-25
**版本**: v1.0
**状态**: 规划中

---

## 1. 架构核心理念：双核驱动 (Hybrid Core)

为了满足 **“既要毫秒级实盘执行，又要强大的 AI 投研回测”** 这一复合需求，Zenith Flow 确立了 **“Python 大脑 + Java 手脚”** 的异构架构。

### 1.1 总体架构图 (Conceptual)

```mermaid
graph TD
    User[用户 (Quant/Traders)] -->|Web UI| Vue[Vue3 前端管理台]
    
    subgraph "大脑: Research & AI (Python)"
        Diff[AI Agents 研讨组] -->|生成策略| Strategy[策略生成]
        Strategy -->|回测请求| Backtest[Qlib 回测引擎]
        Backtest -->|历史数据| DB[(TimeScaleDB)]
        Backtest -->|分析报告| Report[可视化报表数据]
    end
    
    subgraph "手脚: Execution & HFT (Java)"
        Vue -->|指令下发| JavaApp[Java 业务中台 (Spring Boot)]
        JavaApp -->|风控检查| Risk[风控模块 (Risk Control)]
        Risk -->|交易指令| Order[订单管理 (OMS)]
        Order -->|极速通道| Disruptor[Disruptor 队列]
        Disruptor -->|实盘接口| QMT[QMT/XtQuant 接口]
        QMT -->|报单| Market[证券交易所]
    end

    Report -->|JSON| JavaApp
    JavaApp -->|WebSocket| Vue
```

### 1.2 关键模块分工

| 模块 | 语言/技术栈 | 职责核心 | 关键特性 |
| :--- | :--- | :--- | :--- |
| **AI Engine** | Python (Qlib, LangChain) | **思考与推演**。负责市场风格分析、策略自动生成、长周期回测、生成分析报表。 | 生态丰富、计算密集、灵活性高 |
| **Backend** | Java (Spring Boot, Netty) | **管理与执行**。负责用户权限、资产管理、风控最后一道防线、实盘交易指令的高速分发。 | 并发强、延迟低、类型安全 |
| **Frontend** | Vue 3 (Element Plus) | **交互与呈现**。负责投研报表的可视化展示、策略参数配置、实盘监控大屏。 | 交互流畅、图表丰富 |

### 1.3 核心优势
1.  **实盘无忧**: 保留 Java 端 `Disruptor` + `Netty` 的设计，确保在实盘接入 QMT/XtQuant 时，交易指令的处理延迟维持在毫秒甚至微秒级，不因 Python 的 GC 或 GIL 而受阻。
2.  **研投无限**: 解放 Python 端，使其专注于它最擅长的离线计算、大模型交互和复杂回测，无需为“实时性”过度优化代码，保证了策略迭代效率。
3.  **平滑闭环**: Python 生成的“策略信号”或“训练好的模型(ONNX)”可以无缝传递给 Java 端进行实盘执行。

---

## 2. 场景适配性：AI 投研 SaaS 平台

针对 **“AI 间讨论市场风格、形成策略、回测并可视化”** 这一愿景，当前架构具有极高的适配性：

1.  **AI Agents 协作 (Multi-Agent Debate)**:
    *   Python 服务天然适配 `LangChain` 或 `AutoGen`。
    *   可以轻松构建一组 Agents（如：宏观分析员、技术面分析员、风控官）进行多轮对话，最终输出策略代码。
2.  **复杂报表可视化**:
    *   回测产生的大量数据（收益率曲线、最大回撤、夏普比率）由 Python 计算，存入数据库。
    *   Vue3 前端通过 ECharts/D3.js 进行专业级的渲染展示，这是单机版交易软件难以企及的用户体验。
3.  **多用户/SaaS 能力**:
    *   Java 后端提供了成熟的 `Spring Security` 权限体系，支持多团队、多 Fund Manager 同时使用，数据相互隔离。

---

## 3. 项目演进路线图 (Roadmap)

结合 `dev-log-20251225.md` 的当前进度，制定以下阶段计划：

### ✅ 第一阶段：基座搭建 (已完成)
*   **工程化**: Java/Python 微服务工程结构，Poetry/Maven 依赖管理。
*   **AI 基座**: Qlib 完整工作流跑通，E2E 测试通过 (LGBModel, LSTM)。
*   **数据流**: 多源数据采集 (AkShare)，Disruptor 高性能队列集成。
*   **前端**: 基础 Dashboard 与 登录页。

### 🚀 第二阶段：实盘与风控 (当前聚焦 - Q4 2025)
*目标：打通“模拟/实盘交易”的最后一公里，保障资金安全。*
*   **[P0] 交易接口对接**:
    *   接入 **QMT / XtQuant** (Python SDK 或 Java JNI/TCP 封装)。
    *   实现 `OrderService`，打通 Java -> QMT 的报单链路。
*   **[P1] 风控系统 (RMS)**:
    *   实现事前风控：单笔限额、持仓限制、黑名单检查。
    *   实现事中风控：动态止损监控。

### 🤖 第三阶段：AI Agent 赋能 (计划 - Q1 2026)
*目标：引入 LLM，实现“文字生成策略”和“智能研报”。*
*   **AI 投研助理**:
    *   集成 LLM API (DeepSeek/OpenAI)，允许用户用自然语言提问（“分析最近一个月的半导体行情”）。
    *   实现 RAG (检索增强生成)，让 AI 能读取项目的 `docs/learning-notes` 和历史回测数据。
*   **策略自动工厂**:
    *   构建 Agent Workflow：`分析市场 -> 生成 Python 策略代码 -> 自动跑 Qlib 回测 -> 输出评分`。

### 📊 第四阶段：SaaS 化与生态完善 (计划 - Q2 2026)
*   **多租户系统**: 完善用户计费、策略市场。
*   **移动端适配**: 小程序/App 监控实盘状态。

---

## 4. 下一步行动建议 (Next Actions)

1.  **技术调研**: 立即启动关于 **QMT/XtQuant** 的 Java 接入方案调研（优先考虑 TCP/Socket 通信还是 Python 桥接）。
2.  **文档固化**: 将本规划固化为项目白皮书，指导后续开发。
