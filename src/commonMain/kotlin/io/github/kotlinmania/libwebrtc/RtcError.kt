// port-lint: source lib.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.libwebrtc

import kotlin.native.HiddenFromObjC

// Upstream formats this error as the error type followed by the raw message.
// Kotlin composes that formatted message eagerly at construction time.
@HiddenFromObjC
public data class RtcError(
    public val errorType: RtcErrorType,
    public val rtcMessage: String,
) : RuntimeException("an RtcError occured: $errorType - $rtcMessage")
