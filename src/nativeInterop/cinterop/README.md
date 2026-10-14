# Native C Interop (`project.ffi.native`)

This directory houses the Kotlin/Native `cinterop` definition and per-target C wrapper for `libwebrtc-kotlin`, gated by `project.ffi.native=true` in `gradle.properties` with `project.ffi.name=Webrtc`.

## Files

- `Webrtc.def` — `cinterop` definition referencing `webrtc_wrapper.h` and `webrtc_wrapper.a`.
- `webrtc_wrapper.h` — C ABI declarations mirroring `webrtc-sys`, `yuv-sys`, and `soxr-sys`.
- `webrtc_wrapper.c` — C implementation compiled into `build/cinterop-targets/<konanTargetName>/webrtc_wrapper.a` by `compileWebrtcWrapperFor<Target>`. Resolves symbols dynamically from `libwebrtc` / `libyuv` / `libsoxr` when present and provides portable C ABI execution across all Kotlin/Native targets.
