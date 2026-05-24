SUMMARY = "xtensor: C++ library for numerical analysis with multi-dimensional array expressions"
HOMEPAGE = "https://github.com/xtensor-stack/xtensor"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5c67ec4d3eb9c5b7eed4c37e69571b93"

SRC_URI = "git://github.com/xtensor-stack/xtensor.git;protocol=https;branch=master"
SRCREV = "f31d415a507b84d0097436a38293df3f56906ad1"

S = "${WORKDIR}/git"

DEPENDS = "xtl"

inherit cmake

# Header-only — nothing to compile
do_compile[noexec] = "1"

FILES:${PN} += "${includedir}/xtensor/* ${datadir}/cmake/xtensor/*"
