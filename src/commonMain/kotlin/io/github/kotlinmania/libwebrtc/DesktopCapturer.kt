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

import io.github.kotlinmania.libwebrtc.native.CaptureError as ImpCaptureError
import io.github.kotlinmania.libwebrtc.native.CaptureSource as ImpCaptureSource
import io.github.kotlinmania.libwebrtc.native.DesktopCapturer as ImpDesktopCapturer
import io.github.kotlinmania.libwebrtc.native.DesktopCapturerOptions as ImpDesktopCapturerOptions
import io.github.kotlinmania.libwebrtc.native.DesktopFrame as ImpDesktopFrame
import io.github.kotlinmania.libwebrtc.native.SourceType as ImpSourceType

public fun interface DesktopCaptureCallback {
    public fun onCaptureResult(frame: DesktopFrame?, error: CaptureError?)
}

/**
 * The type of the desktop capture source.
 */
public enum class DesktopCaptureSourceType {
    /**
     * Screen source type.
     */
    Screen,

    /**
     * Window source type.
     */
    Window,

    /**
     * Generic source type used when screens and windows are shared together by a system portal.
     */
    Generic,
    ;

    internal fun toImp(): ImpSourceType =
        when (this) {
            Screen -> ImpSourceType.Screen
            Window -> ImpSourceType.Window
            Generic -> ImpSourceType.Generic
        }
}

/**
 * Options for the desktop capturer.
 */
public class DesktopCapturerOptions internal constructor(
    internal var imp: ImpDesktopCapturerOptions,
    public val sourceType: DesktopCaptureSourceType = DesktopCaptureSourceType.Screen,
) {
    public constructor(
        sourceType: DesktopCaptureSourceType = DesktopCaptureSourceType.Screen,
        includeCursor: Boolean = false,
        allowSckSystemPicker: Boolean = true,
    ) : this(
        imp =
            ImpDesktopCapturerOptions(
                sourceType = sourceType.toImp(),
                includeCursor = includeCursor,
                allowSckSystemPicker = allowSckSystemPicker,
            ),
        sourceType = sourceType,
    )

    public val includeCursor: Boolean
        get() = imp.includeCursor

    public val allowSckSystemPicker: Boolean
        get() = imp.allowSckSystemPicker

    /**
     * Sets whether the cursor should be included in the captured frame. Default is `false`.
     */
    public fun setIncludeCursor(include: Boolean) {
        imp = imp.withCursor(include)
    }

    /**
     * Sets whether the ScreenCaptureKit system picker is allowed on macOS. Default is `true`.
     */
    public fun setSckSystemPicker(allowSckSystemPicker: Boolean) {
        imp = imp.withSckSystemPicker(allowSckSystemPicker)
    }

    public companion object {
        /**
         * Creates a new [DesktopCapturerOptions] with the given source type and default values.
         */
        public fun new(sourceType: DesktopCaptureSourceType): DesktopCapturerOptions =
            DesktopCapturerOptions(sourceType = sourceType)
    }
}

/**
 * Captures the content of the screen or window.
 */
