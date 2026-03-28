# Qlib 量化投研新手指南

欢迎来到 Zenith Flow 的量化世界！本文档将帮助你快速理解我们如何使用 Microsoft Qlib 进行 AI 量化策略的开发与验证。

## 1. 什么是 Qlib？

Qlib 是一个面向 AI 的量化投资开源平台。在我们的项目中，它主要负责以下核心环节：
- **数据管理**: 高效存储和加载金融数据（使用 Alpha158 因子库）。
- **模型训练**: 提供标准接口训练机器学习模型（如 LightGBM, LSTM）。
- **回测引擎**: 模拟历史交易，评估策略表现。

## 2. 核心概念速览

在 Zenith Flow 中，我们封装了 Qlib 的复杂性，你只需要关注以下几个概念：

### 2.1 数据处理器 (Data Handler)
我们主要使用 **Alpha158**，它会自动计算 158 个常用的量化因子（如移动平均、RSI、KDJ 等）。
- **输入**: 原始 OHLCV（开高低收量）数据。
- **输出**: 归一化后的特征矩阵，供 AI 模型学习。

### 2.2 模型 (Model)
我们支持多种模型：
- **LGBModel**: 基于决策树的梯度提升模型，训练快，效果好，适合新手入门。
- **LSTM/ALSTM**: 深度学习时序模型，能捕捉更复杂的非线性关系，但需要更多数据和计算资源。

### 2.3 工作流 (Workflow)
一个完整的实验包含三个步骤：
1. **数据准备**: 加载数据并划分为训练集、验证集、测试集。
2. **模型训练 (Train)**: 模型学习历史数据规律。
3. **回测 (Backtest)**: 使用训练好的模型在测试集上进行模拟交易，生成收益曲线。

## 3. 快速上手：运行你的第一个实验

我们提供了封装好的服务 `QlibWorkflowService`，你可以通过脚本快速运行实验。

### 3.1 运行测试脚本
最简单的开始方式是运行我们的端到端测试脚本：

```bash
# 进入 AI 引擎目录
cd zenith-flow-ai-engine

# 运行深度学习模型测试（涵盖训练和回测）
poetry run python scripts/test_dl_models.py
```

该脚本会自动完成：
1. 训练 LSTM 和 ALSTM 模型（使用 2020 年上半年的数据）。
2. 在随后的一段时间进行回测。
3. 输出是否成功的状态。

### 3.2 代码示例
如果你想在自己的代码中调用，可以参考以下模式：

```python
from zenith_ai.services.qlib_workflow_service import QlibWorkflowService

# 初始化服务
service = QlibWorkflowService()

# 1. 训练模型
result = service.train_model(
    experiment_name="my_first_experiment",
    market='csi300',
    model_type='LGBModel',  # 或 'LSTM', 'ALSTM'
    start_date='2020-01-01',
    end_date='2020-06-01'
)

# 2. 运行回测
if result['status'] == 'success':
    backtest_result = service.run_backtest(
        experiment_name="my_first_experiment",
        recorder_id=result['recorder_id'],
        start_date='2020-06-02',
        end_date='2020-06-15'
    )
    print("回测结果:", backtest_result)
```

## 4. 常见问题 (FAQ)

**Q: 为什么训练深度学习模型时报错 `shape invalid`？**
A: 这通常是因为模型参数 `d_feat`（输入特征维度）与数据不匹配。Alpha158 产生 158 个特征，所以必须设置 `d_feat=158`。我们的服务代码已自动处理此配置。

**Q: 为什么报错 `best_param referenced before assignment`？**
A: 这通常是因为数据量太少，验证集为空或未能触发模型更新。建议训练数据至少包含 6 个月以上的历史数据。

**Q: 日志里出现 `KeyError: "'__DEFAULT_FREQ'"` 是什么？**
A: 这是 logging 库冲突导致的。我们在 Worker 进程中禁用了 `loguru` 来解决此问题，确保 Qlib 的日志能正常输出。

## 5. 进阶阅读
- [Qlib 官方文档](https://qlib.readthedocs.io/en/latest/)
- 查看 `src/zenith_ai/services/qlib_workflow_service.py` 了解底层实现。
