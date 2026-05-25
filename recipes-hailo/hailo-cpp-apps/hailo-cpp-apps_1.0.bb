SUMMARY = "Hailo C++ standalone inference applications for Hailo-8 / Astrial (IMX8MP)"
DESCRIPTION = "Collection of standalone C++ inference apps using the HailoRT API."
HOMEPAGE = "https://github.com/hailocs/hailo-apps-internal"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ec44867c99de13393426600248d2bedd"

SRC_URI = "git://github.com/hailocs/hailo-apps-internal.git;protocol=https;branch=feature/cpp-apps-astrial-imx8;submodules=1"
SRC_URI += "file://fix-cmake-xtensor.py"

SRCREV = "${AUTOREV}"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS = " \
    cmake-native \
    libhailort \
    opencv \
    yaml-cpp \
    curl \
    xtl \
    xtensor \
    zlib \
    onnxruntime \
"

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

inherit pkgconfig cmake

EXTRA_OECMAKE += "-DONNXRUNTIME_DIR=${STAGING_DIR_TARGET}/usr"

do_configure:prepend() {
    python3 ${WORKDIR}/fix-cmake-xtensor.py ${S}
}

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
        install -m 0755 ${B}/${app}/${app} ${D}/hailo-apps/${app}/
        if [ -d ${B}/${app}/config ]; then
            cp -r ${B}/${app}/config ${D}/hailo-apps/${app}/config
        fi
    done
}

FILES_${PN} = "/hailo-apps"
INSANE_SKIP_${PN} = "dev-so ldflags"