public class DesktopCapturer internal constructor(
    private val imp: ImpDesktopCapturer,
    public val options: DesktopCapturerOptions,
) {
    private var capturing: Boolean = false

    public constructor(options: DesktopCapturerOptions) : this(
        imp = ImpDesktopCapturer.new(options.imp)!!,
        options = options,
    )

    /**
     * Captures a single frame.
     */
    public fun captureFrame() {
        imp.captureFrame()
    }

    /**
     * Starts the capturer with the given callback.
     */
    public fun start(callback: DesktopCaptureCallback) {
        capturing = true
        imp.start { frame, err ->
            val mappedFrame = frame?.let { DesktopFrame(imp = it) }
            val mappedError =
                when (err) {
                    ImpCaptureError.Temporary -> CaptureError.Temporary
                    ImpCaptureError.Permanent -> CaptureError.Permanent
                    null -> null
                }
            callback.onCaptureResult(mappedFrame, mappedError)
        }
    }

    public fun startCapture() {
        capturing = true
    }

    public fun stopCapture() {
        capturing = false
    }

    public fun isCapturing(): Boolean = capturing

    /**
     * Selects the source to be captured. Returns `false` in case of a failure.
     */
    public fun selectSource(id: ULong): Boolean = imp.selectSource(id)

    /**
     * Gets a list of sources that can be captured. The list is empty in case of a failure.
     */
    public fun getSourceList(): List<CaptureSource> =
        imp.getSourceList().map { CaptureSource(imp = it) }

    public companion object {
        /**
         * Creates a new [DesktopCapturer] with the given options, or `null` in case of a failure.
         */
        public fun new(options: DesktopCapturerOptions): DesktopCapturer? {
            val handle = ImpDesktopCapturer.new(options.imp) ?: return null
            return DesktopCapturer(imp = handle, options = options)
        }
    }
}

/**
 * DesktopFrame represents a video frame captured from the screen.
 */
public class DesktopFrame internal constructor(
    private val imp: ImpDesktopFrame,
) {
    public constructor(
        width: Int,
        height: Int,
        stride: UInt,
        left: Int = 0,
        top: Int = 0,
        data: ByteArray = ByteArray((stride.toInt() * height).coerceAtLeast(0)),
    ) : this(
        imp =
            ImpDesktopFrame.new(
                width = width,
                height = height,
                stride = stride,
                left = left,
                top = top,
                data = data,
            ),
    )

    internal val width: Int
        get() = imp.width()

    internal val height: Int
        get() = imp.height()

    internal val stride: UInt
        get() = imp.stride()

    internal val left: Int
        get() = imp.left()

    internal val top: Int
        get() = imp.top()

    internal val data: ByteArray
        get() = imp.data()

    /**
     * Width of the frame in pixels.
     */
    public fun width(): Int = imp.width()

    /**
     * Height of the frame in pixels.
     */
    public fun height(): Int = imp.height()

    /**
     * Distance in bytes between two neighboring rows of pixels.
     */
    public fun stride(): UInt = imp.stride()

    /**
     * The X coordinate of the top-left corner of the frame in desktop coordinates.
     */
    public fun left(): Int = imp.left()

    /**
     * The Y coordinate of the top-left corner of the frame in desktop coordinates.
     */
    public fun top(): Int = imp.top()

    /**
     * Underlying buffer of the frame in `RGBA` format.
     */
    public fun data(): ByteArray = imp.data()
}

/**
 * CaptureSource represents a screen or window that can be captured.
 */
public class CaptureSource internal constructor(
    private val imp: ImpCaptureSource,
) {
    public constructor(
        id: ULong,
        title: String,
        displayId: Long = 0L,
    ) : this(
        imp = ImpCaptureSource(sourceId = id, sourceTitle = title, sourceDisplayId = displayId),
    )

    internal val id: ULong
        get() = imp.id()

    internal val title: String
        get() = imp.title()

    internal val displayId: Long
        get() = imp.displayId()

    /**
     * The identifier of the source.
     */
    public fun id(): ULong = imp.id()

    /**
     * Title of the window or screen.
     */
    public fun title(): String = imp.title()

    /**
     * The id of the display (`CGDirectDisplayID` on macOS).
     */
    public fun displayId(): Long = imp.displayId()

    override fun toString(): String =
        "CaptureSource(id=${id()}, title=${title()}, display_id=${displayId()})"
}

/**
 * An error that occurred during a desktop capture.
 */
public enum class CaptureError(
    public val errorDescription: String,
) {
    /**
     * The capturer has failed to capture a frame, but it may recover in the future.
     */
    Temporary("Desktop capturer temporary failed to capture a frame"),

    /**
     * The capturer has failed permanently and will not be able to capture any more frames.
     */
    Permanent("Desktop capturer permanently failed to capture a frame"),
}
