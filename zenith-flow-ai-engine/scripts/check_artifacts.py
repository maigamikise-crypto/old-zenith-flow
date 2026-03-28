
import os
from pathlib import Path

mlruns_dir = Path("mlruns")
if not mlruns_dir.exists():
    print("mlruns directory not found")
    exit()

# Find latest experiment directory
exps = [d for d in mlruns_dir.iterdir() if d.is_dir() and d.name.isdigit()]
if not exps:
    print("No experiment directories found")
    exit()

latest_exp = max(exps, key=lambda d: d.stat().st_mtime)
print(f"Latest Experiment: {latest_exp}")

# Find latest recorder in that experiment
recorders = [d for d in latest_exp.iterdir() if d.is_dir() and len(d.name) > 10]
if not recorders:
    print("No recorders found in latest experiment")
    exit()

latest_rec = max(recorders, key=lambda d: d.stat().st_mtime)
print(f"Latest Recorder: {latest_rec}")

# List files in recorder
print("Files in recorder root:")
for f in latest_rec.iterdir():
    print(f"  {f.name} ({f.stat().st_size} bytes)")
    
# Check artifacts subdir
artifacts_dir = latest_rec / "artifacts"
if artifacts_dir.exists():
    print("Files in artifacts subdir:")
    for f in artifacts_dir.iterdir():
        print(f"  {f.name} ({f.stat().st_size} bytes)")
else:
    print("No artifacts subdir")
