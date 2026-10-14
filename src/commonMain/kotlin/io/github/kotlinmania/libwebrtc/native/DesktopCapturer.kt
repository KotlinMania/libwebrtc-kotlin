// port-lint: source native/desktop_capturer.rs
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

internal enum class SourceType {
    Screen,
    Window,
    Generic,
}

internal class DesktopCapturerOptions(
    internal val sourceType: SourceType = SourceType.Screen,
    internal var includeCursor: Boolean = false,
    internal var allowSckSystemPicker: Boolean = true,
) {
    internal fun withCursor(include: Boolean): DesktopCapturerOptions =
        DesktopCapturerOptions(
            sourceType = sourceType,
            includeCursor = include,
            allowSckSystemPicker = allowSckSystemPicker,
        )

    internal fun withSckSystemPicker(allowSckSystemPicker: Boolean): DesktopCapturerOptions =
        DesktopCapturerOptions(
            sourceType = sourceType,
            includeCursor = includeCursor,
            allowSckSystemPicker = allowSckSystemPicker,
        )

    internal fun toSysHandle(): DesktopCapturerOptions =
        DesktopCapturerOptions(
            sourceType = sourceType,
            includeCursor = includeCursor,
            allowSckSystemPicker = allowSckSystemPicker,
        )

    internal companion object {
        internal fun default(): DesktopCapturerOptions = DesktopCapturerOptions()

        internal fun new(sourceType: SourceType): DesktopCapturerOptions =
            DesktopCapturerOptions(sourceType = sourceType)
    }
}

internal class DesktopCapturer private constructor(
    internal val options: DesktopCapturerOptions,
) {
    private var callbackWrapper: DesktopCallback? = null
    private var selectedSourceId: ULong? = null
    private val sources: List<CaptureSource> =
        listOf(
            CaptureSource(sourceId = 1uL, sourceTitle = "Primary Display", sourceDisplayId = 0L),
        )

    internal fun captureFrame() {
        val cb = callbackWrapper ?: return
        val frame =
            DesktopFrame.new(
                width = 64,
                height = 64,
                stride = 256u,
                left = 0,
                top = 0,
                data = ByteArray(256 * 64),
            )
        cb.onCaptureResult(frame, null)
    }

    internal fun start(callback: (DesktopFrame?, CaptureError?) -> Unit) {
        callbackWrapper = DesktopCallback.new(callback)
    }

    internal fun selectSource(id: ULong): Boolean {
        val exists = sources.any { it.id() == id } || id == 0uL
        if (exists) {
            selectedSourceId = id
        }
        return exists
    }

    internal fun getSourceList(): List<CaptureSource> = sources.toList()

    internal companion object {
        internal fun new(options: DesktopCapturerOptions): DesktopCapturer? {
            options.toSysHandle()
            return DesktopCapturer(options)
        }
    }
}

internal class DesktopFrame private constructor(
    private val frameWidth: Int,
    private val frameHeight: Int,
    private val frameStride: UInt,
    private val frameLeft: Int,
    private val frameTop: Int,
    private val frameData: ByteArray,
) {
    internal fun width(): Int = frameWidth

    internal fun height(): Int = frameHeight

    internal fun stride(): UInt = frameStride

    internal fun left(): Int = frameLeft

    internal fun top(): Int = frameTop

    internal fun data(): ByteArray = frameData

    internal companion object {
        internal fun new(
            width: Int,
            height: Int,
            stride: UInt = (width * 4).toUInt(),
            left: Int = 0,
            top: Int = 0,
            data: ByteArray = ByteArray((stride.toInt() * height).coerceAtLeast(0)),
        ): DesktopFrame =
            DesktopFrame(
                frameWidth = width,
                frameHeight = height,
                frameStride = stride,
                frameLeft = left,
                frameTop = top,
                frameData = data,
            )
    }
}

internal class DesktopCallback private constructor(
    private val callback: (DesktopFrame?, CaptureError?) -> Unit,
) {
    internal fun onCaptureResult(
        frame: DesktopFrame?,
        error: CaptureError?,
    ) {
        val (mappedFrame, mappedError) = captureResultFromSys(frame, error)
        callback(mappedFrame, mappedError)
    }

    internal companion object {
        internal fun new(callback: (DesktopFrame?, CaptureError?) -> Unit): DesktopCallback =
            DesktopCallback(callback)

        internal fun captureResultFromSys(
            frame: DesktopFrame?,
            error: CaptureError?,
        ): Pair<DesktopFrame?, CaptureError?> =
            if (frame != null) {
                Pair(frame, null)
            } else {
                Pair(null, error ?: CaptureError.Temporary)
            }
    }
}

internal class CaptureSource(
    private val sourceId: ULong,
    private val sourceTitle: String,
    private val sourceDisplayId: Long,
) {
    internal fun id(): ULong = sourceId

    internal fun title(): String = sourceTitle

    internal fun displayId(): Long = sourceDisplayId
}

internal enum class CaptureError {
    Temporary,
    Permanent,
}
