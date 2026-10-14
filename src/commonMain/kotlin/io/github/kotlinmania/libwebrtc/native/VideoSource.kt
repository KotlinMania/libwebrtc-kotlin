// port-lint: source native/video_source.rs
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

import io.github.kotlinmania.libwebrtc.VideoFrame
import io.github.kotlinmania.libwebrtc.VideoResolution

internal class VideoSourceInner(
    internal var capturedFrames: Int = 0,
)

internal class NativeVideoSource(
    private val resolution: VideoResolution = VideoResolution(),
    internal val isScreencast: Boolean = false,
) {
    private val inner: VideoSourceInner = VideoSourceInner()
    private var lastFrame: VideoFrame? = null

    internal fun sysHandle(): NativeVideoSource = this

    internal fun captureFrame(frame: VideoFrame) {
        inner.capturedFrames += 1
        lastFrame = frame
    }

    internal fun videoResolution(): VideoResolution = resolution

    internal fun lastCapturedFrame(): VideoFrame? = lastFrame

    internal companion object {
        internal fun new(
            resolution: VideoResolution,
            isScreencast: Boolean,
        ): NativeVideoSource =
            NativeVideoSource(
                resolution = resolution,
                isScreencast = isScreencast,
            )
    }
}
