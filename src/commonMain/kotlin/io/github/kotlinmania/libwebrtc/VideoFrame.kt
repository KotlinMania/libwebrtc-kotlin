// port-lint: source video_frame.rs
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

import io.github.kotlinmania.libwebrtc.native.I010Buffer as ImpI010Buffer
import io.github.kotlinmania.libwebrtc.native.I420ABuffer as ImpI420ABuffer
import io.github.kotlinmania.libwebrtc.native.I420Buffer as ImpI420Buffer
import io.github.kotlinmania.libwebrtc.native.I422Buffer as ImpI422Buffer
import io.github.kotlinmania.libwebrtc.native.I444Buffer as ImpI444Buffer
import io.github.kotlinmania.libwebrtc.native.NV12Buffer as ImpNV12Buffer
import io.github.kotlinmania.libwebrtc.native.NativeBuffer as ImpNativeBuffer
import io.github.kotlinmania.libwebrtc.web.WebGlBuffer as ImpWebGlBuffer

public enum class VideoRotation {
    VideoRotation0,
    VideoRotation90,
    VideoRotation180,
    VideoRotation270,
}

public enum class VideoFormatType {
    ARGB,
    BGRA,
    ABGR,
    RGBA,
}

public enum class VideoBufferType {
    Native,
    I420,
    I420A,
    I422,
    I444,
    I010,
    NV12,
    WebGl,
}

public sealed class SinkError(
    public val errorDescription: String,
) : RuntimeException(errorDescription) {
    public object CreateTexture : SinkError("cannot create a new WebGL texture")
}

public data class YuvStrides(
    public val strideY: UInt,
    public val strideU: UInt,
    public val strideV: UInt,
)

public data class YuvAStrides(
    public val strideY: UInt,
    public val strideU: UInt,
    public val strideV: UInt,
    public val strideA: UInt,
)

public data class Nv12Strides(
    public val strideY: UInt,
    public val strideUv: UInt,
)

public class YuvPlanes(
    public val dataY: ByteArray,
    public val dataU: ByteArray,
    public val dataV: ByteArray,
) {
    public operator fun component1(): ByteArray = dataY

    public operator fun component2(): ByteArray = dataU

    public operator fun component3(): ByteArray = dataV
}

public class YuvAPlanes(
    public val dataY: ByteArray,
    public val dataU: ByteArray,
    public val dataV: ByteArray,
    public val dataA: ByteArray?,
) {
    public operator fun component1(): ByteArray = dataY

    public operator fun component2(): ByteArray = dataU

    public operator fun component3(): ByteArray = dataV

    public operator fun component4(): ByteArray? = dataA
}

public class Yuv16Planes(
    public val dataY: UShortArray,
    public val dataU: UShortArray,
    public val dataV: UShortArray,
) {
    public operator fun component1(): UShortArray = dataY

    public operator fun component2(): UShortArray = dataU

    public operator fun component3(): UShortArray = dataV
}

public class Nv12Planes(
    public val dataY: ByteArray,
    public val dataUv: ByteArray,
) {
    public operator fun component1(): ByteArray = dataY

    public operator fun component2(): ByteArray = dataUv
}

public interface VideoBuffer {
    public fun width(): UInt

    public fun height(): UInt

    public fun bufferType(): VideoBufferType

    public fun toI420(): I420Buffer

    public fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    )
}

internal val VideoBuffer.width: UInt
    get() = width()

internal val VideoBuffer.height: UInt
    get() = height()

internal val VideoBuffer.bufferType: VideoBufferType
    get() = bufferType()

internal val VideoBuffer.type: VideoBufferType
    get() = bufferType()

public interface VideoFrameBufferExt : VideoBuffer

public typealias BoxVideoBuffer = VideoBuffer

public class VideoFrame(
    public val rotation: VideoRotation = VideoRotation.VideoRotation0,
    public val timestampUs: Long = 0L,
    public val buffer: VideoBuffer,
)

public typealias BoxVideoFrame = VideoFrame

