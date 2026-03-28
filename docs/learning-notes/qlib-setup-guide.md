# Qlib 安装与配置完整指南

## 当前状态

根据之前的安装尝试，Qlib 安装失败是因为需要 **Microsoft Visual C++ Build Tools**。

## 安装步骤

### 步骤 1: 安装 Visual C++ Build Tools（Windows 必需）

1. **下载 Visual C++ Build Tools**
   - 访问：https://visualstudio.microsoft.com/visual-cpp-build-tools/
   - 下载并运行安装程序

2. **安装配置**
   - 选择 "C++ build tools" 工作负载
   - 确保包含以下组件：
     - MSVC v143 - VS 2022 C++ x64/x86 build tools
     - Windows 10/11 SDK
     - CMake tools for Windows

3. **验证安装**
   ```powershell
   # 重启 PowerShell 后，检查编译器是否可用
   cl
   ```

### 步骤 2: 安装 Qlib

```powershell
# 在项目目录下
cd zenith-flow-ai-engine

# 从 GitHub 安装 Qlib
pip install git+https://github.com/microsoft/qlib.git

# 或者如果使用 Poetry
poetry add git+https://github.com/microsoft/qlib.git
```

### 步骤 3: 验证安装

```powershell
python -c "import qlib; print('Qlib installed successfully!')"
```

### 步骤 4: 下载 Qlib 数据（可选）

Qlib 可以使用官方数据或自定义数据。如果使用官方数据：

```powershell
# 下载中国股票市场数据（约 2GB）
python -m qlib.run.get_data qlib_data --target_dir ~/.qlib/qlib_data/cn_data --region cn

# 注意：Windows 下 ~ 可能不工作，使用完整路径
python -m qlib.run.get_data qlib_data --target_dir C:\Users\wuxiao\.qlib\qlib_data\cn_data --region cn
```

**注意**：如果使用 TimescaleDB 数据，可以跳过此步骤，系统会自动从数据库加载。

### 步骤 5: 配置环境变量（可选）

```powershell
# 设置 Qlib 数据目录（如果使用官方数据）
$env:QLIB_PROVIDER_URI = "C:\Users\wuxiao\.qlib\qlib_data\cn_data"

# 设置区域
$env:QLIB_REGION = "cn"

# 设置实验目录
$env:QLIB_EXPERIMENTS_DIR = "..\experiments"
```

或者在 `.env` 文件中配置：
```env
QLIB_PROVIDER_URI=C:\Users\wuxiao\.qlib\qlib_data\cn_data
QLIB_REGION=cn
QLIB_EXPERIMENTS_DIR=../experiments
```

## 安装完成后的下一步

### 1. 测试 Qlib 集成

创建测试脚本 `test_qlib.py`：

```python
"""测试 Qlib 集成"""
import sys
import os

# 添加项目路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

try:
    from zenith_ai.services.qlib_data_loader import QlibDataLoader
    from zenith_ai.services.qlib_workflow_service import QlibWorkflowService
    
    print("✅ Qlib 服务导入成功")
    
    # 测试数据加载器
    loader = QlibDataLoader()
    print("✅ QlibDataLoader 创建成功")
    
    # 测试工作流服务
    workflow = QlibWorkflowService()
    print("✅ QlibWorkflowService 创建成功")
    
    print("\n🎉 Qlib 集成测试通过！")
    
except ImportError as e:
    print(f"❌ 导入失败: {e}")
    print("\n提示：如果 Qlib 未安装，系统会自动使用降级方案（从数据库加载）")
except Exception as e:
    print(f"❌ 测试失败: {e}")
```

运行测试：
```powershell
python test_qlib.py
```

### 2. 启动 AI Engine 服务

```powershell
cd zenith-flow-ai-engine
python -m uvicorn zenith_ai.main:app --reload --port 8000
```

### 3. 测试 API 端点

#### 测试健康检查
```powershell
curl http://localhost:8000/health
```

#### 测试 Qlib 训练 API（如果 Qlib 已安装）
```powershell
curl -X POST http://localhost:8000/api/v1/train/qlib/start `
  -H "Content-Type: application/json" `
  -d '{\"experiment_name\": \"test_experiment\", \"market\": \"csi300\", \"model_type\": \"LGBModel\", \"start_date\": \"2020-01-01\"}'
```

#### 测试传统训练 API（降级方案，不依赖 Qlib）
```powershell
curl -X POST http://localhost:8000/api/v1/train/start `
  -H "Content-Type: application/json" `
  -d '{\"name\": \"test_model\", \"algo\": \"rf\", \"symbols\": null}'
```

### 4. 验证数据连接

确保 TimescaleDB 中有数据：

```python
# 检查数据库连接和数据
from zenith_ai.core.config import settings
from sqlalchemy import create_engine, text

engine = create_engine(settings.DB_URL)
with engine.connect() as conn:
    result = conn.execute(text("SELECT COUNT(*) FROM market_data_bar"))
    count = result.scalar()
    print(f"数据库中有 {count} 条市场数据记录")
```

## 故障排除

### 问题 1: Visual C++ Build Tools 安装失败

**解决方案**：
- 确保以管理员身份运行安装程序
- 检查 Windows 更新是否完整
- 尝试安装完整版 Visual Studio（包含 Build Tools）

### 问题 2: Qlib 安装后导入失败

**解决方案**：
- 检查 Python 版本（需要 >= 3.9）
- 确认安装路径正确：`pip show qlib`
- 尝试重新安装：`pip uninstall qlib && pip install git+https://github.com/microsoft/qlib.git`

### 问题 3: 数据下载失败

**解决方案**：
- 使用 TimescaleDB 数据（推荐）：系统会自动从数据库加载
- 检查网络连接
- 使用代理（如果需要）

### 问题 4: 暂时无法安装 Qlib

**解决方案**：
- **使用降级方案**：系统会自动从 TimescaleDB 加载数据，功能仍然可用
- 所有 API 端点都有降级支持
- 可以在 Linux 服务器上安装 Qlib，然后通过 API 调用

## 推荐工作流

1. **开发阶段**：使用降级方案（从 TimescaleDB 加载），快速迭代
2. **生产环境**：在 Linux 服务器上安装 Qlib，使用完整功能
3. **本地测试**：安装 Visual C++ Build Tools 后使用完整 Qlib 功能

## 下一步

安装完成后，可以：

1. ✅ 运行集成测试
2. ✅ 启动 AI Engine 服务
3. ✅ 测试 API 端点
4. ✅ 开始训练第一个模型
5. ✅ 运行回测

参考 `qlib-integration.md` 了解详细的使用方法。

