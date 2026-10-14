// port-lint: source native/mod.rs
// Copyright 2025 LiveKit, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.MediaType
import io.github.kotlinmania.libwebrtc.RtcError
import io.github.kotlinmania.libwebrtc.RtcErrorType

internal expect val isWebPlatform: Boolean

internal expect val isNativePlatform: Boolean

internal expect val isAndroidPlatform: Boolean

internal expect fun platformInitializeAndroid(vm: Any?)

internal enum class NativeRtcErrorType {
    Internal,
    InvalidSdp,
    InvalidState,
    ;

    internal companion object {
        internal fun from(value: NativeRtcErrorType): RtcErrorType =
            when (value) {
                InvalidState -> RtcErrorType.InvalidState
                else -> RtcErrorType.Internal
            }
    }
}

internal class NativeRtcError(
    internal val errorType: NativeRtcErrorType,
    internal val message: String,
) {
    internal companion object {
        internal fun from(value: NativeRtcError): RtcError =
            RtcError(
                errorType = NativeRtcErrorType.from(value.errorType),
                rtcMessage = value.message,
            )
    }
}

internal enum class NativeMediaType {
    Audio,
    Video,
    Data,
    Unsupported,
    ;

    internal companion object {
        internal fun from(value: MediaType): NativeMediaType =
            when (value) {
                MediaType.Audio -> Audio
                MediaType.Video -> Video
                MediaType.Data -> Data
                MediaType.Unsupported -> Unsupported
            }
    }
}
