# 📚 参考源码项目 (Reference Source Code)

本目录包含用于参考学习的开源量化交易项目源码。

> [!IMPORTANT]
> 这些项目仅作为参考，**不会提交到 Git 仓库**（已在 `.gitignore` 中配置）。

## 📂 项目列表

### 1. Microsoft Qlib
- **用途**: AI 量化投研框架
- **官网**: https://github.com/microsoft/qlib
- **参考重点**:
  - 数据处理流程 (`qlib/data/`)
  - 模型接口设计 (`qlib/model/`)
  - 回测引擎实现 (`qlib/backtest/`)

### 2. VN.py
- **用途**: 全功能量化交易平台
- **官网**: https://github.com/vnpy/vnpy
- **参考重点**:
  - 交易接口封装 (`vnpy/gateway/`)
  - 事件驱动引擎 (`vnpy/event/`)
  - CTA 策略框架 (`vnpy/app/cta_strategy/`)
  - 风控模块设计 (`vnpy/app/risk_manager/`)

## 🔄 更新项目

如需更新参考项目到最新版本：

```bash
cd docs/source-code/qlib
git pull origin main

cd ../vnpy
git pull origin main
```

## 💡 AI 助手提示

在开发 Zenith Flow 时，可参考以下对应关系：

| Zenith Flow 功能 | Qlib 参考 | VN.py 参考 |
|-----------------|----------|-----------|
| AI 模型训练 | ✅ 模型接口 | ❌ |
| 数据处理 | ✅ Alpha158 | ✅ DataFeed |
| 回测引擎 | ✅ Backtest | ✅ BacktestingEngine |
| **实盘交易** | ❌ | ✅ Gateway |
| **风控模块** | ❌ | ✅ RiskManager |
