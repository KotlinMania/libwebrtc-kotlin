// port-lint: source native/video_frame.rs
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

import io.github.kotlinmania.libwebrtc.VideoBuffer
import io.github.kotlinmania.libwebrtc.VideoBufferType
import io.github.kotlinmania.libwebrtc.VideoFormatType

internal fun newVideoFrameBuffer(buffer: VideoBuffer): VideoBuffer =
    when (buffer.bufferType()) {
        VideoBufferType.Native -> {
            io.github.kotlinmania.libwebrtc.NativeBuffer(
                handle = NativeBuffer(bufferWidth = buffer.width(), bufferHeight = buffer.height()),
            )
        }

        VideoBufferType.I420 -> {
            buffer
        }

        VideoBufferType.I420A -> {
            buffer
        }

        VideoBufferType.I422 -> {
            buffer
        }

        VideoBufferType.I444 -> {
            buffer
        }

        VideoBufferType.I010 -> {
            buffer
        }

        VideoBufferType.NV12 -> {
            buffer
        }

        VideoBufferType.WebGl -> {
            buffer
        }
    }

internal class NativeBuffer(
    private val bufferWidth: UInt,
    private val bufferHeight: UInt,
    private val platformImageBuffer: Any? = null,
) {
    internal fun sysHandle(): NativeBuffer = this

    internal fun width(): UInt = bufferWidth

    internal fun height(): UInt = bufferHeight

    internal fun getCvPixelBuffer(): Any? = platformImageBuffer

    internal fun toI420(): I420Buffer =
        I420Buffer(
            bufferWidth = bufferWidth,
            bufferHeight = bufferHeight,
            strideYVal = bufferWidth,
            strideUVal = (bufferWidth + 1u) / 2u,
            strideVVal = (bufferWidth + 1u) / 2u,
        )

    internal fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        toI420().toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    internal companion object {
        internal fun fromCvPixelBuffer(
            cvPixelBuffer: Any?,
            width: UInt = 0u,
            height: UInt = 0u,
        ): io.github.kotlinmania.libwebrtc.NativeBuffer =
            io.github.kotlinmania.libwebrtc.NativeBuffer(
                handle =
                    NativeBuffer(
                        bufferWidth = width,
                        bufferHeight = height,
                        platformImageBuffer = cvPixelBuffer,
                    ),
            )
    }
}

internal class I420Buffer(
    private val bufferWidth: UInt,
    private val bufferHeight: UInt,
    private val strideYVal: UInt = bufferWidth,
    private val strideUVal: UInt = (bufferWidth + 1u) / 2u,
    private val strideVVal: UInt = (bufferWidth + 1u) / 2u,
    internal val yData: ByteArray = ByteArray((strideYVal * bufferHeight).toInt()),
    internal val uData: ByteArray = ByteArray((strideUVal * ((bufferHeight + 1u) / 2u)).toInt()),
    internal val vData: ByteArray = ByteArray((strideVVal * ((bufferHeight + 1u) / 2u)).toInt()),
) {
    internal fun sysHandle(): I420Buffer = this

    internal fun width(): UInt = bufferWidth

    internal fun height(): UInt = bufferHeight

    internal fun chromaWidth(): UInt = (bufferWidth + 1u) / 2u

    internal fun chromaHeight(): UInt = (bufferHeight + 1u) / 2u

    internal fun strideY(): UInt = strideYVal

    internal fun strideU(): UInt = strideUVal

    internal fun strideV(): UInt = strideVVal

    internal fun toI420(): I420Buffer =
        I420Buffer(
            bufferWidth = bufferWidth,
            bufferHeight = bufferHeight,
            strideYVal = strideYVal,
            strideUVal = strideUVal,
            strideVVal = strideVVal,
            yData = yData.copyOf(),
            uData = uData.copyOf(),
            vData = vData.copyOf(),
        )

    internal fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        when (format) {
            VideoFormatType.ARGB -> i420ToArgb(yData, strideYVal, uData, strideUVal, vData, strideVVal, dst, dstStride, dstWidth, dstHeight)
            VideoFormatType.BGRA -> i420ToBgra(yData, strideYVal, uData, strideUVal, vData, strideVVal, dst, dstStride, dstWidth, dstHeight)
            VideoFormatType.ABGR -> i420ToAbgr(yData, strideYVal, uData, strideUVal, vData, strideVVal, dst, dstStride, dstWidth, dstHeight)
            VideoFormatType.RGBA -> i420ToRgba(yData, strideYVal, uData, strideUVal, vData, strideVVal, dst, dstStride, dstWidth, dstHeight)
        }
    }

    internal fun data(): Triple<ByteArray, ByteArray, ByteArray> = Triple(yData, uData, vData)

    internal fun scale(scaledWidth: Int, scaledHeight: Int): io.github.kotlinmania.libwebrtc.I420Buffer {
        val w = scaledWidth.coerceAtLeast(0).toUInt()
        val h = scaledHeight.coerceAtLeast(0).toUInt()
        return new(
            width = w,
            height = h,
            strideY = w,
            strideU = (w + 1u) / 2u,
            strideV = (w + 1u) / 2u,
        )
    }

    internal companion object {
        internal fun new(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): io.github.kotlinmania.libwebrtc.I420Buffer =
            io.github.kotlinmania.libwebrtc.I420Buffer(
                handle =
                    I420Buffer(
                        bufferWidth = width,
                        bufferHeight = height,
                        strideYVal = strideY,
                        strideUVal = strideU,
                        strideVVal = strideV,
                    ),
            )
    }
}

