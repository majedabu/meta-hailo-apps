"""
Patch xtl/xtensor cmake references for Yocto offline builds.
Headers are provided by xtl/xtensor recipes in the sysroot — no
FetchContent or ExternalProject download needed.
"""
import pathlib, re, sys

def patch_instance_seg(path):
    t = path.read_text()
    # Replace the entire xtl/xtensor cmake block with a sysroot include
    old = re.compile(
        r'# ={50,}\n# xtl \+ xtensor.*?endif\(\)\n',
        re.DOTALL
    )
    new = (
        '# xtl + xtensor headers come from sysroot (xtl/xtensor in DEPENDS)\n'
        'set(USE_SYSTEM_XTENSOR ON)\n\n'
    )
    t, n = old.subn(new, t, count=1)
    if not n:
        print(f"WARNING: xtl/xtensor block not found in {path}")
        return

    # Replace target_link_libraries xtl xtensor with include_directories
    t = t.replace(
        '    target_link_libraries(${PROJECT_NAME} PRIVATE xtl xtensor)\n',
        '    target_include_directories(${PROJECT_NAME} PRIVATE\n'
        '        ${CMAKE_SYSROOT}/usr/include)\n'
    )
    path.write_text(t)
    print(f"Patched {path}")

def patch_onnxrt(path):
    t = path.read_text()

    # Remove ExternalProject block (from comment header to XTENSOR_INCLUDE_DIR line)
    old = re.compile(
        r'# ={50,}\n# xtl \+ xtensor \(header-only\).*?set\(XTENSOR_INCLUDE_DIR[^\n]*\)\n',
        re.DOTALL
    )
    new = (
        '# xtl + xtensor headers come from sysroot (xtl/xtensor in DEPENDS)\n\n'
    )
    t, n = old.subn(new, t, count=1)
    if not n:
        print(f"WARNING: ExternalProject block not found in {path}")

    # Remove add_dependencies line
    t = re.sub(r'add_dependencies\(\$\{PROJECT_NAME\} xtl-test xtensor-test\)\n\n?', '', t)

    # Remove XTENSOR_INCLUDE_DIR and external/include from include dirs
    t = re.sub(r'    \$\{XTENSOR_INCLUDE_DIR\}\n', '', t)
    t = re.sub(r'    \$\{CMAKE_BINARY_DIR\}/external/include\n', '', t)

    path.write_text(t)
    print(f"Patched {path}")

src = pathlib.Path(sys.argv[1])
patch_instance_seg(src / 'hailo_apps/cpp/instance_segmentation/CMakeLists.txt')
patch_onnxrt(src / 'hailo_apps/cpp/onnxrt_hailo_pipeline/CMakeLists.txt')
