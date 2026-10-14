// port-lint: source media_stream_track.rs
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

public enum class RtcTrackState {
    Live,
    Ended,
}

public sealed class MediaStreamTrack {
    public class Video(
        public val track: RtcVideoTrack,
    ) : MediaStreamTrack()

    public class Audio(
        public val track: RtcAudioTrack,
    ) : MediaStreamTrack()

    public val id: String
        get() = id()

    public val kind: String
        get() =
            when (this) {
                is Video -> "video"
                is Audio -> "audio"
            }

    public val enabled: Boolean
        get() = enabled()

    public val state: RtcTrackState
        get() = state()

    public fun id(): String =
        when (this) {
            is Video -> track.id()
            is Audio -> track.id()
        }

    public fun enabled(): Boolean =
        when (this) {
            is Video -> track.enabled()
            is Audio -> track.enabled()
        }

    public fun setEnabled(enabled: Boolean): Boolean =
        when (this) {
            is Video -> track.setEnabled(enabled)
            is Audio -> track.setEnabled(enabled)
        }

    public fun state(): RtcTrackState =
        when (this) {
            is Video -> track.state()
            is Audio -> track.state()
        }

    internal fun sysHandle(): NativeMediaStreamTrackHandle =
        when (this) {
            is Video -> track.sysHandle()
            is Audio -> track.sysHandle()
        }

    public companion object {
        public fun from(track: RtcAudioTrack): MediaStreamTrack = Audio(track)

        public fun from(track: RtcVideoTrack): MediaStreamTrack = Video(track)
    }
}
