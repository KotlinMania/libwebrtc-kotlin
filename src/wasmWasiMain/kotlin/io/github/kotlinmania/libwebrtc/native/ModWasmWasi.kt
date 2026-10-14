// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc.native

internal actual val isWebPlatform: Boolean = true

internal actual val isNativePlatform: Boolean = false

internal actual val isAndroidPlatform: Boolean = false

internal actual fun platformInitializeAndroid(vm: Any?) {
    // WasmWasi platform no-op.
}
