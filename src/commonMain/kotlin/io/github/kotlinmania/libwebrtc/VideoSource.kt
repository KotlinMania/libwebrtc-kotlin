// port-lint: source video_source.rs
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

import io.github.kotlinmania.libwebrtc.native.NativeVideoSource as ImpNativeVideoSource

public class VideoResolution(
    public val width: UInt = 0u,
    public val height: UInt = 0u,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VideoResolution) return false
        return width == other.width && height == other.height
    }

    override fun hashCode(): Int = 31 * width.hashCode() + height.hashCode()

    override fun toString(): String = "VideoResolution(width=$width, height=$height)"

    public companion object {
        public fun default(): VideoResolution = VideoResolution()
    }
}

public sealed class RtcVideoSource {
    public class Native(
        public val source: NativeVideoSource,
    ) : RtcVideoSource()

    public fun videoResolution(): VideoResolution =
        when (this) {
            is Native -> source.videoResolution()
        }

    public fun captureFrame(frame: VideoFrame) {
        when (this) {
            is Native -> source.captureFrame(frame)
        }
    }
}

public class NativeVideoSource internal constructor(
    internal val handle: ImpNativeVideoSource,
) {
    public constructor(
        resolution: VideoResolution = VideoResolution(),
        isScreencast: Boolean = false,
    ) : this(
        handle = ImpNativeVideoSource.new(resolution = resolution, isScreencast = isScreencast),
    )

    public val isScreencast: Boolean
        get() = handle.isScreencast

    public fun captureFrame(frame: VideoFrame) {
        handle.captureFrame(frame)
    }

    public fun videoResolution(): VideoResolution = handle.videoResolution()

    public fun lastCapturedFrame(): VideoFrame? = handle.lastCapturedFrame()

    override fun toString(): String = "NativeVideoSource"

    public companion object {
        public fun new(
            resolution: VideoResolution,
            isScreencast: Boolean = false,
        ): NativeVideoSource =
            NativeVideoSource(
                handle = ImpNativeVideoSource.new(resolution = resolution, isScreencast = isScreencast),
            )

        public fun default(): NativeVideoSource = new(VideoResolution.default(), false)
    }
}
