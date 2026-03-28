# Qlib 深度集成文档

## 概述

本项目已完成 Qlib（Microsoft AI-oriented Quantitative Investment Platform）的深度集成，实现了完整的工作流：**数据加载 -> 模型训练 -> 回测**。

## 功能特性

### 1. 数据加载器 (`QlibDataLoader`)

- 从 TimescaleDB 加载市场数据
- 自动转换为 Qlib 标准格式（MultiIndex: instrument, datetime）
- 支持 Qlib 表达式引擎（如 `Ref($close, 1)`）
- 降级方案：Qlib 不可用时自动从数据库加载

### 2. Workflow 服务 (`QlibWorkflowService`)

- **模型训练**：支持多种模型类型
  - `LGBModel`: LightGBM（推荐，速度快）
  - `LSTM`: LSTM 神经网络
  - `ALSTM`: Attention-based LSTM
- **回测**：完整的回测框架
  - TopK 策略
  - 风险分析
  - 性能指标计算

### 3. API 端点

#### 训练 API

```http
POST /api/v1/train/qlib/start
Content-Type: application/json

{
  "experiment_name": "qlib_experiment_1",
  "market": "csi300",
  "model_type": "LGBModel",
  "start_date": "2020-01-01",
  "end_date": "2023-12-31"
}
```

#### 回测 API

```http
POST /api/v1/backtest/qlib/start
Content-Type: application/json

{
  "experiment_name": "qlib_experiment_1",
  "recorder_id": null,  # 使用最新的记录器
  "start_date": "2022-01-01",
  "end_date": "2023-12-31"
}
```

#### 查询实验列表

```http
GET /api/v1/train/qlib/experiments
```

#### 查询记录器列表

```http
GET /api/v1/backtest/qlib/experiments/{experiment_name}/recorders
```

## 安装与配置

### 1. 安装 Qlib

**Windows 用户注意事项**：Qlib 需要编译 Cython 扩展，需要 Microsoft Visual C++ Build Tools。

#### 方法一：从 GitHub 安装（推荐）

```bash
# 1. 首先安装 Visual C++ Build Tools（如果还没有）
# 下载地址：https://visualstudio.microsoft.com/visual-cpp-build-tools/
# 安装时选择 "C++ build tools" 工作负载

# 2. 从 GitHub 安装 Qlib
pip install git+https://github.com/microsoft/qlib.git

# 或者使用 Poetry
poetry add git+https://github.com/microsoft/qlib.git
```

#### 方法二：使用预编译版本（如果可用）

```bash
# 尝试安装预编译版本（如果 PyPI 上有）
pip install qlib
```

#### 方法三：暂时跳过 Qlib（使用降级方案）

如果暂时无法安装 Qlib，系统会自动降级到从 TimescaleDB 直接加载数据，功能仍然可用。

### 2. 下载 Qlib 数据

```bash
# 下载中国股票市场数据
python -m qlib.run.get_data qlib_data --target_dir ~/.qlib/qlib_data/cn_data --region cn

# 下载美国股票市场数据
python -m qlib.run.get_data qlib_data --target_dir ~/.qlib/qlib_data/us_data --region us
```

### 3. 配置环境变量

```bash
# Qlib 数据目录
export QLIB_PROVIDER_URI=~/.qlib/qlib_data/cn_data

# Qlib 区域（cn 或 us）
export QLIB_REGION=cn

# 实验目录
export QLIB_EXPERIMENTS_DIR=../experiments
```

## 使用示例

### Python 代码示例

```python
from zenith_ai.services.qlib_workflow_service import QlibWorkflowService

# 创建服务实例
workflow_service = QlibWorkflowService()

# 训练模型
result = workflow_service.train_model(
    experiment_name="my_experiment",
    market="csi300",
    model_type="LGBModel",
    start_date="2020-01-01",
    end_date="2023-12-31"
)

print(f"Experiment ID: {result['experiment_id']}")

# 运行回测
backtest_result = workflow_service.run_backtest(
    experiment_name="my_experiment",
    start_date="2022-01-01",
    end_date="2023-12-31"
)

print(f"Backtest Status: {backtest_result['status']}")
```

### cURL 示例

```bash
# 启动训练
curl -X POST http://localhost:8000/api/v1/train/qlib/start \
  -H "Content-Type: application/json" \
  -d '{
    "experiment_name": "test_experiment",
    "market": "csi300",
    "model_type": "LGBModel",
    "start_date": "2020-01-01"
  }'

# 运行回测
curl -X POST http://localhost:8000/api/v1/backtest/qlib/start \
  -H "Content-Type: application/json" \
  -d '{
    "experiment_name": "test_experiment",
    "start_date": "2022-01-01",
    "end_date": "2023-12-31"
  }'
```

## 数据流程

1. **数据加载**：
   - 从 TimescaleDB 的 `market_data_bar` 表加载数据
   - 转换为 Qlib 格式（MultiIndex, 标准字段名）
   - 股票代码格式转换（如 `000001` -> `000001.SZ`）

2. **特征工程**：
   - 使用 Qlib 的 DataHandler 进行数据预处理
   - 支持表达式引擎（动态计算特征）
   - 标准化、缺失值填充等

3. **模型训练**：
   - 数据集分割（训练/验证/测试）
   - 模型训练和验证
   - 保存预测结果和模型元数据

4. **回测**：
   - 使用训练好的模型生成信号
   - TopK 策略选股
   - 模拟交易执行
   - 风险分析和性能评估

## 注意事项

1. **Windows 安装**：Qlib 在 Windows 上安装可能比较复杂，需要 Visual C++ Build Tools
2. **数据格式**：确保 TimescaleDB 中的数据格式正确（symbol, date, open, high, low, close, volume, amount）
3. **内存使用**：大规模数据训练时注意内存使用，建议分批处理
4. **实验管理**：每次训练会创建一个实验记录，可以通过实验名称和记录器 ID 管理

## 故障排除

### Qlib 导入错误

如果遇到 `ImportError: Qlib is not installed`，请确保：
- 已安装 pyqlib: `pip install pyqlib`
- Python 版本 >= 3.9
- 已安装必要的系统依赖（Windows 需要 Visual C++ Build Tools）

### 数据加载失败

如果 Qlib 数据加载失败，系统会自动降级到从 TimescaleDB 直接加载。确保：
- 数据库连接正常
- `market_data_bar` 表存在且有数据
- 日期格式正确（YYYY-MM-DD）

## 参考资料

- [Qlib 官方文档](https://qlib.doczh.com/)
- [Qlib GitHub](https://github.com/microsoft/qlib)
- [Qlib 论文](https://arxiv.org/abs/2009.11189)

