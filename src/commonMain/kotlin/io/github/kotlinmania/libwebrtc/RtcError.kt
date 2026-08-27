// port-lint: source lib.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.libwebrtc

import kotlin.native.HiddenFromObjC

// Upstream formats this error as the error type followed by the raw message.
// Kotlin composes that formatted message eagerly at construction time.
@HiddenFromObjC
public class RtcError(
    public val errorType: RtcErrorType,
    public val rtcMessage: String,
) : RuntimeException("an RtcError occured: $errorType - $rtcMessage") {
    // Upstream field is named message; Kotlin's Throwable already owns
    // a nullable `message`, so the upstream field is exposed under
    // `rtcMessage` to avoid shadowing the standard exception accessor while
    // still surfacing the raw, unformatted text.
}