public class NativeBuffer internal constructor(
    internal val handle: ImpNativeBuffer,
) : VideoBuffer {
    public constructor(
        width: UInt,
        height: UInt,
    ) : this(handle = ImpNativeBuffer(bufferWidth = width, bufferHeight = height))

    override fun width(): UInt = handle.width()

    override fun height(): UInt = handle.height()

    override fun bufferType(): VideoBufferType = VideoBufferType.Native

    override fun toI420(): I420Buffer = I420Buffer(handle = handle.toI420())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    public fun getCvPixelBuffer(): Any? = handle.getCvPixelBuffer()

    override fun toString(): String = "NativeBuffer(width=${width()}, height=${height()})"

    public companion object {
        public fun fromCvPixelBuffer(
            cvPixelBuffer: Any?,
            width: UInt = 0u,
            height: UInt = 0u,
        ): NativeBuffer = ImpNativeBuffer.fromCvPixelBuffer(cvPixelBuffer, width, height)
    }
}

public class WebGlBuffer internal constructor(
    internal val handle: ImpWebGlBuffer,
) : VideoBuffer {
    public constructor(
        width: UInt,
        height: UInt,
    ) : this(handle = ImpWebGlBuffer(bufferWidth = width.toInt(), bufferHeight = height.toInt()))

    override fun width(): UInt = handle.width

    override fun height(): UInt = handle.height

    override fun bufferType(): VideoBufferType = VideoBufferType.WebGl

    override fun toI420(): I420Buffer = handle.toI420()

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    override fun toString(): String = "WebGlBuffer(width=${width()}, height=${height()})"
}

public class I420Buffer internal constructor(
    internal val handle: ImpI420Buffer,
) : VideoBuffer {
    public constructor(
        width: Int,
        height: Int,
        dataY: ByteArray,
        strideY: Int,
        dataU: ByteArray,
        strideU: Int,
        dataV: ByteArray,
        strideV: Int,
    ) : this(
        handle =
            ImpI420Buffer(
                bufferWidth = width.toUInt(),
                bufferHeight = height.toUInt(),
                strideYVal = strideY.toUInt(),
                strideUVal = strideU.toUInt(),
                strideVVal = strideV.toUInt(),
                yData = dataY,
                uData = dataU,
                vData = dataV,
            ),
    )

    public constructor(
        width: UInt,
        height: UInt,
    ) : this(
        handle =
            ImpI420Buffer(
                bufferWidth = width,
                bufferHeight = height,
                strideYVal = width,
                strideUVal = (width + 1u) / 2u,
                strideVVal = (width + 1u) / 2u,
            ),
    )

    override fun width(): UInt = handle.width()

    override fun height(): UInt = handle.height()

    override fun bufferType(): VideoBufferType = VideoBufferType.I420

    internal val dataY: ByteArray
        get() = handle.yData

    internal val strideY: Int
        get() = handle.strideY().toInt()

    internal val dataU: ByteArray
        get() = handle.uData

    internal val strideU: Int
        get() = handle.strideU().toInt()

    internal val dataV: ByteArray
        get() = handle.vData

    internal val strideV: Int
        get() = handle.strideV().toInt()

    override fun toI420(): I420Buffer = I420Buffer(handle = handle.toI420())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    public fun chromaWidth(): UInt = handle.chromaWidth()

    public fun chromaHeight(): UInt = handle.chromaHeight()

    public fun strideY(): UInt = handle.strideY()

    public fun strideU(): UInt = handle.strideU()

    public fun strideV(): UInt = handle.strideV()

    public fun strides(): YuvStrides =
        YuvStrides(
            strideY = handle.strideY(),
            strideU = handle.strideU(),
            strideV = handle.strideV(),
        )

    public fun data(): YuvPlanes =
        YuvPlanes(
            dataY = handle.yData,
            dataU = handle.uData,
            dataV = handle.vData,
        )

    public fun dataMut(): YuvPlanes = data()

    public fun scale(scaledWidth: Int, scaledHeight: Int): I420Buffer =
        handle.scale(scaledWidth, scaledHeight)

    override fun toString(): String = "I420Buffer(width=$width, height=$height)"

    public companion object {
        public fun withStrides(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): I420Buffer = ImpI420Buffer.new(width, height, strideY, strideU, strideV)

        public fun new(
            width: UInt,
            height: UInt,
        ): I420Buffer =
            withStrides(
                width = width,
                height = height,
                strideY = width,
                strideU = (width + 1u) / 2u,
                strideV = (width + 1u) / 2u,
            )
    }
}

