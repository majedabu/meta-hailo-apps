"""
Yocto build fixes for hailo-apps-internal (feature/cpp-apps-astrial-imx8):

1. Fix xtensor include paths: xtensor 0.26 uses subdirs (views/, containers/,
   misc/...) but sysroot has xtensor 0.24 which uses flat layout (xtensor/xview.hpp).

2. Remove FetchContent / ExternalProject download calls — headers come from
   xtl/xtensor sysroot packages (declared in DEPENDS).
"""
import pathlib, re, sys

SRC = pathlib.Path(sys.argv[1])

# -----------------------------------------------------------------------
# 1. Fix include paths in C++ source/header files
# -----------------------------------------------------------------------
SUBDIR_RE = re.compile(
    r'(#include\s*[<"])xtensor/'
    r'(?:containers|views|io|core|misc|generators|xgenerators)/'
)
CPP_GLOBS = [
    'hailo_apps/cpp/common/general/*.hpp',
    'hailo_apps/cpp/instance_segmentation/*.hpp',
    'hailo_apps/cpp/pose_estimation/*.hpp',
    'hailo_apps/cpp/pose_estimation/*.cpp',
    'hailo_apps/cpp/onnxrt_hailo_pipeline/*.hpp',
    'hailo_apps/cpp/onnxrt_hailo_pipeline/*.cpp',
]

for pattern in CPP_GLOBS:
    for f in SRC.glob(pattern):
        original = f.read_text()
        fixed = SUBDIR_RE.sub(r'\1xtensor/', original)
        if fixed != original:
            f.write_text(fixed)
            print(f"Fixed includes: {f.relative_to(SRC)}")

# -----------------------------------------------------------------------
# 2. instance_segmentation/CMakeLists.txt — remove xtl/xtensor cmake block
# -----------------------------------------------------------------------
cmake_inst = SRC / 'hailo_apps/cpp/instance_segmentation/CMakeLists.txt'
t = cmake_inst.read_text()

# Remove the whole if(HAILO_USE_SYSTEM_DEPS) ... endif() block
t = re.sub(
    r'# ={50,}\n# xtl \+ xtensor \(header-only\).*?endif\(\)\n',
    '',
    t, flags=re.DOTALL, count=1
)

# Remove the if(USE_SYSTEM_XTENSOR) ... endif() block (target_link / include)
t = re.sub(
    r'# xtensor include path\nif\(USE_SYSTEM_XTENSOR\).*?endif\(\)\n',
    '',
    t, flags=re.DOTALL, count=1
)

cmake_inst.write_text(t)
print("Patched instance_segmentation/CMakeLists.txt")

# -----------------------------------------------------------------------
# 3. onnxrt_hailo_pipeline/CMakeLists.txt — remove ExternalProject block
# -----------------------------------------------------------------------
cmake_onnx = SRC / 'hailo_apps/cpp/onnxrt_hailo_pipeline/CMakeLists.txt'
t = cmake_onnx.read_text()

# Remove ExternalProject section
t = re.sub(
    r'# ={50,}\n# xtl \+ xtensor \(header-only\).*?set\(XTENSOR_INCLUDE_DIR[^\n]*\)\n',
    '',
    t, flags=re.DOTALL, count=1
)

# Remove add_dependencies line
t = re.sub(r'add_dependencies\(\$\{PROJECT_NAME\} xtl-test xtensor-test\)\n\n?', '', t)

# Remove XTENSOR_INCLUDE_DIR and external/include from target_include_directories
t = re.sub(r'    \$\{XTENSOR_INCLUDE_DIR\}\n', '', t)
t = re.sub(r'    \$\{CMAKE_BINARY_DIR\}/external/include\n', '', t)

cmake_onnx.write_text(t)
print("Patched onnxrt_hailo_pipeline/CMakeLists.txt")
