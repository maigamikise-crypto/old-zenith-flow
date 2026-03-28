# 量化 AI 项目功能调研与规划 (AI Quant Features Research)

基于业界成熟项目 (Microsoft Qlib, Backtrader, Zipline, Vn.py) 及 AI 量化前沿趋势，整理 Zenith Flow Quant 应具备的功能模块。

## 1. 数据层 (Data Layer)

底层的基石，需要支持多源异构数据的高效存储与查询。

- **多源采集 (Data Ingestion)**
  - [x] **股票日线/分钟线**: Tushare, Baostock, Yahoo Finance.
  - [ ] **高频数据 (L1/L2)**: 逐笔成交 (Tick), 买卖队列.
  - [ ] **另类数据**: 宏观经济指标, 舆情新闻 (NLP), 财报数据.
  - [ ] **加密货币**: Binance, OKX API (可选).
- **数据存储 (Storage)**
  - **时序数据库**: TimescaleDB / InfluxDB (已选定 TimescaleDB).
  - **缓存层**: Redis (实时行情缓存).
  - **文件存储**: Parquet / HDF5 (用于大规模模型训练，I/O 更快).
- **数据处理 (Processing)**
  - **清洗**: 去除异常值, 复权处理 (前复权/后复权), 停牌填充.
  - **对齐**: 保证不同资产在同一时间戳上的数据对齐.

## 2. 特征工程 (Feature Engineering)

AI 模型的核心输入。

- **基础因子**: OHLCV 衍生指标 (MA, RSI, MACD, Bollinger Bands).
- **Alpha 因子**:
  - **WorldQuant Alphas**: 经典的 101 个 Alpha 因子实现.
  - **遗传编程挖掘**: 使用 gplearn 自动挖掘公式化因子.
- **深度因子**:
  - 这里的“特征”是原始数据通过神经网络提取的 Embedding.
- **因子分析**:
  - IC/IR 分析 (Information Coefficient).
  - 因子自相关性分析.
  - 因子分层回测 (Layered Backtest).

## 3. 模型层 (Model Zoo)

支持多种预测模型，不仅限于单一算法。

- **传统机器学习 (Machine Learning)**
  - [x] Linear Regression / Logistic Regression
  - [x] Random Forest / SVM
  - [ ] **GBDT 系列**: LightGBM (速度快，效果好), XGBoost, CatBoost.
- **深度学习 (Deep Learning)**
  - [ ] **RNN/LSTM/GRU**: 适合时序数据.
  - [ ] **Transformer (Time-Series)**: 捕捉长序列依赖.
  - [ ] **GNN (Graph Neural Networks)**: 利用产业链关系图谱进行预测.
- **强化学习 (Reinforcement Learning)**
  - [ ] **Environment**: 搭建符合 OpenAI Gym 接口的交易环境.
  - [ ] **Agents**: PPO, DQN, A3C 等算法，直接输出交易动作 (Buy/Sell/Hold).

## 4. 策略引擎 (Strategy Engine)

将模型预测转化为具体的交易信号。

- **信号生成**: Top-K 选股, 阈值触发.
- **仓位管理 (Portfolio Management)**:
  - **Markowitz 均值方差模型**: 优化投资组合权重.
  - **风险平价 (Risk Parity)**.
  - **凯利公式**.
- **风控模块 (Risk Control)**:
  - 最大回撤限制 (Max Drawdown).
  - 单票持仓上限.
  - 止盈止损策略 (Stop Loss / Take Profit).

## 5. 回测系统 (Backtesting)

验证策略有效性的关键。

- **模式**:
  - **向量化回测 (Vectorized)**: 速度极快，适合初步筛选策略 (类似 Zipline/Qlib).
  - **事件驱动回测 (Event-Driven)**: 模拟真实交易流，更精准 (类似 Backtrader/Vn.py).
- **评价指标**: Sharpe Ratio, Sortino Ratio, Annualized Return, Max Drawdown, Win Rate.
- **可视化**: 资金曲线图, 持仓分布图, 交易明细表.

## 6. 实盘交易 (Live Trading)

- **交易网关 (Gateway)**:
  - **QMT / XtQuant**: 连接券商实盘软件 (MiniQMT).
  - **CTP**: 期货市场标准接口.
  - **IB API**: 盈透证券 (美股/港股).
- **订单执行 (Execution)**:
  - **算法交易**: TWAP, VWAP (拆单算法，减少市场冲击).
  - **滑点控制**.

## 7. 推荐架构演进

1.  **Phase 1 (Current)**: 基于 Admin + 简单 Python 脚本。
2.  **Phase 2 (Engineering)**: 引入 Qlib 风格的 Dataset 和 Model Loader，规范化特征计算。
3.  **Phase 3 (Live)**: 对接 MiniQMT，跑通实盘闭环。

