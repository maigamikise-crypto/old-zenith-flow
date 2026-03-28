"""
端到端测试调试脚本
"""
import sys
import time
import traceback
from pathlib import Path

# 添加 src 到 path
project_root = Path(__file__).parent.parent
sys.path.insert(0, str(project_root / "src"))

# 调试路径
print(f"DEBUG: sys.path = {sys.path}")
try:
    import qlib
    print(f"DEBUG: qlib version = {qlib.__version__}")
    print(f"DEBUG: qlib file = {qlib.__file__}")
except Exception as e:
    print(f"DEBUG: Failed to import qlib directly: {e}")
    import traceback
    traceback.print_exc()

# 禁用 loguru 防止花括号格式化问题
import loguru
loguru.logger.remove()

from zenith_ai.services.qlib_workflow_service import QlibWorkflowService

def main():
    service = QlibWorkflowService()
    exp_name = f'debug_test_{int(time.time())}'
    print(f'=== Testing experiment: {exp_name} ===')

    # Step 1: Train
    print('\n=== STEP 1: Training ===')
    try:
        result = service.train_model(
            experiment_name=exp_name,
            market='csi300',
            model_type='LGBModel',
            start_date='2020-01-01',
            end_date='2020-02-01'
        )
        print(f'Training result: {result}')
        
        if result.get('status') != 'success':
            print(f'Training FAILED: {result.get("error")}')
            return
        
        recorder_id = result.get('recorder_id')
        pred_path = result.get('pred_path')
        print(f'Recorder ID: {recorder_id}')
        print(f'Pred Path: {pred_path}')
        
        # Check if pred_path exists
        if pred_path:
            from pathlib import Path as P
            if P(pred_path).exists():
                print(f'Pred file EXISTS: {pred_path}')
            else:
                print(f'Pred file NOT FOUND: {pred_path}')
        
    except Exception as e:
        print(f'Training exception: {e}')
        traceback.print_exc()
        return

    # Step 2: Backtest
    print('\n=== STEP 2: Backtest ===')
    try:
        bt_result = service.run_backtest(
            experiment_name=exp_name,
            recorder_id=recorder_id,
            pred_path=pred_path,
            start_date='2020-02-02',
            end_date='2020-02-10'
        )
        print(f'Backtest result: {bt_result}')
        
        if bt_result.get('status') == 'success':
            print('\n=== SUCCESS! ===')
        else:
            print(f'\n=== BACKTEST FAILED: {bt_result.get("error")} ===')
            
    except Exception as e:
        print(f'Backtest exception: {e}')
        traceback.print_exc()

if __name__ == "__main__":
    main()
