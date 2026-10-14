// port-lint: source media_stream.rs
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

import io.github.kotlinmania.libwebrtc.native.MediaStream as ImpMediaStream

public class MediaStream internal constructor(
    internal val handle: ImpMediaStream,
) {
    public constructor(id: String) : this(handle = ImpMediaStream(streamId = id))

    public val id: String
        get() = handle.id()

    public fun id(): String = handle.id()

    public fun audioTracks(): List<RtcAudioTrack> = handle.audioTracks()

    public fun videoTracks(): List<RtcVideoTrack> = handle.videoTracks()

    public fun addTrack(track: RtcAudioTrack) {
        handle.addAudioTrack(track.handle)
    }

    public fun addTrack(track: RtcVideoTrack) {
        handle.addVideoTrack(track.handle)
    }

    public fun removeTrack(track: RtcAudioTrack) {
        handle.removeAudioTrack(track.handle)
    }

    public fun removeTrack(track: RtcVideoTrack) {
        handle.removeVideoTrack(track.handle)
    }

    override fun toString(): String =
        "MediaStream(id=${id()}, audioTracks=${audioTracks()}, videoTracks=${videoTracks()})"
}
