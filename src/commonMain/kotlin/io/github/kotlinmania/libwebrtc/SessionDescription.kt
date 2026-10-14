// port-lint: source session_description.rs
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

import io.github.kotlinmania.libwebrtc.native.SessionDescription as ImpSessionDescription

public enum class SdpType(
    public val value: String,
) {
    Offer("offer"),
    PrAnswer("pranswer"),
    Answer("answer"),
    Rollback("rollback"),
    ;

    override fun toString(): String = value

    public companion object {
        public fun fromStr(sdpType: String): SdpType =
            when (sdpType.lowercase()) {
                "offer" -> Offer
                "pranswer" -> PrAnswer
                "answer" -> Answer
                "rollback" -> Rollback
                else -> throw IllegalArgumentException("invalid SdpType: $sdpType")
            }

        public fun fromString(sdpType: String): SdpType = fromStr(sdpType)
    }
}

public class SdpParseError(
    public val line: String,
    public val description: String,
) : RuntimeException("Failed to parse sdp: $line - $description") {
    public val errorDescription: String
        get() = description
}

public class SessionDescription internal constructor(
    internal val handle: ImpSessionDescription,
) {
    public constructor(
        sdp: String,
        sdpType: SdpType,
    ) : this(handle = ImpSessionDescription(sdp = sdp, type = sdpType))

    public val sdp: String
        get() = handle.sdp

    internal val sdpType: SdpType
        get() = handle.sdpType()

    public fun sdpType(): SdpType = handle.sdpType()

    override fun toString(): String = handle.toString()

    public companion object {
        public fun parse(
            sdp: String,
            sdpType: SdpType,
        ): SessionDescription = ImpSessionDescription.parse(sdp, sdpType)
    }
}
