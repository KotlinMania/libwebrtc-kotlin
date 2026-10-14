// port-lint: source native/video_stream.rs
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

import io.github.kotlinmania.libwebrtc.RtcVideoTrack
import io.github.kotlinmania.libwebrtc.VideoFrame

internal class VideoTrackObserver(
    private val frameQueue: ArrayDeque<VideoFrame>,
    private val isClosedProvider: () -> Boolean,
) {
    internal var discardedFrames: Int = 0
        private set
    internal var lastConstraints: Any? = null
        private set

    internal fun onFrame(frame: VideoFrame) {
        if (!isClosedProvider()) {
            frameQueue.addLast(
                VideoFrame(
                    rotation = frame.rotation,
                    timestampUs = frame.timestampUs,
                    buffer = newVideoFrameBuffer(frame.buffer),
                ),
            )
        }
    }

    internal fun onDiscardedFrame() {
        discardedFrames += 1
    }

    internal fun onConstraintsChanged(constraints: Any) {
        lastConstraints = constraints
    }
}

internal class NativeVideoStream(
    private val videoTrack: RtcVideoTrack,
) {
    private val frameQueue: ArrayDeque<VideoFrame> = ArrayDeque()
    internal var closed: Boolean = false
        private set
    internal val observer: VideoTrackObserver = VideoTrackObserver(frameQueue) { closed }

    internal fun track(): RtcVideoTrack = videoTrack

    internal fun close() {
        closed = true
        frameQueue.clear()
    }

    internal fun pollNext(): VideoFrame? = frameQueue.removeFirstOrNull()

    internal companion object {
        internal fun new(videoTrack: RtcVideoTrack): NativeVideoStream =
            NativeVideoStream(videoTrack = videoTrack)
    }
}
