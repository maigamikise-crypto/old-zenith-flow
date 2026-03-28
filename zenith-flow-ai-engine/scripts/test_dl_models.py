
"""
Deep Learning Models Test Script (LSTM, ALSTM)
"""
import sys
import time
import traceback
from pathlib import Path
import loguru

# Add src to path
project_root = Path(__file__).parent.parent
sys.path.insert(0, str(project_root / "src"))

# Disable loguru default handler to avoid formatting issues
loguru.logger.remove()
loguru.logger.add(sys.stderr, format="{time} {level} {message}", filter="zenith_ai", level="INFO")


from zenith_ai.services.qlib_workflow_service import QlibWorkflowService

def test_model(model_type):
    print(f'\n\n====== Testing Model: {model_type} ======')
    service = QlibWorkflowService()
    exp_name = f'debug_{model_type}_{int(time.time())}'
    
    # Step 1: Train
    print(f'--- Training {model_type} ---')
    try:
        # Use a small dataset range for faster verification
        result = service.train_model(
            experiment_name=exp_name,
            market='csi300',
            model_type=model_type,
            start_date='2020-01-01',
            end_date='2020-06-01'
        )
        print(f'Training result: {result}')
        
        if result.get('status') != 'success':
            print(f'Training FAILED for {model_type}: {result.get("error")}')
            return False
            
        recorder_id = result.get('recorder_id')
        pred_path = result.get('pred_path')
        
    except Exception as e:
        print(f'Training exception for {model_type}: {e}')
        traceback.print_exc()
        return False

    # Step 2: Backtest
    print(f'--- Backtesting {model_type} ---')
    try:
        bt_result = service.run_backtest(
            experiment_name=exp_name,
            recorder_id=recorder_id,
            pred_path=pred_path,
            start_date='2020-06-02',
            end_date='2020-06-15'
        )
        print(f'Backtest result: {bt_result}')
        
        if bt_result.get('status') == 'success':
            print(f'SUCCESS: {model_type} passed E2E test.')
            return True
        else:
            print(f'BACKTEST FAILED for {model_type}: {bt_result.get("error")}')
            return False
            
    except Exception as e:
        print(f'Backtest exception for {model_type}: {e}')
        traceback.print_exc()
        return False

def main():
    models_to_test = ['LSTM', 'ALSTM']
    results = {}
    
    for model in models_to_test:
        results[model] = test_model(model)
        
    print('\n\n====== Final Summary ======')
    all_passed = True
    for model, success in results.items():
        status = "PASS" if success else "FAIL"
        print(f'{model}: {status}')
        if not success:
            all_passed = False
            
    if all_passed:
        print("ALL TESTS PASSED")
    else:
        print("SOME TESTS FAILED")

if __name__ == "__main__":
    main()
