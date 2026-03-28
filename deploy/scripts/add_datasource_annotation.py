import os

TARGET_DIR = r"zenith-flow-admin/zenith-flow-quant/src/main/java/com/zenithflow/quant/mapper"

def add_annotation(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    if "@DataSource" in content:
        print(f"Skipping {file_path}, already annotated.")
        return

    lines = content.splitlines()
    new_lines = []
    import_added = False
    
    for line in lines:
        if not import_added and line.startswith("import "):
            new_lines.append("import com.zenithflow.common.datasource.annotation.DataSource;")
            import_added = True
        
        if line.strip().startswith("public interface"):
            new_lines.append('@DataSource("quant")')
        
        new_lines.append(line)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write("\n".join(new_lines))
    print(f"Updated: {file_path}")

def process_dir(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                add_annotation(os.path.join(root, file))

if __name__ == "__main__":
    process_dir(TARGET_DIR)

