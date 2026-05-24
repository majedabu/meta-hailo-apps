SUMMARY = "Hailo C++ standalone inference applications for Hailo-8 / Astrial (IMX8MP)"
DESCRIPTION = "Collection of standalone C++ inference apps using the HailoRT API. \
               Supports object detection, instance segmentation, classification, \
               pose estimation, semantic segmentation, depth estimation, and \
               ONNX Runtime postprocessing."
HOMEPAGE = "https://github.com/hailocs/hailo-apps-internal"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9b6d1545b0dc7e89c4f09e42bbd7e0f3"

# ---------------------------------------------------------------------------
# Source
# ---------------------------------------------------------------------------
# submodules=1 fetches yaml-cpp and curl bundled under hailo_apps/cpp/external/
SRC_URI = "git://github.com/hailocs/hailo-apps-internal.git;protocol=https;branch=feature/cpp-apps-astrial-imx8;submodules=1"

# Pin to a fixed commit for reproducible builds.
# Use AUTOREV only during active development.
SRCREV = "${AUTOREV}"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

# ---------------------------------------------------------------------------
# Dependencies
# ---------------------------------------------------------------------------

# Build-time: headers and libraries needed to compile
DEPENDS = " \
    libhailort \
    opencv \
    yaml-cpp \
    curl \
    xtensor \
    onnxruntime \
"

# Runtime: packages that must be present on the board for the apps to run
RDEPENDS:${PN} = " \
    libhailort \
    opencv \
    yaml-cpp \
    curl \
    onnxruntime \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-libav \
"

# ---------------------------------------------------------------------------
# Apps to build
# ---------------------------------------------------------------------------
HAILO_APPS = " \
    object_detection \
    instance_segmentation \
    classification \
    semantic_segmentation \
    pose_estimation \
    oriented_object_detection \
    depth_estimation_mono \
    depth_estimation_stereo \
    onnxrt_hailo_pipeline \
    zero_shot_classification \
"

inherit pkgconfig

# ---------------------------------------------------------------------------
# Build — one CMake invocation per app
# ---------------------------------------------------------------------------
do_configure() {
    for app in ${HAILO_APPS}; do
        bbnote "Configuring ${app}"
        cmake -S ${S}/hailo_apps/cpp/${app} \
              -B ${B}/${app} \
              -DCMAKE_BUILD_TYPE=Release \
              -DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/toolchain.cmake \
              ${EXTRA_OECMAKE}
    done
}

do_compile() {
    for app in ${HAILO_APPS}; do
        bbnote "Building ${app}"
        cmake --build ${B}/${app} -- ${PARALLEL_MAKE}
    done
}

do_install() {
    for app in ${HAILO_APPS}; do
        install -d ${D}/hailo-apps/${app}

        # Install binary
        install -m 0755 ${B}/${app}/${app} ${D}/hailo-apps/${app}/

        # Install config directory (resources_config.yaml, visualization_config.yaml)
        if [ -d ${B}/${app}/config ]; then
            cp -r ${B}/${app}/config ${D}/hailo-apps/${app}/config
        fi
    done
}

# ---------------------------------------------------------------------------
# Package
# ---------------------------------------------------------------------------
FILES_${PN} = "/hailo-apps"

INSANE_SKIP_${PN} = "dev-so ldflags"