public class I420ABuffer internal constructor(
    internal val handle: ImpI420ABuffer,
) : VideoBuffer {
    public constructor(
        width: UInt,
        height: UInt,
    ) : this(handle = ImpI420ABuffer(bufferWidth = width, bufferHeight = height))

    override fun width(): UInt = handle.width()

    override fun height(): UInt = handle.height()

    override fun bufferType(): VideoBufferType = VideoBufferType.I420A

    override fun toI420(): I420Buffer = I420Buffer(handle = handle.toI420())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    public fun chromaWidth(): UInt = handle.chromaWidth()

    public fun chromaHeight(): UInt = handle.chromaHeight()

    public fun strideY(): UInt = handle.strideY()

    public fun strideU(): UInt = handle.strideU()

    public fun strideV(): UInt = handle.strideV()

    public fun strideA(): UInt = handle.strideA()

    public fun strides(): YuvAStrides =
        YuvAStrides(
            strideY = handle.strideY(),
            strideU = handle.strideU(),
            strideV = handle.strideV(),
            strideA = handle.strideA(),
        )

    public fun data(): YuvAPlanes =
        YuvAPlanes(
            dataY = handle.dataY(),
            dataU = handle.dataU(),
            dataV = handle.dataV(),
            dataA = handle.dataA(),
        )

    public fun dataMut(): YuvAPlanes = data()

    public fun scale(scaledWidth: Int, scaledHeight: Int): I420ABuffer =
        handle.scale(scaledWidth, scaledHeight)

    override fun toString(): String = "I420ABuffer(width=$width, height=$height)"
}

public class I422Buffer internal constructor(
    internal val handle: ImpI422Buffer,
) : VideoBuffer {
    public constructor(
        width: UInt,
        height: UInt,
    ) : this(handle = ImpI422Buffer(bufferWidth = width, bufferHeight = height))

    override fun width(): UInt = handle.width()

    override fun height(): UInt = handle.height()

    override fun bufferType(): VideoBufferType = VideoBufferType.I422

    override fun toI420(): I420Buffer = I420Buffer(handle = handle.toI420())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    public fun chromaWidth(): UInt = handle.chromaWidth()

    public fun chromaHeight(): UInt = handle.chromaHeight()

    public fun strideY(): UInt = handle.strideY()

    public fun strideU(): UInt = handle.strideU()

    public fun strideV(): UInt = handle.strideV()

    public fun strides(): YuvStrides =
        YuvStrides(
            strideY = handle.strideY(),
            strideU = handle.strideU(),
            strideV = handle.strideV(),
        )

    public fun data(): YuvPlanes =
        YuvPlanes(
            dataY = handle.yData,
            dataU = handle.uData,
            dataV = handle.vData,
        )

    public fun dataMut(): YuvPlanes = data()

    public fun scale(scaledWidth: Int, scaledHeight: Int): I422Buffer =
        handle.scale(scaledWidth, scaledHeight)

    override fun toString(): String = "I422Buffer(width=$width, height=$height)"

    public companion object {
        public fun withStrides(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): I422Buffer = ImpI422Buffer.new(width, height, strideY, strideU, strideV)

        public fun new(
            width: UInt,
            height: UInt,
        ): I422Buffer =
            withStrides(
                width = width,
                height = height,
                strideY = width,
                strideU = (width + 1u) / 2u,
                strideV = (width + 1u) / 2u,
            )
    }
}

public class I444Buffer internal constructor(
    internal val handle: ImpI444Buffer,
) : VideoBuffer {
    public constructor(
        width: UInt,
        height: UInt,
    ) : this(handle = ImpI444Buffer(bufferWidth = width, bufferHeight = height))

    override fun width(): UInt = handle.width()

    override fun height(): UInt = handle.height()

    override fun bufferType(): VideoBufferType = VideoBufferType.I444

    override fun toI420(): I420Buffer = I420Buffer(handle = handle.toI420())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    public fun chromaWidth(): UInt = handle.chromaWidth()

    public fun chromaHeight(): UInt = handle.chromaHeight()

    public fun strideY(): UInt = handle.strideY()

    public fun strideU(): UInt = handle.strideU()

    public fun strideV(): UInt = handle.strideV()

    public fun strides(): YuvStrides =
        YuvStrides(
            strideY = handle.strideY(),
            strideU = handle.strideU(),
            strideV = handle.strideV(),
        )

    public fun data(): YuvPlanes =
        YuvPlanes(
            dataY = handle.yData,
            dataU = handle.uData,
            dataV = handle.vData,
        )

    public fun dataMut(): YuvPlanes = data()

    public fun scale(scaledWidth: Int, scaledHeight: Int): I444Buffer =
        handle.scale(scaledWidth, scaledHeight)

    override fun toString(): String = "I444Buffer(width=$width, height=$height)"

    public companion object {
        public fun withStrides(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): I444Buffer = ImpI444Buffer.new(width, height, strideY, strideU, strideV)

        public fun new(
            width: UInt,
            height: UInt,
        ): I444Buffer =
            withStrides(
                width = width,
                height = height,
                strideY = width,
                strideU = width,
                strideV = width,
            )
    }
}

