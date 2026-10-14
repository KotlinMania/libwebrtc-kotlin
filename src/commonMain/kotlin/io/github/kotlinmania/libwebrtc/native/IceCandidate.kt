// port-lint: source native/ice_candidate.rs
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

internal class IceCandidate(
    private val mid: String,
    private val mlineIndex: Int,
    private val candidateSdp: String,
) {
    internal fun sdpMid(): String = mid

    internal fun sdpMlineIndex(): Int = mlineIndex

    internal fun candidate(): String = candidateSdp

    override fun toString(): String = candidateSdp

    internal companion object {
        internal fun parse(
            sdpMid: String,
            sdpMlineIndex: Int,
            sdp: String,
        ): io.github.kotlinmania.libwebrtc.IceCandidate {
            if (sdp.isBlank()) {
                throw SdpParseError(line = sdp, description = "ICE candidate string cannot be blank")
            }
            return io.github.kotlinmania.libwebrtc.IceCandidate(
                handle =
                    IceCandidate(
                        mid = sdpMid,
                        mlineIndex = sdpMlineIndex,
                        candidateSdp = sdp,
                    ),
            )
        }
    }
}
