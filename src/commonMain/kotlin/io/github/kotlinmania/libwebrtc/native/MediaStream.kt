// port-lint: source native/media_stream.rs
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

internal class MediaStream(
    private val streamId: String,
    private val audioTrackHandles: MutableList<RtcAudioTrack> = mutableListOf(),
    private val videoTrackHandles: MutableList<RtcVideoTrack> = mutableListOf(),
) {
    internal fun id(): String = streamId

    internal fun audioTracks(): List<io.github.kotlinmania.libwebrtc.RtcAudioTrack> =
        audioTrackHandles.map {
            io.github.kotlinmania.libwebrtc
                .RtcAudioTrack(handle = it)
        }

    internal fun videoTracks(): List<io.github.kotlinmania.libwebrtc.RtcVideoTrack> =
        videoTrackHandles.map {
            io.github.kotlinmania.libwebrtc
                .RtcVideoTrack(handle = it)
        }

    internal fun addAudioTrack(track: RtcAudioTrack) {
        audioTrackHandles.add(track)
    }

    internal fun addVideoTrack(track: RtcVideoTrack) {
        videoTrackHandles.add(track)
    }

    internal fun removeAudioTrack(track: RtcAudioTrack) {
        audioTrackHandles.removeAll { it.id() == track.id() }
    }

    internal fun removeVideoTrack(track: RtcVideoTrack) {
        videoTrackHandles.removeAll { it.id() == track.id() }
    }
}
