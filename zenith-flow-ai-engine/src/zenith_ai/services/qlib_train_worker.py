import sys
import os
import yaml
import json
import logging
from pathlib import Path
try:
    import loguru
    loguru.logger.remove()
except ImportError:
    pass

import qlib
from qlib.workflow import R
from qlib.data.dataset import DatasetH
from qlib.utils import init_instance_by_config

# Setup logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger("QlibTrainWorker")

def run_train(config_path, experiment_name, provider_uri, mount_path):
    logger.info(f"Worker started. Config: {config_path}, Exp: {experiment_name}")
    
    # Init qlib
    if not os.path.exists(mount_path):
        os.makedirs(mount_path, exist_ok=True)
    
    # Resolve absolute paths
    provider_uri = str(Path(provider_uri).expanduser().resolve())
    mount_path = str(Path(mount_path).expanduser().resolve())
        
    qlib.init(provider_uri=provider_uri, region="cn", mount_path=mount_path)
    logger.info("Qlib initialized")

    with open(config_path, 'r', encoding='utf-8') as f:
        config = yaml.safe_load(f)

    logger.info(f"Starting Qlib workflow experiments: {experiment_name}")
    with R.start(experiment_name=experiment_name):
        # Load dataset using new config structure
        logger.info("Loading dataset (DatasetH)...")
        dataset_config = config["dataset"]
        
        # Initialize handler using init_instance_by_config
        handler = init_instance_by_config(dataset_config["handler"])
        
        # Create DatasetH with handler and segments
        dataset = DatasetH(handler=handler, segments=dataset_config["segments"])
        logger.info("Dataset loaded")
        
        # Init model
        model_config = config["model"]
        model_class_path = model_config["module_path"]
        model_class_name = model_config["class"]
        logger.info(f"Initializing model: {model_class_name} from {model_class_path}")
        
        # Import model class
        module = __import__(model_class_path, fromlist=[model_class_name])
        model_class = getattr(module, model_class_name)
        model = model_class(**model_config["kwargs"])
        
        # Fit
        logger.info("Training model...")
        model.fit(dataset)
        
        # Predict
        logger.info("Predicting...")
        pred = model.predict(dataset)
        
        # Save
        logger.info("Saving predictions...")
        R.save_objects(**{"pred.pkl": pred})
        
        # Get recorder info
        recorder = R.get_recorder()
        exp_dir = Path(recorder.get_local_dir())
        pred_path_artifact = exp_dir / "artifacts" / "pred.pkl"
        pred_path_root = exp_dir / "pred.pkl"
        
        final_pred_path = str(pred_path_artifact) if pred_path_artifact.exists() else str(pred_path_root)
        logger.info(f"Prediction saved at: {final_pred_path}")
        
        # Output result JSON to stdout for parent process to capture causes issues if logger prints to stdout too.
        # We will print a special delinquent line or just print JSON at end and ensure logger uses stderr?
        # Default logging goes to stderr. So print() goes to stdout. This is fine.
        
        result = {
            "status": "success",
            "experiment_id": recorder.experiment_id,
            "recorder_id": recorder.id,  # MLflowRecorder uses 'id' not 'recorder_id'
            "pred_path": final_pred_path,
        }
        print(f"__RESULT_JSON_START__")
        print(json.dumps(result))
        print(f"__RESULT_JSON_END__")

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("--config_path", required=True)
    parser.add_argument("--experiment_name", required=True)
    parser.add_argument("--provider_uri", required=True)
    parser.add_argument("--mount_path", required=True)
    args = parser.parse_args()
    
    try:
        run_train(args.config_path, args.experiment_name, args.provider_uri, args.mount_path)
    except Exception as e:
        logger.exception("Worker failed")
        error_res = {"status": "error", "error": str(e)}
        print(f"__RESULT_JSON_START__")
        print(json.dumps(error_res))
        print(f"__RESULT_JSON_END__")
        sys.exit(1)