internal class I420ABuffer(
    private val bufferWidth: UInt,
    private val bufferHeight: UInt,
    private val strideYVal: UInt = bufferWidth,
    private val strideUVal: UInt = (bufferWidth + 1u) / 2u,
    private val strideVVal: UInt = (bufferWidth + 1u) / 2u,
    private val strideAVal: UInt = bufferWidth,
    internal val yData: ByteArray = ByteArray((strideYVal * bufferHeight).toInt()),
    internal val uData: ByteArray = ByteArray((strideUVal * ((bufferHeight + 1u) / 2u)).toInt()),
    internal val vData: ByteArray = ByteArray((strideVVal * ((bufferHeight + 1u) / 2u)).toInt()),
    internal val aData: ByteArray? = ByteArray((strideAVal * bufferHeight).toInt()),
) {
    internal fun sysHandle(): I420ABuffer = this

    internal fun width(): UInt = bufferWidth

    internal fun height(): UInt = bufferHeight

    internal fun chromaWidth(): UInt = (bufferWidth + 1u) / 2u

    internal fun chromaHeight(): UInt = (bufferHeight + 1u) / 2u

    internal fun strideY(): UInt = strideYVal

    internal fun strideU(): UInt = strideUVal

    internal fun strideV(): UInt = strideVVal

    internal fun strideA(): UInt = strideAVal

    internal fun toI420(): I420Buffer =
        I420Buffer(
            bufferWidth = bufferWidth,
            bufferHeight = bufferHeight,
            strideYVal = strideYVal,
            strideUVal = strideUVal,
            strideVVal = strideVVal,
            yData = yData.copyOf(),
            uData = uData.copyOf(),
            vData = vData.copyOf(),
        )

    internal fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        toI420().toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    internal fun dataY(): ByteArray = yData

    internal fun dataU(): ByteArray = uData

    internal fun dataV(): ByteArray = vData

    internal fun dataA(): ByteArray? = aData

    internal fun scale(scaledWidth: Int, scaledHeight: Int): io.github.kotlinmania.libwebrtc.I420ABuffer {
        val w = scaledWidth.coerceAtLeast(0).toUInt()
        val h = scaledHeight.coerceAtLeast(0).toUInt()
        return io.github.kotlinmania.libwebrtc.I420ABuffer(
            handle =
                I420ABuffer(
                    bufferWidth = w,
                    bufferHeight = h,
                    strideYVal = w,
                    strideUVal = (w + 1u) / 2u,
                    strideVVal = (w + 1u) / 2u,
                    strideAVal = w,
                ),
        )
    }
}

