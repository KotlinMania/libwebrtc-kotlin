// port-lint: source video_stream.rs
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

import io.github.kotlinmania.libwebrtc.native.NativeVideoStream as ImpNativeVideoStream

public class NativeVideoStream internal constructor(
    internal val handle: ImpNativeVideoStream,
) {
    public constructor(videoTrack: RtcVideoTrack) : this(
        handle = ImpNativeVideoStream.new(videoTrack),
    )

    public fun track(): RtcVideoTrack = handle.track()

    public fun close() {
        handle.close()
    }

    public fun isClosed(): Boolean = handle.closed

    public fun pollNext(): VideoFrame? = handle.pollNext()

    override fun toString(): String = "NativeVideoStream(track=${track()})"

    public companion object {
        public fun new(videoTrack: RtcVideoTrack): NativeVideoStream =
            NativeVideoStream(handle = ImpNativeVideoStream.new(videoTrack))
    }
}
