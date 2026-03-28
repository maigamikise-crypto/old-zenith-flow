import sys
import importlib

def check_import(module_name):
    try:
        importlib.import_module(module_name)
        print(f"[OK] {module_name}")
        return True
    except ImportError as e:
        print(f"[FAIL] {module_name}: {e}")
        return False

print(f"Python Version: {sys.version}")

modules = [
    "pandas",
    "numpy",
    "sklearn",
    "sqlalchemy",
    "psycopg2",
    "requests",
    "akshare",
    "baostock",
    "tushare",
    "fastapi",
    "uvicorn",
    "pydantic",
    "loguru",
    "qlib" # Check qlib separately as it might fail
]

all_passed = True
for module in modules:
    if not check_import(module):
        all_passed = False

try:
    import qlib
    print(f"[OK] qlib (version: {qlib.__version__})")
except ImportError as e:
    print(f"[WARNING] qlib: {e}")
    # Qlib might fail on Windows without build tools, but we want to know if others passed

if all_passed:
    print("\nCore dependencies installed successfully.")
else:
    print("\nSome dependencies failed to install.")
