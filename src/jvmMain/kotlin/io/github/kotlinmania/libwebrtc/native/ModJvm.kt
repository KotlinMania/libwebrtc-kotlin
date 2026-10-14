// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc.native

internal actual val isWebPlatform: Boolean = false

internal actual val isNativePlatform: Boolean = false

internal actual val isAndroidPlatform: Boolean = false

internal actual fun platformInitializeAndroid(vm: Any?) {
    // No-op on standard JVM target.
}
