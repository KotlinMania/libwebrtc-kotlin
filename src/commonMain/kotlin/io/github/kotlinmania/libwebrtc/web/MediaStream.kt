// port-lint: source web/media_stream.rs
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

package io.github.kotlinmania.libwebrtc.web

import io.github.kotlinmania.libwebrtc.RtcVideoTrack
import io.github.kotlinmania.libwebrtc.SinkError
import io.github.kotlinmania.libwebrtc.VideoBuffer
import io.github.kotlinmania.libwebrtc.VideoBufferType
import io.github.kotlinmania.libwebrtc.VideoFormatType
import io.github.kotlinmania.libwebrtc.VideoFrame

internal interface VideoFrameBuffer {
    fun width(): Int

    fun height(): Int
}

internal interface PlanarYuvBuffer : VideoFrameBuffer {
    fun chromaWidth(): Int

    fun chromaHeight(): Int

    fun strideY(): Int

    fun strideU(): Int

    fun strideV(): Int
}

internal interface PlanarYuv8Buffer : PlanarYuvBuffer {
    fun dataY(): ByteArray

    fun dataU(): ByteArray

    fun dataV(): ByteArray
}

internal interface PlanarYuv16BBuffer : PlanarYuvBuffer {
    fun dataY(): UShortArray

    fun dataU(): UShortArray

    fun dataV(): UShortArray
}

internal interface BiplanarYuvBuffer : VideoFrameBuffer {
    fun chromaWidth(): Int

    fun chromaHeight(): Int

    fun strideY(): Int

    fun strideUv(): Int
}

internal interface BiplanarYuv8Buffer : BiplanarYuvBuffer {
    fun dataY(): ByteArray

    fun dataUv(): ByteArray
}

/**
 * Create a new WebGL texture and update it inside requestAnimationFrame.
 */
internal class WebGlVideoSink private constructor(
    internal val track: RtcVideoTrack,
    internal val glCtx: Any,
    internal val tex: Any,
) {
    internal companion object {
        internal fun new(
            track: RtcVideoTrack,
            glCtx: Any?,
        ): Pair<WebGlVideoSink, List<VideoFrame>> {
            if (glCtx == null) {
                throw SinkError.CreateTexture
            }
            val tex = Any()
            return Pair(WebGlVideoSink(track, glCtx, tex), emptyList())
        }
    }
}

internal class WebGlBuffer(
    private val bufferWidth: Int,
    private val bufferHeight: Int,
    internal val tex: Any = Any(),
) : VideoFrameBuffer, VideoBuffer {
    override val width: UInt
        get() = bufferWidth.toUInt()

    override val height: UInt
        get() = bufferHeight.toUInt()

    override val bufferType: VideoBufferType
        get() = VideoBufferType.WebGl

    override fun width(): Int = bufferWidth

    override fun height(): Int = bufferHeight

    override fun toI420(): io.github.kotlinmania.libwebrtc.I420Buffer =
        io.github.kotlinmania.libwebrtc.I420Buffer.new(bufferWidth.toUInt(), bufferHeight.toUInt())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        toI420().toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }
}

/**
 * The following types support VideoFrame with WebCodecs.
 */
internal class I420Buffer(
    private val bufferWidth: Int = 0,
    private val bufferHeight: Int = 0,
    private val yPlane: ByteArray = ByteArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
    private val uPlane: ByteArray = ByteArray((((bufferWidth + 1) / 2) * ((bufferHeight + 1) / 2)).coerceAtLeast(0)),
    private val vPlane: ByteArray = ByteArray((((bufferWidth + 1) / 2) * ((bufferHeight + 1) / 2)).coerceAtLeast(0)),
) : PlanarYuv8Buffer {
    override fun width(): Int = bufferWidth

    override fun height(): Int = bufferHeight

    override fun chromaWidth(): Int = (bufferWidth + 1) / 2

    override fun chromaHeight(): Int = (bufferHeight + 1) / 2

    override fun strideY(): Int = bufferWidth

    override fun strideU(): Int = (bufferWidth + 1) / 2

    override fun strideV(): Int = (bufferWidth + 1) / 2

    override fun dataY(): ByteArray = yPlane

    override fun dataU(): ByteArray = uPlane

    override fun dataV(): ByteArray = vPlane
}

