import sys
from pathlib import Path

# 添加 src 到 path
project_root = Path.cwd()
sys.path.insert(0, str(project_root / "src"))

print(f"Current working directory: {project_root}")
print(f"Python path: {sys.path}")

print("\n--- Diagnostic: Importing Qlib modules one by one ---")

modules_to_test = [
    "qlib",
    "qlib.workflow",
    "qlib.workflow.record_temp",
    "qlib.data.dataset",
    "qlib.contrib.model.gbdt",
    "qlib.contrib.model.pytorch_lstm",
    "qlib.contrib.model.pytorch_alstm",
    "qlib.contrib.strategy.signal_strategy",
    "qlib.backtest",
    "qlib.contrib.evaluate"
]

for mod in modules_to_test:
    try:
        __import__(mod)
        print(f"✅ SUCCESS: {mod}")
    except Exception as e:
        print(f"❌ FAILED: {mod}")
        print(f"   Reason: {e}")
        import traceback
        traceback.print_exc()
        print("-" * 20)

print("\n--- Diagnostic: Importing QlibWorkflowService ---")
try:
    from zenith_ai.services.qlib_workflow_service import QLIB_AVAILABLE
    print(f"QLIB_AVAILABLE in service: {QLIB_AVAILABLE}")
except Exception as e:
    print(f"❌ Failed to import service: {e}")
    traceback.print_exc()
