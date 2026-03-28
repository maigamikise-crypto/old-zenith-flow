import os

TARGET_DIR = r"zenith-flow-admin/zenith-flow-quant/src/main/java/com/zenithflow/quant/mapper"
OLD_IMPORT = "import com.zenithflow.common.datasource.annotation.DataSource;"
NEW_IMPORT = "import com.zenithflow.commons.dynamic.datasource.annotation.DataSource;"

def fix_import(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if OLD_IMPORT in content:
        new_content = content.replace(OLD_IMPORT, NEW_IMPORT)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed: {file_path}")
    else:
        print(f"Skipped (no match): {file_path}")

def process_dir(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                fix_import(os.path.join(root, file))

if __name__ == "__main__":
    process_dir(TARGET_DIR)

