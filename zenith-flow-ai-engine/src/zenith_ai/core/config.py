import os

class Settings:
    PROJECT_NAME: str = "Zenith Flow AI Engine"
    API_V1_STR: str = "/api/v1"
    
    # Database Config
    DB_URL: str = os.getenv("DB_URL", "postgresql://postgres:password@localhost:5432/zenith_flow")
    
    # Models Config
    MODELS_DIR: str = os.getenv("MODELS_DIR", "../models")
    
    # Qlib Config
    QLIB_PROVIDER_URI: str = os.getenv("QLIB_PROVIDER_URI", "~/.qlib/qlib_data/cn_data")
    QLIB_REGION: str = os.getenv("QLIB_REGION", "cn")  # cn or us
    QLIB_EXPERIMENTS_DIR: str = os.getenv("QLIB_EXPERIMENTS_DIR", "../experiments")
    
    # Java Backend API Config
    RELOAD_API: str = os.getenv("RELOAD_API", "http://localhost:8080/api/backtest/reload-model")

    # Risk Control Settings
    RISK_MAX_POSITION_RATIO: float = 0.2  # Max 20% of account per trade
    RISK_DAILY_LOSS_LIMIT: float = 0.05  # Max 5% loss per day

settings = Settings()
