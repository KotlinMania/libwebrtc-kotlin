// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc.native

internal actual val isWebPlatform: Boolean = false

internal actual val isNativePlatform: Boolean = true

internal actual val isAndroidPlatform: Boolean = false

internal actual fun platformInitializeAndroid(vm: Any?) {
    // Native platform initialization hook.
}
