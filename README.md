# Zenith Flow - AI Quant Trading System

[![GitHub](https://img.shields.io/badge/GitHub-Repo-blue?logo=github)](https://github.com/wuxiaaao/zenith-flow)

基于 **Java (Core Engine)** + **Python (AI Service)** 的微服务架构 A 股量化交易系统。

## 🏗 核心架构 (Architecture)

### 1. 数据层 (Data Layer)
*   **Database:** PostgreSQL 14 / TimescaleDB (量化数据) + MySQL 8.0 (系统管理)
*   **Data Pipeline:**
    *   **Source:** AkShare (主) -> Baostock (备) -> Tushare (灾备)
    *   **Features:** 智能增量更新、多线程并发、批量预检
*   **ORM:** MyBatis Plus (支持多数据源切换)

### 2. 交易引擎 (Java Backend)
*   **Core:** Spring Boot 2.7 + Spring Cloud OpenFeign
*   **Module:** `zenith-flow-quant` 模块负责量化核心逻辑
*   **Messaging:** LMAX Disruptor (高性能内存无锁队列)
*   **Communication:** 通过 Feign Client 调用 Python AI Engine 的 RESTful API

### 3. AI 引擎 (Python Microservice)
*   **Base:** **Microsoft Qlib** + **FastAPI** + **Poetry**
*   **Architecture:** 常驻内存的 Web 服务 (不再是独立脚本)
*   **Capabilities:**
    *   **Model Zoo:** 内置 LightGBM, LSTM, Transformer 等 SOTA 模型
    *   **Async Task:** 后台异步执行耗时训练任务
    *   **Hot Reload:** 训练完成后自动通知 Java 端更新模型
    *   **Inference:** 提供实时预测 API

### 4. 前端可视化 (Dashboard)
*   **Stack:** Vue 3 + Vite + Admin UI Base
*   **Features:**
    *   策略净值 vs 基准净值曲线对比
    *   在线触发 AI 模型训练
    *   多模型回测对比
    *   系统级权限管理与监控

## 🚀 快速开始 (Quick Start)

### Step 1: 启动基础设施
使用 Docker 启动 TimescaleDB (PG) 和 MySQL:
```bash
docker-compose up -d
```

### Step 2: 启动 AI 引擎 (Python)
```bash
cd zenith-flow-ai-engine
poetry install
python -m poetry run python src/zenith_ai/main.py
# 服务运行在 http://localhost:8000
```

### Step 3: 启动后端 (Java)
运行 `backend` 下的主启动类 `ZenithAdminApplication`。
(确保 `ai-engine.url` 配置指向正确的 Python 服务地址)

### Step 4: 启动前端 (Web)
```bash
cd frontend
npm install
npm run dev
```
访问 `http://localhost:80` (或配置的端口)。

## 📋 待办事项 (Todo)
- [x] 基础工程搭建 (Java/Python)
- [x] **架构升级**: 引入 FastAPI + Feign 实现微服务化调用
- [x] **工程化**: Python 引入 Poetry, Ruff, MyPy
- [x] **AI 基座**: 集成 Microsoft Qlib
- [x] 数据管道: 多源灾备、智能增量、并发抓取
- [x] 核心引擎: 引入 Disruptor 高性能队列
- [x] AI 模型集成: ONNX Runtime + 热重载
- [x] 前端可视化: Dashboard, ECharts 图表
- [ ] **实盘对接**: 接入 QMT/XtQuant 交易接口
- [ ] **风控模块**: 最大回撤控制