public class I010Buffer internal constructor(
    internal val handle: ImpI010Buffer,
) : VideoBuffer {
    public constructor(
        width: UInt,
        height: UInt,
    ) : this(handle = ImpI010Buffer(bufferWidth = width, bufferHeight = height))

    override fun width(): UInt = handle.width()

    override fun height(): UInt = handle.height()

    override fun bufferType(): VideoBufferType = VideoBufferType.I010

    override fun toI420(): I420Buffer = I420Buffer(handle = handle.toI420())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    public fun chromaWidth(): UInt = handle.chromaWidth()

    public fun chromaHeight(): UInt = handle.chromaHeight()

    public fun strideY(): UInt = handle.strideY()

    public fun strideU(): UInt = handle.strideU()

    public fun strideV(): UInt = handle.strideV()

    public fun strides(): YuvStrides =
        YuvStrides(
            strideY = handle.strideY(),
            strideU = handle.strideU(),
            strideV = handle.strideV(),
        )

    public fun data(): Yuv16Planes =
        Yuv16Planes(
            dataY = handle.yData,
            dataU = handle.uData,
            dataV = handle.vData,
        )

    public fun dataMut(): Yuv16Planes = data()

    public fun scale(scaledWidth: Int, scaledHeight: Int): I010Buffer =
        handle.scale(scaledWidth, scaledHeight)

    override fun toString(): String = "I010Buffer(width=$width, height=$height)"

    public companion object {
        public fun withStrides(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideU: UInt,
            strideV: UInt,
        ): I010Buffer = ImpI010Buffer.new(width, height, strideY, strideU, strideV)

        public fun new(
            width: UInt,
            height: UInt,
        ): I010Buffer =
            withStrides(
                width = width,
                height = height,
                strideY = width * 2u,
                strideU = ((width + 1u) / 2u) * 2u,
                strideV = ((width + 1u) / 2u) * 2u,
            )
    }
}

public class NV12Buffer internal constructor(
    internal val handle: ImpNV12Buffer,
) : VideoBuffer {
    public constructor(
        width: UInt,
        height: UInt,
    ) : this(handle = ImpNV12Buffer(bufferWidth = width, bufferHeight = height))

    override fun width(): UInt = handle.width()

    override fun height(): UInt = handle.height()

    override fun bufferType(): VideoBufferType = VideoBufferType.NV12

    override fun toI420(): I420Buffer = I420Buffer(handle = handle.toI420())

    override fun toArgb(
        format: VideoFormatType,
        dst: ByteArray,
        dstStride: UInt,
        dstWidth: Int,
        dstHeight: Int,
    ) {
        handle.toArgb(format, dst, dstStride, dstWidth, dstHeight)
    }

    public fun chromaWidth(): UInt = handle.chromaWidth()

    public fun chromaHeight(): UInt = handle.chromaHeight()

    public fun strideY(): UInt = handle.strideY()

    public fun strideUv(): UInt = handle.strideUv()

    public fun strides(): Nv12Strides =
        Nv12Strides(
            strideY = handle.strideY(),
            strideUv = handle.strideUv(),
        )

    public fun data(): Nv12Planes =
        Nv12Planes(
            dataY = handle.yData,
            dataUv = handle.uvData,
        )

    public fun dataMut(): Nv12Planes = data()

    public fun scale(scaledWidth: Int, scaledHeight: Int): NV12Buffer =
        handle.scale(scaledWidth, scaledHeight)

    override fun toString(): String = "NV12Buffer(width=$width, height=$height)"

    public companion object {
        public fun withStrides(
            width: UInt,
            height: UInt,
            strideY: UInt,
            strideUv: UInt,
        ): NV12Buffer = ImpNV12Buffer.new(width, height, strideY, strideUv)

        public fun new(
            width: UInt,
            height: UInt,
        ): NV12Buffer =
            withStrides(
                width = width,
                height = height,
                strideY = width,
                strideUv = ((width + 1u) / 2u) * 2u,
            )
    }
}
