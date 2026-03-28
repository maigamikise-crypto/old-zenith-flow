# 推荐量化基座：Microsoft Qlib

如果您在寻找“量化界的 RuoYi”——即**内置了大量策略、模型、数据加载器，开箱即用**的 Python 基座项目，**Microsoft Qlib** 是目前最符合您需求的。

它不是一个空的脚手架，而是一个完整的 AI 量化平台。

## 1. 为什么 Qlib 是 "量化版 RuoYi"?

RuoYi 提供了内置的用户管理、菜单管理；Qlib 提供了内置的 **Data Handler (数据处理器)** 和 **Model Zoo (模型库)**。

| 功能 | RuoYi (Java Web) | Qlib (AI Quant) |
| :--- | :--- | :--- |
| **数据层** | Mybatis + MySQL | Qlib Data Engine (二进制存储，比 HDF5/MySQL 快) |
| **业务逻辑** | Built-in Services (User/Role) | Built-in Data Handlers (Alpha158, Alpha360) |
| **核心算法** | - | **Model Zoo** (内置 20+ 种 SOTA 模型) |
| **流程编排** | Controller -> Service | Workflow (Data -> Model -> Backtest) |

## 2. 内置模型库 (Model Zoo)

Qlib 不仅提供了基座，还直接内置了大量已经写好的、可以直接跑的策略模型：

-   **传统机器学习**:
    -   `LightGBM`, `XGBoost`, `CatBoost` (基于树的模型，目前实战效果最好)
    -   `Linear Model` (线性回归)
-   **深度学习 (RNN/LSTM 类)**:
    -   `LSTM` (长短期记忆网络)
    -   `GRU` (门控循环单元)
    -   `ALSTM` (Attentive LSTM)
-   **前沿 AI 模型 (Transformer/GNN 类)**:
    -   `Transformer` (也就是 Attention is all you need)
    -   `GATs` (Graph Attention Networks, 图神经网络，分析股票关联)
    -   `TRA` (Temporal Routing Adaptor)
-   **强化学习**:
    -   `PPO`, `DQN` (在 `qlib.rl` 模块中)

## 3. 快速集成方案

我们不需要重写整个项目，而是将 Qlib 作为核心依赖引入 `zenith-flow-quant`。

### 目录结构调整建议

保留我们之前的 `src/zenith_quant` 结构，但在内部直接继承 Qlib 的类。

```python
# 示例：继承 Qlib 的模型加载器
from qlib.contrib.model.gbdt import LGBModel

class ZenithLGBModel(LGBModel):
    def __init__(self, config):
        super().__init__(**config)
        # 在这里增加我们自定义的逻辑
```

## 4. 其他备选

-   **Backtrader**: 
    -   **特点**: 纯回测框架，内置了大量技术指标 (MA, RSI)，但**没有内置 AI 模型**。它像是一个工具箱，不是一个完整的“后台系统”。
-   **FinRL**:
    -   **特点**: 专注于**强化学习** (Reinforcement Learning)。如果您只想做强化学习，它是首选；但如果您想做通用的量化 (包括 LightGBM 等)，Qlib 更全面。
-   **Vn.py**:
    -   **特点**: 专注于**实盘交易**和**CTA 策略** (期货趋势)。内置了海量的交易接口 (CTP, IB)，但 AI 能力较弱。

## 5. 结论

**推荐使用 Microsoft Qlib 作为核心基座。**

它最像 RuoYi：
1.  **开箱即用**: 下载数据 -> 运行 Workflow -> 出结果。
2.  **可扩展**: 您可以像写 RuoYi 插件一样，写自定义的 `Dataset` 或 `Model`。
3.  **工业级**: 微软出品，被大量对冲基金使用。

