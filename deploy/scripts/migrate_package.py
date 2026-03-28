import os

TARGET_DIR = r"zenith-flow-admin/zenith-flow-quant/src/main/java/com/zenithflow/quant"

def replace_in_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = content.replace('package com.zenith.engine', 'package com.zenithflow.quant')
    new_content = new_content.replace('import com.zenith.engine', 'import com.zenithflow.quant')
    new_content = new_content.replace('import jakarta.', 'import javax.')
    
    if content != new_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated: {file_path}")

def process_dir(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                replace_in_file(os.path.join(root, file))

if __name__ == "__main__":
    process_dir(TARGET_DIR)

