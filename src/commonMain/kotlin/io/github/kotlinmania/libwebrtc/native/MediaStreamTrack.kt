// port-lint: source native/media_stream_track.rs
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

import io.github.kotlinmania.libwebrtc.MediaStreamTrack
import io.github.kotlinmania.libwebrtc.RtcTrackState

internal const val MEDIA_TYPE_AUDIO: String = "audio"
internal const val MEDIA_TYPE_VIDEO: String = "video"

internal enum class NativeTrackState {
    Live,
    Ended,
    ;

    internal fun toRtcTrackState(): RtcTrackState =
        when (this) {
            Live -> RtcTrackState.Live
            Ended -> RtcTrackState.Ended
        }
}

internal class NativeMediaStreamTrackHandle(
    internal val id: String,
    internal val kind: String,
    internal val audioTrack: RtcAudioTrack? = null,
    internal val videoTrack: RtcVideoTrack? = null,
)

internal fun newMediaStreamTrack(sysHandle: NativeMediaStreamTrackHandle): MediaStreamTrack =
    when (sysHandle.kind) {
        MEDIA_TYPE_AUDIO -> {
            val handle = sysHandle.audioTrack ?: RtcAudioTrack(sysHandle.id)
            MediaStreamTrack.Audio(io.github.kotlinmania.libwebrtc.RtcAudioTrack(handle = handle))
        }
        MEDIA_TYPE_VIDEO -> {
            val handle = sysHandle.videoTrack ?: RtcVideoTrack(sysHandle.id)
            MediaStreamTrack.Video(io.github.kotlinmania.libwebrtc.RtcVideoTrack(handle = handle))
        }
        else -> error("unknown track kind: ${sysHandle.kind}")
    }
