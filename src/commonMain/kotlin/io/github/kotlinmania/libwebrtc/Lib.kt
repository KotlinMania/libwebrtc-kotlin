// port-lint: source lib.rs
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

package io.github.kotlinmania.libwebrtc

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

public enum class MediaType {
    Audio,
    Video,
    Data,
    Unsupported,
}

public enum class RtcErrorType {
    Internal,
    InvalidSdp,
    InvalidState,
}

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
public class RtcError(
    public val errorType: RtcErrorType,
    public val rtcMessage: String,
) : Exception("an RtcError occured: $errorType - $rtcMessage") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RtcError
        if (errorType != other.errorType) return false
        if (rtcMessage != other.rtcMessage) return false
        return true
    }

    override fun hashCode(): Int {
        var result = errorType.hashCode()
        result = 31 * result + rtcMessage.hashCode()
        return result
    }

    override fun toString(): String = "RtcError(errorType=$errorType, message=$rtcMessage)"
}

public object Lib
