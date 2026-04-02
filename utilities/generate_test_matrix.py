#!/usr/bin/env python3
import os
import re
import json

def chunk_list(lst, n):
    k, m = divmod(len(lst), n)
    return [lst[i*k+min(i, m) : (i+1)*k+min(i+1, m)] for i in range(n) if len(lst[i*k+min(i, m) : (i+1)*k+min(i+1, m)]) > 0]

def main():
    test_dir = 'src/test/java'
    test_methods = []

    for root, _, files in os.walk(test_dir):
        for f in files:
            if f.endswith('Test.java'):
                path = os.path.join(root, f)
                with open(path, 'r', encoding='utf-8') as file:
                    content = file.read()
                    
                    pkg_match = re.search(r'package\s+([\w\.]+);', content)
                    pkg = pkg_match.group(1) if pkg_match else ''
                    
                    cls_match = re.search(r'class\s+([A-Za-z0-9_]+)', content)
                    if not cls_match:
                        continue
                    cls = cls_match.group(1)
                    
                    methods = re.findall(r'@Test[\s\S]*?void\s+([A-Za-z0-9_]+)\s*\(', content)
                    
                    for m in methods:
                        if pkg:
                            test_methods.append(f"{pkg}.{cls}.{m}")
                        else:
                            test_methods.append(f"{cls}.{m}")

    test_methods.sort()
    
    # We want up to 15 groups
    num_groups = min(15, len(test_methods))
    
    if num_groups == 0:
        matrix_output = [{"group": 1, "args": ""}]
    else:
        chunks = chunk_list(test_methods, num_groups)
        matrix_output = []
        for i, chunk in enumerate(chunks):
            args = " ".join([f"--tests {m}" for m in chunk])
            matrix_output.append({"group": i + 1, "args": args})

    output_json = json.dumps(matrix_output)
    
    github_output = os.environ.get("GITHUB_OUTPUT")
    if github_output:
        with open(github_output, "a") as f:
            f.write(f"matrix={output_json}\n")
    else:
        print(output_json)

if __name__ == '__main__':
    main()
