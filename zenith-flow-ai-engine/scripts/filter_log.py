
filename = 'debug_output_2.txt'
try:
    with open(filename, 'r', encoding='utf-8', errors='ignore') as f:
        lines = f.readlines()
        
    for i, line in enumerate(lines):
        if any(k in line for k in ["ERROR", "Exception", "Traceback", "failed"]):
            print(f"--- Found at line {i+1} ---")
            for j in range(max(0, i-2), min(len(lines), i+15)):
                print(lines[j].strip())
            print("-------------------------")
except FileNotFoundError:
    print(f"{filename} not found")
