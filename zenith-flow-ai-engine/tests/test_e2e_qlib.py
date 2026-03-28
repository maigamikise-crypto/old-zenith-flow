from fastapi.testclient import TestClient
from zenith_ai.main import app
import pytest
import time
from loguru import logger


print("Starting test script...")
try:
    print("Initializing TestClient...")
    client = TestClient(app)
    print("TestClient initialized.")
except Exception as e:
    print(f"Failed to init TestClient: {e}")
    raise

def test_qlib_e2e_workflow():
    print("Entering test function...")
    experiment_name = f"test_e2e_{int(time.time())}"
    
    # 1. Start Training
    logger.info(f"Starting training for experiment: {experiment_name}")
    response = client.post("/api/v1/train/qlib/start", json={
        "experiment_name": experiment_name,
        "market": "csi300",
        "model_type": "LGBModel",
        "start_date": "2020-01-01",
        "end_date": "2020-02-01"  # Short range for speed
    })
    assert response.status_code == 200
    data = response.json()
    print(f"DEBUG_TEST: Response status: {response.status_code}")
    print(f"DEBUG_TEST: Response data: {data}")
    # Since we are running synchronously for debug, status might be success
    assert data["status"] in ["accepted", "success"]
    assert data["experiment_name"] == experiment_name
    
    # Note: TestClient executes background tasks synchronously before returning response
    # So training should be done here if it doesn't fail.
    
    # 2. Verify Experiment Exists
    logger.info("Verifying experiment existence...")
    response = client.get("/api/v1/train/qlib/experiments")
    assert response.status_code == 200
    print(f"Experiments Response: {response.json()}")
    if response.json().get("status") == "error":
        print(f"API Error: {response.json().get('error')}")
    
    experiments = response.json()["experiments"]
    experiment_names = [exp["name"] for exp in experiments]
    assert experiment_name in experiment_names
    
    # Capture recorder_id and pred_path
    recorder_id = data.get("recorder_id")
    pred_path = data.get("pred_path")
    logger.info(f"Recorder ID: {recorder_id}")
    logger.info(f"Pred Path: {pred_path}")

    # ... (verifying experiment checks) ...
    # Assuming verify logic is between here and backtest start
    # Re-locating context for backtest request
    
    # 3. Start Backtest
    logger.info("Starting backtest...")
    backtest_req = {
        "experiment_name": experiment_name,
        "start_date": "2020-02-02",
        "end_date": "2020-02-10"
    }
    if recorder_id:
        backtest_req["recorder_id"] = recorder_id
    if pred_path:
        backtest_req["pred_path"] = pred_path
        
    response = client.post("/api/v1/backtest/qlib/start", json=backtest_req)
    # Backtest is synchronous in the API currently (no BackgroundTasks used in controller for backtest)
    assert response.status_code == 200
    result = response.json()
    logger.info(f"Backtest result: {result}")
    
    # Check for expected keys in backtest result
    # We need to know what run_backtest returns. 
    # QlibWorkflowService.run_backtest returns a dict with 'status', 'risk_analysis', etc.
    if "status" in result and result["status"] == "error":
        print(f"FULL BACKTEST ERROR: {result.get('error')}")
        pytest.fail(f"Backtest failed: {result.get('error')}")
        
    assert "risk_analysis" in result or "analysis" in result # Adjust based on actual return
    
if __name__ == "__main__":
    # Allow running directly
    try:
        test_qlib_e2e_workflow()
        print("Test Passed!")
    except Exception as e:
        print(f"Test Failed: {e}")
        import traceback
        traceback.print_exc()
