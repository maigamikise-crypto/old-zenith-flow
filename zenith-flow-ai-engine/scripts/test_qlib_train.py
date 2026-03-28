
import sys
from pathlib import Path
sys.path.append(str(Path(__file__).parent.parent / "src"))

from zenith_ai.services.qlib_workflow_service import QlibWorkflowService
from loguru import logger
import time

def test_train():
    logger.info("Starting isolated training test...")
    service = QlibWorkflowService()
    
    exp_name = f"iso_test_{int(time.time())}"
    logger.info(f"Experiment: {exp_name}")
    
    try:
        result = service.train_model(
            experiment_name=exp_name,
            market="csi300",
            model_type="LGBModel",
            start_date="2020-01-01",
            end_date="2020-02-01"
        )
        print("Training Result:", result)
        
        # Check if experiment exists
        from qlib.workflow import R
        service.data_loader.init_qlib()
        exps = R.list_experiments()
        print("Experiments:", exps)
        
        if isinstance(exps, dict):
             exps_values = exps.values()
        else:
             exps_values = exps
             
        names = [e.name for e in exps_values]
        if exp_name in names:
            print("SUCCESS: Experiment found.")
        else:
            print("FAILURE: Experiment NOT found.")
            
    except Exception as e:
        logger.exception("Isolated training failed")

if __name__ == "__main__":
    test_train()