internal class I422Buffer(
    private val bufferWidth: UInt,
    private val bufferHeight: UInt,
    private val strideYVal: UInt = bufferWidth,
    private val strideUVal: UInt = (bufferWidth + 1u) / 2u,
    private val strideVVal: UInt = (bufferWidth + 1u) / 2u,
    internal val yData: ByteArray = ByteArray((strideYVal * bufferHeight).toInt()),
    internal val uData: ByteArray = ByteArray((strideUVal * bufferHeight).toInt()),
    internal val vData: ByteArray = ByteArray((strideVVal * bufferHeight).toInt()),
) {
    internal fun sysHandle(): I422Buffer = this

    internal fun width(): UInt = bufferWidth

    internal fun height(): UInt = bufferHeight

    internal fun chromaWidth(): UInt = (bufferWidth + 1u) / 2u

    internal fun chromaHeight(): UInt = bufferHeight

    internal fun strideY(): UInt = strideYVal

    internal fun strideU(): UInt = strideUVal

    internal fun strideV(): UInt = strideVVal

    internal fun toI420(): I420Buffer {
        val chromaH = (bufferHeight + 1u) / 2u
        val chromaW = (bufferWidth + 1u) / 2u
        val dstY = ByteArray((bufferWidth * bufferHeight).toInt())
        val dstU = ByteArray((chromaW * chromaH).toInt())
        val dstV = ByteArray((chromaW * chromaH).toInt())
        i422ToI420(
            yData,
            strideYVal,
            uData,
            strideUVal,
            vData,
            strideVVal,
            dstY,
            bufferWidth,
            dstU,
            chromaW,
            dstV,
            chromaW,
            bufferWidth.toInt(),
            bufferHeight.toInt(),
        )
        return I420Buffer(
            bufferWidth = bufferWidth,
            bufferHeight = bufferHeight,
            strideYVal = bufferWidth,
            strideUVal = chromaW,
            strideVVal = chromaW,
            yData = dstY,
            uData = dstU,
            vData = dstV,
        )
    }

    internal fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        toI420().toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    internal fun data(): Triple<ByteArray, ByteArray, ByteArray> = Triple(yData, uData, vData)

    internal fun scale(scaledWidth: Int, scaledHeight: Int): io.github.kotlinmania.libwebrtc.I422Buffer {
        val w = scaledWidth.coerceAtLeast(0).toUInt()
        val h = scaledHeight.coerceAtLeast(0).toUInt()
        return new(
            width = w,
            height = h,
            strideY = w,
            strideU = (w + 1u) / 2u,
            strideV = (w + 1u) / 2u,
        )
    }

    internal companion object {
        internal fun new(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): io.github.kotlinmania.libwebrtc.I422Buffer =
            io.github.kotlinmania.libwebrtc.I422Buffer(
                handle =
                    I422Buffer(
                        bufferWidth = width,
                        bufferHeight = height,
                        strideYVal = strideY,
                        strideUVal = strideU,
                        strideVVal = strideV,
                    ),
            )
    }
}

