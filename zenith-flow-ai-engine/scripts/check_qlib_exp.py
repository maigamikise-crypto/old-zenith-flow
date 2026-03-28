from qlib.workflow import R
from zenith_ai.services.qlib_data_loader import QlibDataLoader

loader = QlibDataLoader()
loader.init_qlib()

experiments = R.list_experiments()
print(f"Type: {type(experiments)}")
print(f"Content: {experiments}")
