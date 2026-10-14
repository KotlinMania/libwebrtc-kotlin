// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc.native

internal actual val isWebPlatform: Boolean = false

internal actual val isNativePlatform: Boolean = false

internal actual val isAndroidPlatform: Boolean = true

internal actual fun platformInitializeAndroid(vm: Any?) {
    // Android JVM runtime initialization hook.
}
