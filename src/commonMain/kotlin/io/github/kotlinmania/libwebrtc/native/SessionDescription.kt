// port-lint: source native/session_description.rs
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

import io.github.kotlinmania.libwebrtc.SdpParseError
import io.github.kotlinmania.libwebrtc.SdpType

internal class SessionDescription(
    internal val sdp: String,
    private val type: SdpType,
) {
    internal fun sdpType(): SdpType = type

    override fun toString(): String = sdp

    internal fun clone(): SessionDescription = SessionDescription(sdp = sdp, type = type)

    internal companion object {
        internal fun parse(
            sdp: String,
            sdpType: SdpType,
        ): io.github.kotlinmania.libwebrtc.SessionDescription {
            if (sdp.isBlank()) {
                throw SdpParseError(line = "", description = "SDP string cannot be blank")
            }
            return io.github.kotlinmania.libwebrtc.SessionDescription(
                handle = SessionDescription(sdp = sdp, type = sdpType),
            )
        }
    }
}
