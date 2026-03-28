# Python 工程化基座方案 (Python Engineering Base)

为了将 `zenith-flow-quant` (原 `ai-engine`) 打造为生产级项目，建议采用以下工程化标准。

## 1. 核心工具栈

- **依赖管理**: [Poetry](https://python-poetry.org/)
  - 优势：解决依赖冲突，生成确定的 `poetry.lock`，统一管理虚拟环境。
- **代码风格**: [Ruff](https://github.com/astral-sh/ruff) (替代 Flake8, Isort, Black)
  - 优势：速度极快，All-in-one 配置。
- **类型检查**: [MyPy](http://mypy-lang.org/)
- **测试框架**: [Pytest](https://docs.pytest.org/)
- **Git Hooks**: [Pre-commit](https://pre-commit.com/)

## 2. 推荐目录结构

```text
zenith-flow-quant/
├── .github/                # CI/CD 配置
├── .vscode/                # IDE 配置
├── data/                   # 本地数据 (gitignored)
├── docs/                   # 文档
├── notebooks/              # Jupyter Notebooks (实验/探索)
├── src/
│   └── zenith_quant/       # 核心包名
│       ├── __init__.py
│       ├── data/           # 数据获取与处理
│       │   ├── loader.py
│       │   └── processor.py
│       ├── features/       # 特征工程
│       │   └── indicators.py
│       ├── models/         # 模型定义
│       │   ├── factory.py
│       │   └── strategies/
│       ├── backtest/       # 回测引擎
│       ├── trading/        # 实盘交易接口
│       └── utils/          # 工具类
├── tests/                  # 测试用例
├── .gitignore
├── .pre-commit-config.yaml # 代码提交检查配置
├── pyproject.toml          # 项目核心配置 (依赖/工具配置)
├── README.md
└── run.py                  # 入口脚本
```

## 3. 关键配置示例 (pyproject.toml)

```toml
[tool.poetry]
name = "zenith-quant"
version = "0.1.0"
description = "AI Quantitative Trading Engine for Zenith Flow"
authors = ["Zenith Flow Team"]

[tool.poetry.dependencies]
python = ">=3.9,<3.12"
pandas = "^2.0.0"
numpy = "^1.24.0"
scikit-learn = "^1.2.0"
torch = "^2.0.0"  # 如果使用深度学习
sqlalchemy = "^2.0.0"
psycopg2-binary = "^2.9.0" # 连接 TimescaleDB
tushare = "^1.2.89"

[tool.poetry.group.dev.dependencies]
pytest = "^7.0.0"
ruff = "^0.1.0"
mypy = "^1.0.0"
jupyterlab = "^4.0.0"

[build-system]
requires = ["poetry-core"]
build-backend = "poetry.core.masonry.api"

[tool.ruff]
line-length = 88
select = ["E", "F", "I"] # E: pycodestyle, F: pyflakes, I: isort
```

## 4. 实施步骤

1. 安装 Poetry: `curl -sSL https://install.python-poetry.org | python3 -`
2. 初始化项目: `poetry init`
3. 迁移现有脚本: 将 `ai-engine/src/*.py` 移动到 `src/zenith_quant/` 并模块化。
4. 配置 CI: 在 `.github/workflows` 中添加 Python 测试任务。

