{
  "targets": [
    {
      "target_name": "webrtc_native",
      "cflags!": [ "-fno-exceptions" ],
      "cflags_cc!": [ "-fno-exceptions" ],
      "sources": [
        "src/webrtc_bindings.cpp",
        "../../src/nativeInterop/cinterop/webrtc_wrapper.c"
      ],
      "include_dirs": [
        "<!@(node -p \"require('node-addon-api').include\")",
        "../../src/nativeInterop/cinterop"
      ],
      "defines": [ "NAPI_DISABLE_CPP_EXCEPTIONS" ]
    }
  ]
}
