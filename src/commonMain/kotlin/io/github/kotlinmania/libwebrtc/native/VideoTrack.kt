// port-lint: source native/video_track.rs
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

import io.github.kotlinmania.libwebrtc.RtcTrackState

internal class RtcVideoTrack(
    private val trackId: String,
    trackEnabled: Boolean = true,
    trackState: RtcTrackState = RtcTrackState.Live,
    factoryHandle: Long = 0L,
    sourceHandle: Long = 0L,
) {
    internal val nativeHandle: Long = WebrtcNativeBridge.createVideoTrack(factoryHandle, trackId, sourceHandle)

    init {
        if (!trackEnabled) {
            WebrtcNativeBridge.trackSetEnabled(nativeHandle, false)
        }
        if (trackState != RtcTrackState.Live) {
            WebrtcNativeBridge.trackSetState(nativeHandle, trackState.ordinal)
        }
    }

    internal fun id(): String = trackId

    internal fun enabled(): Boolean = WebrtcNativeBridge.trackEnabled(nativeHandle)

    internal fun setEnabled(enabled: Boolean): Boolean =
        WebrtcNativeBridge.trackSetEnabled(nativeHandle, enabled)

    internal fun state(): RtcTrackState =
        if (WebrtcNativeBridge.trackState(nativeHandle) == 0) RtcTrackState.Live else RtcTrackState.Ended

    internal fun setState(newState: RtcTrackState) {
        WebrtcNativeBridge.trackSetState(nativeHandle, newState.ordinal)
    }

    internal fun sysHandle(): NativeMediaStreamTrackHandle =
        NativeMediaStreamTrackHandle(
            id = trackId,
            kind = MEDIA_TYPE_VIDEO,
            videoTrack = this,
        )
}
