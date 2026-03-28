import os

TARGET_DIR = r"zenith-flow-admin"
WEB_DIR = r"../../frontend"

REPLACEMENTS = {
    # Configs
    "username: renren": "username: admin",
    "context-path: /renren-admin": "context-path: /admin",
    "context-path: /renren-api": "context-path: /api",
    "renren:": "zenith:",
    "renren-dynamic-datasource": "zenith-flow-dynamic-datasource",

    # Swagger / Docs
    'description("renren-admin文档")': 'description("Zenith-Flow Admin API")',
    'description("renren-api模块接口文档")': 'description("Zenith-Flow API")',

    # Java Copyrights / Comments
    "https://www.renren.io": "",
    "人人开源": "",

    # Web UI
    "renren-security [Enterprise]": "Zenith Flow [Enterprise]",
    "renren-security": "Zenith Flow"
}

def process_file(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        new_content = content
        for old, new in REPLACEMENTS.items():
            new_content = new_content.replace(old, new)

        if content != new_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Updated: {file_path}")
    except Exception as e:
        print(f"Error processing {file_path}: {e}")

def process_dir(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(('.java', '.yml', '.yaml', '.xml', '.ts', '.js', '.vue', '.properties')):
                process_file(os.path.join(root, file))

if __name__ == "__main__":
    process_dir(TARGET_DIR)
    process_dir(WEB_DIR)

