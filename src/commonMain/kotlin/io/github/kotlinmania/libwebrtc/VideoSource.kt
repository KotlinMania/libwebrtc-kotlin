// port-lint: source libwebrtc/libwebrtc/src/video_source.rs
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

public data class VideoResolution(
    public val width: UInt = 1280u,
    public val height: UInt = 720u,
)

public class NativeVideoSource(
    private var resolution: VideoResolution = VideoResolution(),
    public val isScreencast: Boolean = false,
) {
    public fun captureFrame(frame: VideoFrame) {
        // capture video frame
    }

    public fun videoResolution(): VideoResolution = resolution
}

public sealed class RtcVideoSource {
    public class Native(
        public val nativeSource: NativeVideoSource,
    ) : RtcVideoSource()

    public fun videoResolution(): VideoResolution =
        when (this) {
            is Native -> nativeSource.videoResolution()
        }
}
