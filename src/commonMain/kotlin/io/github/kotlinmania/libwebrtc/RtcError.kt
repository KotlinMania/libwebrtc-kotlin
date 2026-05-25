// port-lint: source lib.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.libwebrtc

import kotlin.native.HiddenFromObjC

// Upstream derives thiserror's #[error("an RtcError occured: {error_type:?} - {message}")].
// In Kotlin the formatted message is composed eagerly at construction time.
@HiddenFromObjC
public class RtcError(
    public val errorType: RtcErrorType,
    public val rtcMessage: String,
) : RuntimeException("an RtcError occured: $errorType - $rtcMessage") {
    // Upstream Rust field is named `message`; Kotlin's Throwable already owns
    // a nullable `message`, so the upstream field is exposed under
    // `rtcMessage` to avoid shadowing the standard exception accessor while
    // still surfacing the raw, unformatted text.
}
