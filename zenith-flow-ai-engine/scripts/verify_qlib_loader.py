import sys
from pathlib import Path
import os

# Add src to path
current_dir = Path(__file__).parent
src_path = current_dir.parent / "src"
sys.path.append(str(src_path))
print(f"Added to path: {src_path}")

from zenith_ai.services.qlib_data_loader import QlibDataLoader
from loguru import logger

def verify():
    logger.info("Starting QlibDataLoader verification...")
    # Ensure Qlib provider uri is set to expand user path if needed
    # But Qlib's config might handle it. Let's rely on default config first.
    
    loader = QlibDataLoader()
    
    try:
        # 1. Init
        logger.info("Initializing Qlib...")
        loader.init_qlib()
        
        # 2. Get Instruments
        logger.info("Getting instruments (csi300)...")
        instruments = loader.get_instruments(market="csi300")
        logger.info(f"Got {len(instruments)} instruments")
        if not instruments:
            logger.error("Failed to get instruments!")
            return
            
        # 3. Load Features
        logger.info(f"Loading features for first 5 instruments: {instruments[:5]}")
        features = loader.load_features(
            instruments=instruments[:5],
            fields=['$close', '$volume'],
            start_date='2020-01-01',
            end_date='2020-01-10'
        )
        logger.info(f"Loaded features shape: {features.shape}")
        if features.empty:
             logger.warning("Loaded features are empty! Check date range or data availability.")
        else:
             print(features.head())
        
        logger.info("Verification finished.")
        
    except Exception as e:
        logger.exception(f"Verification failed: {e}")

if __name__ == "__main__":
    verify()
