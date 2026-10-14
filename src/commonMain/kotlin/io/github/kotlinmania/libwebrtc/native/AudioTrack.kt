// port-lint: source native/audio_track.rs
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

internal class RtcAudioTrack(
    private val trackId: String,
    private var trackEnabled: Boolean = true,
    private var trackState: RtcTrackState = RtcTrackState.Live,
) {
    internal fun id(): String = trackId

    internal fun enabled(): Boolean = trackEnabled

    internal fun setEnabled(enabled: Boolean): Boolean {
        val prev = trackEnabled
        trackEnabled = enabled
        return prev
    }

    internal fun state(): RtcTrackState = trackState

    internal fun setState(newState: RtcTrackState) {
        trackState = newState
    }

    internal fun sysHandle(): NativeMediaStreamTrackHandle =
        NativeMediaStreamTrackHandle(
            id = trackId,
            kind = MEDIA_TYPE_AUDIO,
            audioTrack = this,
        )
}