internal class I444Buffer(
    private val bufferWidth: UInt,
    private val bufferHeight: UInt,
    private val strideYVal: UInt = bufferWidth,
    private val strideUVal: UInt = bufferWidth,
    private val strideVVal: UInt = bufferWidth,
    internal val yData: ByteArray = ByteArray((strideYVal * bufferHeight).toInt()),
    internal val uData: ByteArray = ByteArray((strideUVal * bufferHeight).toInt()),
    internal val vData: ByteArray = ByteArray((strideVVal * bufferHeight).toInt()),
) {
    internal fun sysHandle(): I444Buffer = this

    internal fun width(): UInt = bufferWidth

    internal fun height(): UInt = bufferHeight

    internal fun chromaWidth(): UInt = bufferWidth

    internal fun chromaHeight(): UInt = bufferHeight

    internal fun strideY(): UInt = strideYVal

    internal fun strideU(): UInt = strideUVal

    internal fun strideV(): UInt = strideVVal

    internal fun toI420(): I420Buffer {
        val chromaH = (bufferHeight + 1u) / 2u
        val chromaW = (bufferWidth + 1u) / 2u
        val dstY = ByteArray((bufferWidth * bufferHeight).toInt())
        val dstU = ByteArray((chromaW * chromaH).toInt())
        val dstV = ByteArray((chromaW * chromaH).toInt())
        i444ToI420(
            yData,
            strideYVal,
            uData,
            strideUVal,
            vData,
            strideVVal,
            dstY,
            bufferWidth,
            dstU,
            chromaW,
            dstV,
            chromaW,
            bufferWidth.toInt(),
            bufferHeight.toInt(),
        )
        return I420Buffer(
            bufferWidth = bufferWidth,
            bufferHeight = bufferHeight,
            strideYVal = bufferWidth,
            strideUVal = chromaW,
            strideVVal = chromaW,
            yData = dstY,
            uData = dstU,
            vData = dstV,
        )
    }

    internal fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        toI420().toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    internal fun data(): Triple<ByteArray, ByteArray, ByteArray> = Triple(yData, uData, vData)

    internal fun scale(scaledWidth: Int, scaledHeight: Int): io.github.kotlinmania.libwebrtc.I444Buffer {
        val w = scaledWidth.coerceAtLeast(0).toUInt()
        val h = scaledHeight.coerceAtLeast(0).toUInt()
        return new(
            width = w,
            height = h,
            strideY = w,
            strideU = w,
            strideV = w,
        )
    }

    internal companion object {
        internal fun new(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): io.github.kotlinmania.libwebrtc.I444Buffer =
            io.github.kotlinmania.libwebrtc.I444Buffer(
                handle =
                    I444Buffer(
                        bufferWidth = width,
                        bufferHeight = height,
                        strideYVal = strideY,
                        strideUVal = strideU,
                        strideVVal = strideV,
                    ),
            )
    }
}

internal class I010Buffer(
    private val bufferWidth: UInt,
    private val bufferHeight: UInt,
    private val strideYVal: UInt = bufferWidth * 2u,
    private val strideUVal: UInt = ((bufferWidth + 1u) / 2u) * 2u,
    private val strideVVal: UInt = ((bufferWidth + 1u) / 2u) * 2u,
    internal val yData: UShortArray = UShortArray(((strideYVal * bufferHeight) / 2u).toInt()),
    internal val uData: UShortArray = UShortArray(((strideUVal * ((bufferHeight + 1u) / 2u)) / 2u).toInt()),
    internal val vData: UShortArray = UShortArray(((strideVVal * ((bufferHeight + 1u) / 2u)) / 2u).toInt()),
) {
    internal fun sysHandle(): I010Buffer = this

    internal fun width(): UInt = bufferWidth

    internal fun height(): UInt = bufferHeight

    internal fun chromaWidth(): UInt = (bufferWidth + 1u) / 2u

    internal fun chromaHeight(): UInt = (bufferHeight + 1u) / 2u

    internal fun strideY(): UInt = strideYVal

    internal fun strideU(): UInt = strideUVal

    internal fun strideV(): UInt = strideVVal

    internal fun toI420(): I420Buffer {
        val chromaH = (bufferHeight + 1u) / 2u
        val chromaW = (bufferWidth + 1u) / 2u
        val dstY = ByteArray((bufferWidth * bufferHeight).toInt())
        val dstU = ByteArray((chromaW * chromaH).toInt())
        val dstV = ByteArray((chromaW * chromaH).toInt())
        i010ToI420(
            yData,
            strideYVal,
            uData,
            strideUVal,
            vData,
            strideVVal,
            dstY,
            bufferWidth,
            dstU,
            chromaW,
            dstV,
            chromaW,
            bufferWidth.toInt(),
            bufferHeight.toInt(),
        )
        return I420Buffer(
            bufferWidth = bufferWidth,
            bufferHeight = bufferHeight,
            strideYVal = bufferWidth,
            strideUVal = chromaW,
            strideVVal = chromaW,
            yData = dstY,
            uData = dstU,
            vData = dstV,
        )
    }

    internal fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        toI420().toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    internal fun data(): Triple<UShortArray, UShortArray, UShortArray> = Triple(yData, uData, vData)

    internal fun scale(scaledWidth: Int, scaledHeight: Int): io.github.kotlinmania.libwebrtc.I010Buffer {
        val w = scaledWidth.coerceAtLeast(0).toUInt()
        val h = scaledHeight.coerceAtLeast(0).toUInt()
        return new(
            width = w,
            height = h,
            strideY = w * 2u,
            strideU = ((w + 1u) / 2u) * 2u,
            strideV = ((w + 1u) / 2u) * 2u,
        )
    }

    internal companion object {
        internal fun new(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): io.github.kotlinmania.libwebrtc.I010Buffer =
            io.github.kotlinmania.libwebrtc.I010Buffer(
                handle =
                    I010Buffer(
                        bufferWidth = width,
                        bufferHeight = height,
                        strideYVal = strideY,
                        strideUVal = strideU,
                        strideVVal = strideV,
                    ),
            )
    }
}

