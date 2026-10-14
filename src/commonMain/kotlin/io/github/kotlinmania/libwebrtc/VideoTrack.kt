// port-lint: source video_track.rs
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

import io.github.kotlinmania.libwebrtc.native.NativeMediaStreamTrackHandle
import io.github.kotlinmania.libwebrtc.native.RtcVideoTrack as ImpRtcVideoTrack

public class RtcVideoTrack internal constructor(
    internal val handle: ImpRtcVideoTrack,
) {
    public constructor(
        id: String,
        enabled: Boolean = true,
        state: RtcTrackState = RtcTrackState.Live,
    ) : this(
        handle = ImpRtcVideoTrack(trackId = id, trackEnabled = enabled, trackState = state),
    )

    internal val id: String
        get() = handle.id()

    public val kind: String
        get() = "video"

    internal val enabled: Boolean
        get() = handle.enabled()

    internal val state: RtcTrackState
        get() = handle.state()

    public fun id(): String = handle.id()

    public fun enabled(): Boolean = handle.enabled()

    public fun setEnabled(enabled: Boolean): Boolean = handle.setEnabled(enabled)

    public fun state(): RtcTrackState = handle.state()

    internal fun sysHandle(): NativeMediaStreamTrackHandle = handle.sysHandle()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RtcVideoTrack) return false
        return handle.id() == other.handle.id()
    }

    override fun hashCode(): Int = handle.id().hashCode()

    override fun toString(): String =
        "RtcVideoTrack(id=${id()}, enabled=${enabled()}, state=${state()})"
}
