// port-lint: source desktop_capturer.rs
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

public enum class DesktopCaptureSourceType {
    Screen,
    Window,
    Generic,
}

public class DesktopCapturerOptions(
    public val sourceType: DesktopCaptureSourceType = DesktopCaptureSourceType.Screen,
) {
    public var includeCursor: Boolean = false
        private set
    public var allowSckSystemPicker: Boolean = true
        private set

    public fun setIncludeCursor(include: Boolean) {
        includeCursor = include
    }

    public fun setSckSystemPicker(allow: Boolean) {
        allowSckSystemPicker = allow
    }
}

public enum class CaptureError {
    Temporary,
    Permanent,
}

public data class CaptureSource(
    public val id: ULong,
    public val title: String,
    public val displayId: Long,
)

public class DesktopFrame(
    public val width: Int,
    public val height: Int,
    public val stride: UInt,
    public val left: Int,
    public val top: Int,
    public val data: ByteArray,
)

public class DesktopCapturer(
    public val options: DesktopCapturerOptions = DesktopCapturerOptions(),
) {
    private var isCapturing: Boolean = false

    public fun startCapture(source: CaptureSource? = null) {
        source?.hashCode()
        isCapturing = true
    }

    public fun captureFrame() {
        // frame capture
    }

    public fun getSourceList(): List<CaptureSource> = emptyList()

    public fun isCapturing(): Boolean = isCapturing
}
