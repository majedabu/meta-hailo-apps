# Customization on top of the base onnxruntime recipe from meta-imx.
# Disables unit tests and installs the C/C++ API headers.

# Disable unit tests to reduce build time
EXTRA_OECMAKE += "-Donnxruntime_BUILD_UNIT_TESTS=OFF"

ORT_TESTS = "\
    onnxruntime_mlas_test \
    onnxruntime_global_thread_pools_test \
    onnxruntime_shared_lib_test \
    testdata \
"

# Create dummy files for hardcoded installation steps in the meta-imx recipe
do_install:prepend() {
    touch ${B}/onnxruntime_perf_test
    touch ${B}/onnxruntime_test_all
    touch ${B}/libcustom_op_library.so
    for test in ${ORT_TESTS}; do
        touch ${B}/${test}
    done
}

do_install:append() {
    # Remove dummy test binaries
    rm -f ${D}${bindir}/onnxruntime_perf_test
    rm -f ${D}${bindir}/onnxruntime_test_all

    # Remove dummy sample library
    rm -f ${D}${libdir}/libcustom_op_library.so
    for test in ${ORT_TESTS}; do
        rm -f ${D}${libdir}/${test}
    done

    # Install C/C++ API headers
    install -d ${D}${includedir}/onnxruntime/core/session
    install -m 0644 ${S}/include/onnxruntime/core/session/*.h \
        ${D}${includedir}/onnxruntime/core/session/
}
