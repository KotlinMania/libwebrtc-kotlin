// port-lint: source ice_candidate.rs
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

import io.github.kotlinmania.libwebrtc.native.IceCandidate as ImpIceCandidate

public class IceCandidate internal constructor(
    internal val handle: ImpIceCandidate,
) {
    public constructor(
        sdpMid: String,
        sdpMLineIndex: Int,
        candidate: String,
    ) : this(
        handle = ImpIceCandidate(mid = sdpMid, mlineIndex = sdpMLineIndex, candidateSdp = candidate),
    )

    public val sdpMid: String
        get() = handle.sdpMid()

    public val sdpMLineIndex: Int
        get() = handle.sdpMlineIndex()

    public val candidate: String
        get() = handle.candidate()

    public fun sdpMid(): String = handle.sdpMid()

    public fun sdpMlineIndex(): Int = handle.sdpMlineIndex()

    public fun candidate(): String = handle.candidate()

    override fun toString(): String = handle.toString()

    public companion object {
        public fun parse(
            sdpMid: String,
            sdpMlineIndex: Int,
            sdp: String,
        ): IceCandidate = ImpIceCandidate.parse(sdpMid, sdpMlineIndex, sdp)
    }
}
