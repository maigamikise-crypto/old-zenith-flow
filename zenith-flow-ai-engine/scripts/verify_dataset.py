
import qlib
from qlib.data.dataset import DatasetH
from qlib.config import REG_CN
from pathlib import Path
import os
import sys

# Init qlib
provider_uri = "~/.qlib/qlib_data/cn_data"
provider_uri = str(Path(provider_uri).expanduser().resolve())
mount_path = str(Path("../experiments").resolve())
if not os.path.exists(mount_path):
    os.makedirs(mount_path)

print(f"Init qlib: {provider_uri}")
qlib.init(provider_uri=provider_uri, region=REG_CN, mount_path=mount_path)

start_date = "2020-01-01"
end_date = "2020-02-01"
market = "csi300"

dataset_config = {
    "handler": {
        "class": "Alpha158",
        "module_path": "qlib.contrib.data.handler",
        "kwargs": {
            "start_time": start_date,
            "end_time": end_date,
            "fit_start_time": start_date,
            "fit_end_time": end_date,
            "instruments": market
        }
    },
    "segments": {
        "train": [start_date, end_date],
        "valid": [start_date, end_date],
        "test": [start_date, end_date]
    }
}

print("Instantiating DatasetH...")
try:
    ds = DatasetH(**dataset_config)
    print(f"Success! DS: {ds}")
except Exception as e:
    print(f"Error: {e}")
    import traceback
    traceback.print_exc()

print("Done.")
