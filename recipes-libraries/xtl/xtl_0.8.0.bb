SUMMARY = "xtl: Basic tools (containers, algorithms) used by xtensor"
HOMEPAGE = "https://github.com/xtensor-stack/xtl"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c12cbcb0f50cce3b0c58db4e3db8c2da"

SRC_URI = "git://github.com/xtensor-stack/xtl.git;protocol=https;branch=master"
SRCREV = "ef84e9f27020ad54961c99acd293d80d4b775dd3"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${includedir}/xtl
    cp -r ${S}/include/xtl/* ${D}${includedir}/xtl
}

FILES:${PN} += "${includedir}/xtl/*"