internal class I420ABuffer(
    private val bufferWidth: Int = 0,
    private val bufferHeight: Int = 0,
    private val yPlane: ByteArray = ByteArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
    private val uPlane: ByteArray = ByteArray((((bufferWidth + 1) / 2) * ((bufferHeight + 1) / 2)).coerceAtLeast(0)),
    private val vPlane: ByteArray = ByteArray((((bufferWidth + 1) / 2) * ((bufferHeight + 1) / 2)).coerceAtLeast(0)),
) : PlanarYuv8Buffer {
    override fun width(): Int = bufferWidth

    override fun height(): Int = bufferHeight

    override fun chromaWidth(): Int = (bufferWidth + 1) / 2

    override fun chromaHeight(): Int = (bufferHeight + 1) / 2

    override fun strideY(): Int = bufferWidth

    override fun strideU(): Int = (bufferWidth + 1) / 2

    override fun strideV(): Int = (bufferWidth + 1) / 2

    override fun dataY(): ByteArray = yPlane

    override fun dataU(): ByteArray = uPlane

    override fun dataV(): ByteArray = vPlane
}

internal class I422Buffer(
    private val bufferWidth: Int = 0,
    private val bufferHeight: Int = 0,
    private val yPlane: ByteArray = ByteArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
    private val uPlane: ByteArray = ByteArray((((bufferWidth + 1) / 2) * bufferHeight).coerceAtLeast(0)),
    private val vPlane: ByteArray = ByteArray((((bufferWidth + 1) / 2) * bufferHeight).coerceAtLeast(0)),
) : PlanarYuv8Buffer {
    override fun width(): Int = bufferWidth

    override fun height(): Int = bufferHeight

    override fun chromaWidth(): Int = (bufferWidth + 1) / 2

    override fun chromaHeight(): Int = bufferHeight

    override fun strideY(): Int = bufferWidth

    override fun strideU(): Int = (bufferWidth + 1) / 2

    override fun strideV(): Int = (bufferWidth + 1) / 2

    override fun dataY(): ByteArray = yPlane

    override fun dataU(): ByteArray = uPlane

    override fun dataV(): ByteArray = vPlane
}

internal class I444Buffer(
    private val bufferWidth: Int = 0,
    private val bufferHeight: Int = 0,
    private val yPlane: ByteArray = ByteArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
    private val uPlane: ByteArray = ByteArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
    private val vPlane: ByteArray = ByteArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
) : PlanarYuv8Buffer {
    override fun width(): Int = bufferWidth

    override fun height(): Int = bufferHeight

    override fun chromaWidth(): Int = bufferWidth

    override fun chromaHeight(): Int = bufferHeight

    override fun strideY(): Int = bufferWidth

    override fun strideU(): Int = bufferWidth

    override fun strideV(): Int = bufferWidth

    override fun dataY(): ByteArray = yPlane

    override fun dataU(): ByteArray = uPlane

    override fun dataV(): ByteArray = vPlane
}

internal class I010Buffer(
    private val bufferWidth: Int = 0,
    private val bufferHeight: Int = 0,
    private val yPlane: UShortArray = UShortArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
    private val uPlane: UShortArray = UShortArray((((bufferWidth + 1) / 2) * ((bufferHeight + 1) / 2)).coerceAtLeast(0)),
    private val vPlane: UShortArray = UShortArray((((bufferWidth + 1) / 2) * ((bufferHeight + 1) / 2)).coerceAtLeast(0)),
) : PlanarYuv16BBuffer {
    override fun width(): Int = bufferWidth

    override fun height(): Int = bufferHeight

    override fun chromaWidth(): Int = (bufferWidth + 1) / 2

    override fun chromaHeight(): Int = (bufferHeight + 1) / 2

    override fun strideY(): Int = bufferWidth * 2

    override fun strideU(): Int = ((bufferWidth + 1) / 2) * 2

    override fun strideV(): Int = ((bufferWidth + 1) / 2) * 2

    override fun dataY(): UShortArray = yPlane

    override fun dataU(): UShortArray = uPlane

    override fun dataV(): UShortArray = vPlane
}

internal class NV12Buffer(
    private val bufferWidth: Int = 0,
    private val bufferHeight: Int = 0,
    private val yPlane: ByteArray = ByteArray((bufferWidth * bufferHeight).coerceAtLeast(0)),
    private val uvPlane: ByteArray = ByteArray((((bufferWidth + 1) / 2) * 2 * ((bufferHeight + 1) / 2)).coerceAtLeast(0)),
) : PlanarYuvBuffer, BiplanarYuv8Buffer {
    override fun width(): Int = bufferWidth

    override fun height(): Int = bufferHeight

    override fun chromaWidth(): Int = (bufferWidth + 1) / 2

    override fun chromaHeight(): Int = (bufferHeight + 1) / 2

    override fun strideY(): Int = bufferWidth

    override fun strideU(): Int = (bufferWidth + 1) / 2

    override fun strideV(): Int = (bufferWidth + 1) / 2

    override fun strideUv(): Int = ((bufferWidth + 1) / 2) * 2

    override fun dataY(): ByteArray = yPlane

    override fun dataUv(): ByteArray = uvPlane
}
