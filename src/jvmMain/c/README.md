# `src/jvmMain/c/` — JVM JNI C bindings

Contains `webrtc_jni.c`, compiled into `libwebrtc_jni.dylib` / `libwebrtc_jni.so` / `webrtc_jni.dll` by the `compileWebrtcJni` Gradle task and loaded at runtime by `io.github.kotlinmania.libwebrtc.internal.WebrtcJni`.
