# Qlib 知识点手册 (Qlib Handbook)

## 1. Qlib 简介
[官方文档](https://qlib.doczh.com/) | [GitHub 仓库](https://github.com/microsoft/qlib)

**Qlib** (Quantitative Library) 是微软亚洲研究院开发的开源 AI 量化平台。它的核心优势在于**高性能的数据存储**和**完整的 AI 建模工作流**。

---

## 2. 核心组件 (Core Components)

### 2.1 数据层 (Data Layer)
- **Bin Format**: Qlib 使用自定义的二进制格式存储数据，而非 CSV 或 HDF5。
    - **优势**: 读取速度快几十倍，占用空间小。
    - **命令**: `python scripts/dump_bin.py dump_all ...` (将 CSV 转为 Bin)。
- **Expression Engine**: 支持类似 `Ref($close, 1)` 的公式表达，无需先计算因子再存库，而是**运行时动态计算**。

### 2.2 流程编排 (Workflow)
Qlib 的运行基于配置 (YAML)，一个典型的流程包含：
1.  **Market**: 股票池 (如 CSI300)。
2.  **Data Handler**: 数据加载与预处理 (标准化/缺失值填充)。
3.  **Model**: 训练模型 (LightGBM, LSTM, ALSTM, Transformer)。
4.  **Record**: 记录实验结果 (IC, Rank IC, Returns)。

### 2.3 内置模型 (Model Zoo)
位于 `qlib/contrib/model/`：
-   **GBDT 类**: `LGBModel` (LightGBM), `XGBModel`.
-   **RNN 类**: `LSTM`, `GRU`.
-   **Attention 类**: `Transformer`, `ALSTM`.
-   **Graph 类**: `GATs` (图注意力网络).

---

## 3. 常用操作速查

### 初始化
```python
import qlib
# provider_uri 指向数据目录
qlib.init(provider_uri='~/.qlib/qlib_data/cn_data')
```

### 加载数据 (Dataloader)
```python
from qlib.data import D
# 获取 2020 年 CSI300 成分股的收盘价
df = D.features(instruments=['csi300'], fields=['$close'], start_time='2020-01-01', end_time='2020-12-31')
```

### 运行工作流 (Workflow)
```python
from qlib.workflow import R
from qlib.workflow.record_temp import SignalRecord

# 启动实验
with R.start(experiment_name="my_experiment"):
    model.fit(dataset)
    pred = model.predict(dataset)
    # 记录预测结果
    R.save_objects(**{"pred.pkl": pred})
```

---

## 4. 架构集成建议 (Java + Qlib)

由于 Qlib 是纯 Python 的，Java 端与 Qlib 的交互建议采用 **API 服务化** 模式：

1.  **Python 端**: 使用 FastAPI 封装 Qlib 的 `Workflow`。
    -   `POST /train`: 接收配置参数 -> 生成 YAML -> 调用 `qrun` -> 返回 Experiment ID。
    -   `POST /predict`: 加载模型 -> 计算最新数据的 Score -> 返回 Top K 股票。
2.  **Java 端**: 不直接调用 `python script.py`，而是发送 HTTP 请求。
    -   优点：无需处理复杂的 Python 环境路径，解耦部署。

---

## 5. 学习资源
- [Qlib 论文: Qlib: An AI-oriented Quantitative Investment Platform](https://arxiv.org/abs/2009.11189)
- [官方示例 notebooks](https://github.com/microsoft/qlib/tree/main/examples)

