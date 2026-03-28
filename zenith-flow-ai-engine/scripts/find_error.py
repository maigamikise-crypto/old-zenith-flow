
filename = 'debug_backtest_21.txt'
try:
    with open(filename, 'r', encoding='utf-16', errors='ignore') as f:
        content = f.read()

    print("--- Checking 'DEBUG_TEST' ---")
    if "DEBUG_TEST" in content:
        idx = content.find("DEBUG_TEST")
        # Print next 500 chars (it might span lines)
        print(content[idx:idx+500])
    else:
        print("DEBUG_TEST NOT FOUND")
        
    print("--- Checking 'Pred Path' ---")
    if "Pred Path" in content:
        idx = content.find("Pred Path")
        print(content[idx:idx+200])
    else:
        print("Pred Path NOT FOUND")
        
    print("\n--- Checking 'DEBUG:' ---")
    lines = content.split('\n')
    found_debug = False
    for line in lines:
        if "DEBUG:" in line:
            print(line.strip())
            found_debug = True
    if not found_debug:
        print("DEBUG: NOT FOUND")

    print("\n--- Checking 'Worker stdout' ---")
    if "Worker stdout" in content:
        idx = content.find("Worker stdout")
        print(content[idx:idx+2000])
    else:
        print("Worker stdout NOT FOUND")
        
    print("\n--- Checking 'Training failed' ---")
    if "Training failed" in content:
        idx = content.find("Training failed")
        print(content[idx:idx+2000])
    else:
        print("Training failed NOT FOUND")
        
    print("\n--- Checking 'RuntimeError' ---")
    if "RuntimeError" in content:
        idx = content.find("RuntimeError")
        print(content[idx:idx+1000])
        
    print("\n--- Checking 'Traceback' ---")
    if "Traceback" in content:
        idx = content.find("Traceback")
        # Find last traceback if multiple
        idx = content.rfind("Traceback")
        print(content[idx:idx+2000])

    print("\n--- Checking 'Test Passed!' ---")
    if "Test Passed!" in content:
        print("TEST PASSED!")
    else:
        print("TEST FAILED.")

except Exception as e:
    print(f"Error: {e}")
