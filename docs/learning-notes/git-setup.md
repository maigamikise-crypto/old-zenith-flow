# Git Repository Setup & Management

本文档记录了 `zenith-flow` 项目的 Git 仓库初始化、配置及推送到 GitHub 的全过程。

## 1. 初始化仓库 (Initialization)

项目采用 Monorepo 结构，在根目录下初始化 Git 仓库：

```bash
# 初始化 Git
git init

# 重命名主分支为 main (符合现代 Git 习惯)
git branch -M main
```

## 2. 忽略文件配置 (.gitignore)

创建 `.gitignore` 文件，配置需要排除的文件和目录，涵盖以下类别：

*   **通用**: OS 生成文件 (`.DS_Store`, `Thumbs.db`), 日志 (`*.log`)
*   **IDE**: JetBrains (`.idea/`), VS Code (`.vscode/`)
*   **Backend (Java)**: 构建产物 (`target/`, `*.jar`, `*.class`)
*   **Frontend (Node/Vue)**: 依赖库 (`node_modules/`), 构建输出 (`dist/`), 环境变量 (`.env.local`)
*   **AI Engine (Python)**: 缓存 (`__pycache__/`), 虚拟环境 (`venv/`), 字节码 (`*.pyc`)
*   **Database**: 本地数据卷 (`timescale_data/`, `mysql_data/`)

## 3. 首次提交 (First Commit)

将所有项目文件添加到暂存区并提交：

```bash
# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: Zenith Flow monorepo structure"
```

## 4. 关联远程仓库 (Remote Configuration)

关联 GitHub 远程仓库并推送代码：

```bash
# 添加远程仓库 origin
git remote add origin git@github.com:wuxiaaao/zenith-flow.git

# 推送代码并建立追踪关系
git push -u origin main
```

## 5. 常用操作 (Common Operations)

### 提交更改
```bash
git add .
git commit -m "feat: description of changes"
git push
```

### 拉取更新
```bash
git pull origin main
```

## 仓库信息

*   **GitHub Repository**: [https://github.com/wuxiaaao/zenith-flow](https://github.com/wuxiaaao/zenith-flow)
*   **SSH URL**: `git@github.com:wuxiaaao/zenith-flow.git`

