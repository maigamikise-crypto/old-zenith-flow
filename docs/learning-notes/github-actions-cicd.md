# GitHub Actions CI/CD 指南

本文档详细介绍了本项目中使用的 GitHub Actions 工作流配置及相关概念，旨在帮助团队成员理解持续集成/持续交付 (CI/CD) 的运作机制。

## 1. 核心概念 (Core Concepts)

**GitHub Actions** 是 GitHub 提供的 CI/CD 平台，允许开发者直接在仓库中自动化构建、测试和部署流程。

### 关键术语

- **Workflow (工作流)**:
    - 一个可配置的自动化过程，由 YAML 文件定义（位于 `.github/workflows/` 目录下）。
    - 可以由特定事件触发（如 push 代码、提交 PR 或定时任务）。

- **Events (事件)**:
    - 触发工作流运行的特定活动。
    - 常见事件：`push` (代码推送), `pull_request` (合并请求), `schedule` (定时触发), `workflow_dispatch` (手动触发)。

- **Jobs (作业)**:
    - 工作流中包含的一组步骤。
    - **并行运行**: 默认情况下，不同的 Job 是并行执行的（除非使用了 `needs` 关键字定义依赖）。
    - **独立环境**: 每个 Job 都在其独立的虚拟机运行器 (Runner) 上执行，互不干扰。

- **Runner (运行器)**:
    - 执行 Job 的服务器。
    - GitHub 托管运行器：`ubuntu-latest`, `windows-latest`, `macos-latest`。
    - 自托管运行器 (Self-hosted)：使用自己的服务器。

- **Steps (步骤)**:
    - Job 中的具体操作序列。
    - 可以是 Shell 命令 (`run`)，也可以是封装好的动作 (`uses`)。

- **Actions (动作)**:
    - GitHub Actions 平台上的独立命令单元。例如 `actions/checkout` 用于拉取代码，`actions/setup-java` 用于配置 Java 环境。

---

## 2. 本项目 CI 配置详解

配置文件位于: `.github/workflows/ci.yml`

该工作流名为 "Zenith Flow CI"，包含两个并行任务：后端构建 (`backend-build`) 和 前端构建 (`frontend-build`)。

### 配置代码说明

```yaml
name: Zenith Flow CI  # 工作流名称，显示在 GitHub Actions 页面

# 触发条件 (Triggers)
on:
  push:
    branches: [ main ]        # 当代码推送到 main 分支时触发
  pull_request:
    branches: [ main ]        # 当向 main 分支发起合并请求时触发

# 作业定义 (Jobs)
jobs:
  # ------------------------------------------------------------
  # 任务 1: 后端构建 (Backend Build)
  # ------------------------------------------------------------
  backend-build:
    runs-on: ubuntu-latest    # 运行环境：最新版 Ubuntu Linux
    defaults:
      run:
        working-directory: backend  # 【关键】设置默认工作目录为 backend (Monorepo 结构)

    steps:
    - uses: actions/checkout@v3     # 步骤 1: 拉取代码仓库
    
    - name: Set up JDK 17           # 步骤 2: 配置 Java 环境
      uses: actions/setup-java@v3   # 使用官方 action
      with:
        java-version: '17'          # 指定 JDK 版本
        distribution: 'temurin'     # 发行版使用 Eclipse Temurin
        cache: 'maven'              # 【优化】开启 Maven 依赖缓存，加速后续构建
        
    - name: Build with Maven        # 步骤 3: 执行 Maven 构建
      run: mvn clean package -DskipTests  # 清理并打包，跳过测试以加快速度

  # ------------------------------------------------------------
  # 任务 2: 前端构建 (Frontend Build)
  # ------------------------------------------------------------
  frontend-build:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend # 【关键】设置默认工作目录为 frontend

    steps:
    - uses: actions/checkout@v3     # 步骤 1: 拉取代码
    
    - name: Set up Node.js          # 步骤 2: 配置 Node.js 环境
      uses: actions/setup-node@v3
      with:
        node-version: '18'          # 指定 Node.js 版本 (LTS)
        cache: 'npm'                # 【优化】开启 npm 缓存
        cache-dependency-path: frontend/package-lock.json # 指定锁文件路径用于计算缓存 Key
        
    - name: Install dependencies    # 步骤 3: 安装依赖
      run: npm ci                   # 使用 npm ci (Clean Install)，比 npm install 更快更严格，适合 CI 环境
      
    - name: Build                   # 步骤 4: 构建项目
      run: npm run build            # 执行构建命令
```

---

## 3. 设计思路与优化

### 为什么这样配置？

1.  **并行构建 (Parallel Execution)**:
    *   我们将 `backend-build` 和 `frontend-build` 分开定义为两个独立的 Job。
    *   GitHub 会同时启动两台虚拟机分别运行这两个任务。
    *   **收益**: 总构建时间取决于最慢的那个任务，而不是两者之和，大大提高了效率。

2.  **依赖缓存 (Caching)**:
    *   配置了 `cache: 'maven'` 和 `cache: 'npm'`。
    *   **机制**: 第一次运行后，GitHub 会把 `~/.m2` (Maven 仓库) 和 `node_modules` (npm 依赖) 缓存起来。
    *   **收益**: 下一次运行时，如果依赖文件 (`pom.xml` 或 `package-lock.json`) 没有变更，CI 将直接复用缓存，避免重复下载大量依赖包，显著缩短构建时间。

3.  **Monorepo 支持**:
    *   由于本项目是 Monorepo（单体仓库包含前后端），代码分别位于 `backend/` 和 `frontend/` 目录。
    *   通过 `defaults.run.working-directory` 关键字，我们告诉 Action 所有 `run` 命令默认在指定的子目录下执行，避免了每次都要 `cd backend` 的麻烦。

4.  **严谨的安装命令**:
    *   前端使用 `npm ci` 而不是 `npm install`。`npm ci` 严格根据 `package-lock.json` 安装依赖，确保 CI 环境与开发环境完全一致，且安装速度通常更快。