internal class NV12Buffer(
    private val bufferWidth: UInt,
    private val bufferHeight: UInt,
    private val strideYVal: UInt = bufferWidth,
    private val strideUvVal: UInt = ((bufferWidth + 1u) / 2u) * 2u,
    internal val yData: ByteArray = ByteArray((strideYVal * bufferHeight).toInt()),
    internal val uvData: ByteArray = ByteArray((strideUvVal * ((bufferHeight + 1u) / 2u)).toInt()),
) {
    internal fun sysHandle(): NV12Buffer = this

    internal fun width(): UInt = bufferWidth

    internal fun height(): UInt = bufferHeight

    internal fun chromaWidth(): UInt = (bufferWidth + 1u) / 2u

    internal fun chromaHeight(): UInt = (bufferHeight + 1u) / 2u

    internal fun strideY(): UInt = strideYVal

    internal fun strideUv(): UInt = strideUvVal

    internal fun toI420(): I420Buffer {
        val chromaH = (bufferHeight + 1u) / 2u
        val chromaW = (bufferWidth + 1u) / 2u
        val dstY = ByteArray((bufferWidth * bufferHeight).toInt())
        val dstU = ByteArray((chromaW * chromaH).toInt())
        val dstV = ByteArray((chromaW * chromaH).toInt())
        nv12ToI420(
            yData,
            strideYVal,
            uvData,
            strideUvVal,
            dstY,
            bufferWidth,
            dstU,
            chromaW,
            dstV,
            chromaW,
            bufferWidth.toInt(),
            bufferHeight.toInt(),
        )
        return I420Buffer(
            bufferWidth = bufferWidth,
            bufferHeight = bufferHeight,
            strideYVal = bufferWidth,
            strideUVal = chromaW,
            strideVVal = chromaW,
            yData = dstY,
            uData = dstU,
            vData = dstV,
        )
    }

    internal fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        toI420().toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    internal fun data(): Pair<ByteArray, ByteArray> = Pair(yData, uvData)

    internal fun scale(scaledWidth: Int, scaledHeight: Int): io.github.kotlinmania.libwebrtc.NV12Buffer {
        val w = scaledWidth.coerceAtLeast(0).toUInt()
        val h = scaledHeight.coerceAtLeast(0).toUInt()
        return new(
            width = w,
            height = h,
            strideY = w,
            strideUv = ((w + 1u) / 2u) * 2u,
        )
    }

    internal companion object {
        internal fun new(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideUv: UInt,
        ): io.github.kotlinmania.libwebrtc.NV12Buffer =
            io.github.kotlinmania.libwebrtc.NV12Buffer(
                handle =
                    NV12Buffer(
                        bufferWidth = width,
                        bufferHeight = height,
                        strideYVal = strideY,
                        strideUvVal = strideUv,
                    ),
            )
    }
}
