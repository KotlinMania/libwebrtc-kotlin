// port-lint: source native/yuv_helper.rs
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

import kotlin.math.abs

internal fun argbAssertSafety(
    src: ByteArray,
    srcStride: UInt,
    width: Int,
    height: Int,
) {
    require(width >= 0) { "width must be non-negative" }
    val heightAbs = abs(height).toUInt()
    val min = (srcStride * heightAbs).toInt()
    require(src.size >= min) { "src isn't large enough" }
}

internal fun i420AssertSafety(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    width: Int,
    height: Int,
) {
    require(width >= 0) { "width must be non-negative" }
    val heightAbs = abs(height).toUInt()
    val chromaHeight = (heightAbs + 1u) / 2u
    val minY = (srcStrideY * heightAbs).toInt()
    val minU = (srcStrideU * chromaHeight).toInt()
    val minV = (srcStrideV * chromaHeight).toInt()

    require(srcY.size >= minY) { "src_y isn't large enough" }
    require(srcU.size >= minU) { "src_u isn't large enough" }
    require(srcV.size >= minV) { "src_v isn't large enough" }
}

internal fun nv12AssertSafety(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcUv: ByteArray,
    srcStrideUv: UInt,
    width: Int,
    height: Int,
) {
    require(width >= 0) { "width must be non-negative" }
    val heightAbs = abs(height).toUInt()
    val chromaHeight = (heightAbs + 1u) / 2u

    val minY = (srcStrideY * heightAbs).toInt()
    val minUv = (srcStrideUv * chromaHeight).toInt()

    require(srcY.size >= minY) { "src_y isn't large enough" }
    require(srcUv.size >= minUv) { "src_uv isn't large enough" }
}

internal fun i444AssertSafety(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    width: Int,
    height: Int,
) {
    require(width >= 0) { "width must be non-negative" }
    val heightAbs = abs(height).toUInt()
    val minY = (srcStrideY * heightAbs).toInt()
    val minU = (srcStrideU * heightAbs).toInt()
    val minV = (srcStrideV * heightAbs).toInt()

    require(srcY.size >= minY) { "src_y isn't large enough" }
    require(srcU.size >= minU) { "src_u isn't large enough" }
    require(srcV.size >= minV) { "src_v isn't large enough" }
}

internal fun i422AssertSafety(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    width: Int,
    height: Int,
) {
    require(width >= 0) { "width must be non-negative" }
    val heightAbs = abs(height).toUInt()
    val minY = (srcStrideY * heightAbs).toInt()
    val minU = (srcStrideU * heightAbs).toInt()
    val minV = (srcStrideV * heightAbs).toInt()

    require(srcY.size >= minY) { "src_y isn't large enough" }
    require(srcU.size >= minU) { "src_u isn't large enough" }
    require(srcV.size >= minV) { "src_v isn't large enough" }
}

internal fun i010AssertSafety(
    srcY: UShortArray,
    srcStrideY: UInt,
    srcU: UShortArray,
    srcStrideU: UInt,
    srcV: UShortArray,
    srcStrideV: UInt,
    width: Int,
    height: Int,
) {
    require(width >= 0) { "width must be non-negative" }
    val heightAbs = abs(height).toUInt()
    val chromaHeight = heightAbs / 2u
    val minY = (srcStrideY * heightAbs).toInt() / 2
    val minU = (srcStrideU * chromaHeight).toInt() / 2
    val minV = (srcStrideV * chromaHeight).toInt() / 2

    require(srcY.size >= minY) { "src_y isn't large enough" }
    require(srcU.size >= minU) { "src_u isn't large enough" }
    require(srcV.size >= minV) { "src_v isn't large enough" }
}

internal fun yuvToRgbPixel(y: Int, u: Int, v: Int): Triple<Int, Int, Int> {
    val c = y - 16
    val d = u - 128
    val e = v - 128
    val r = ((298 * c + 409 * e + 128) shr 8).coerceIn(0, 255)
    val g = ((298 * c - 100 * d - 208 * e + 128) shr 8).coerceIn(0, 255)
    val b = ((298 * c + 516 * d + 128) shr 8).coerceIn(0, 255)
    return Triple(r, g, b)
}

internal fun rgbToY(r: Int, g: Int, b: Int): Int =
    (((66 * r + 129 * g + 25 * b + 128) shr 8) + 16).coerceIn(0, 255)

internal fun rgbToU(r: Int, g: Int, b: Int): Int =
    (((-38 * r - 74 * g + 112 * b + 128) shr 8) + 128).coerceIn(0, 255)

internal fun rgbToV(r: Int, g: Int, b: Int): Int =
    (((112 * r - 94 * g - 18 * b + 128) shr 8) + 128).coerceIn(0, 255)

public fun argbToRgb24(
    srcArgb: ByteArray,
    srcStrideArgb: UInt,
    dstRgb24: ByteArray,
    dstStrideRgb24: UInt,
    width: Int,
    height: Int,
) {
    argbAssertSafety(srcArgb, srcStrideArgb, width, height)
    argbAssertSafety(dstRgb24, dstStrideRgb24, width, height)
    WebrtcNativeBridge.yuvArgbToRgb24(
        srcArgb = srcArgb,
        srcStrideArgb = srcStrideArgb.toInt(),
        dstRgb24 = dstRgb24,
        dstStrideRgb24 = dstStrideRgb24.toInt(),
        width = width,
        height = height,
    )
}

public fun argbToI420(
    srcArgb: ByteArray,
    srcStrideArgb: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstU: ByteArray,
    dstStrideU: UInt,
    dstV: ByteArray,
    dstStrideV: UInt,
    width: Int,
    height: Int,
) {
    i420AssertSafety(dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height)
    argbAssertSafety(srcArgb, srcStrideArgb, width, height)
    WebrtcNativeBridge.yuvArgbToI420(
        srcArgb = srcArgb,
        srcStrideArgb = srcStrideArgb.toInt(),
        dstY = dstY,
        dstStrideY = dstStrideY.toInt(),
        dstU = dstU,
        dstStrideU = dstStrideU.toInt(),
        dstV = dstV,
        dstStrideV = dstStrideV.toInt(),
        width = width,
        height = height,
    )
}

public fun abgrToI420(
    srcArgb: ByteArray,
    srcStrideArgb: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstU: ByteArray,
    dstStrideU: UInt,
    dstV: ByteArray,
    dstStrideV: UInt,
    width: Int,
    height: Int,
) {
    i420AssertSafety(dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height)
    argbAssertSafety(srcArgb, srcStrideArgb, width, height)
    WebrtcNativeBridge.yuvAbgrToI420(
        srcAbgr = srcArgb,
        srcStrideAbgr = srcStrideArgb.toInt(),
        dstY = dstY,
        dstStrideY = dstStrideY.toInt(),
        dstU = dstU,
        dstStrideU = dstStrideU.toInt(),
        dstV = dstV,
        dstStrideV = dstStrideV.toInt(),
        width = width,
        height = height,
    )
}

private fun convertI420ToFourCc(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dst: ByteArray,
    dstStride: UInt,
    width: Int,
    height: Int,
    order: Int,
) {
    i420AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    argbAssertSafety(dst, dstStride, width, height)
    val sY = srcStrideY.toInt()
    val sU = srcStrideU.toInt()
    val sV = srcStrideV.toInt()
    val dS = dstStride.toInt()
    when (order) {
        0 -> WebrtcNativeBridge.yuvI420ToArgb(srcY, sY, srcU, sU, srcV, sV, dst, dS, width, height)
        1 -> WebrtcNativeBridge.yuvI420ToBgra(srcY, sY, srcU, sU, srcV, sV, dst, dS, width, height)
        2 -> WebrtcNativeBridge.yuvI420ToAbgr(srcY, sY, srcU, sU, srcV, sV, dst, dS, width, height)
        else -> WebrtcNativeBridge.yuvI420ToRgba(srcY, sY, srcU, sU, srcV, sV, dst, dS, width, height)
    }
}

public fun i420ToArgb(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dst: ByteArray,
    dstStride: UInt,
    width: Int,
    height: Int,
) {
    convertI420ToFourCc(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dst, dstStride, width, height, 0)
}

public fun i420ToBgra(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dst: ByteArray,
    dstStride: UInt,
    width: Int,
    height: Int,
) {
    convertI420ToFourCc(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dst, dstStride, width, height, 1)
}

public fun i420ToAbgr(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dst: ByteArray,
    dstStride: UInt,
    width: Int,
    height: Int,
) {
    convertI420ToFourCc(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dst, dstStride, width, height, 2)
}

public fun i420ToRgba(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dst: ByteArray,
    dstStride: UInt,
    width: Int,
    height: Int,
) {
    convertI420ToFourCc(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, dst, dstStride, width, height, 3)
}

public fun i420ToNv12(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstUv: ByteArray,
    dstStrideUv: UInt,
    width: Int,
    height: Int,
) {
    i420AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    nv12AssertSafety(dstY, dstStrideY, dstUv, dstStrideUv, width, height)
    WebrtcNativeBridge.yuvI420ToNv12(
        srcY = srcY,
        srcStrideY = srcStrideY.toInt(),
        srcU = srcU,
        srcStrideU = srcStrideU.toInt(),
        srcV = srcV,
        srcStrideV = srcStrideV.toInt(),
        dstY = dstY,
        dstStrideY = dstStrideY.toInt(),
        dstUv = dstUv,
        dstStrideUv = dstStrideUv.toInt(),
        width = width,
        height = height,
    )
}

public fun nv12ToI420(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcUv: ByteArray,
    srcStrideUv: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstU: ByteArray,
    dstStrideU: UInt,
    dstV: ByteArray,
    dstStrideV: UInt,
    width: Int,
    height: Int,
) {
    nv12AssertSafety(srcY, srcStrideY, srcUv, srcStrideUv, width, height)
    i420AssertSafety(dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height)
    WebrtcNativeBridge.yuvNv12ToI420(
        srcY = srcY,
        srcStrideY = srcStrideY.toInt(),
        srcUv = srcUv,
        srcStrideUv = srcStrideUv.toInt(),
        dstY = dstY,
        dstStrideY = dstStrideY.toInt(),
        dstU = dstU,
        dstStrideU = dstStrideU.toInt(),
        dstV = dstV,
        dstStrideV = dstStrideV.toInt(),
        width = width,
        height = height,
    )
}

public fun i444ToI420(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstU: ByteArray,
    dstStrideU: UInt,
    dstV: ByteArray,
    dstStrideV: UInt,
    width: Int,
    height: Int,
) {
    i444AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    i420AssertSafety(dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    for (row in 0 until h) {
        srcY.copyInto(dstY, row * dstStrideY.toInt(), row * srcStrideY.toInt(), row * srcStrideY.toInt() + width)
    }
    for (row in 0 until chromaH) {
        val srcRow = (row * 2).coerceAtMost(h - 1)
        for (col in 0 until chromaW) {
            val srcCol = (col * 2).coerceAtMost(width - 1)
            dstU[row * dstStrideU.toInt() + col] = srcU[srcRow * srcStrideU.toInt() + srcCol]
            dstV[row * dstStrideV.toInt() + col] = srcV[srcRow * srcStrideV.toInt() + srcCol]
        }
    }
}

public fun i422ToI420(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstU: ByteArray,
    dstStrideU: UInt,
    dstV: ByteArray,
    dstStrideV: UInt,
    width: Int,
    height: Int,
) {
    i422AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    i420AssertSafety(dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    for (row in 0 until h) {
        srcY.copyInto(dstY, row * dstStrideY.toInt(), row * srcStrideY.toInt(), row * srcStrideY.toInt() + width)
    }
    for (row in 0 until chromaH) {
        val srcRow = (row * 2).coerceAtMost(h - 1)
        for (col in 0 until chromaW) {
            dstU[row * dstStrideU.toInt() + col] = srcU[srcRow * srcStrideU.toInt() + col]
            dstV[row * dstStrideV.toInt() + col] = srcV[srcRow * srcStrideV.toInt() + col]
        }
    }
}

public fun i010ToI420(
    srcY: UShortArray,
    srcStrideY: UInt,
    srcU: UShortArray,
    srcStrideU: UInt,
    srcV: UShortArray,
    srcStrideV: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstU: ByteArray,
    dstStrideU: UInt,
    dstV: ByteArray,
    dstStrideV: UInt,
    width: Int,
    height: Int,
) {
    i010AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    i420AssertSafety(dstY, dstStrideY, dstU, dstStrideU, dstV, dstStrideV, width, height)
    val h = abs(height)
    val chromaH = h / 2
    val chromaW = (width + 1) / 2
    val sStrideY = srcStrideY.toInt() / 2
    val sStrideU = srcStrideU.toInt() / 2
    val sStrideV = srcStrideV.toInt() / 2
    for (row in 0 until h) {
        for (col in 0 until width) {
            dstY[row * dstStrideY.toInt() + col] = ((srcY[row * sStrideY + col].toInt() shr 2) and 0xFF).toByte()
        }
    }
    for (row in 0 until chromaH) {
        for (col in 0 until chromaW) {
            dstU[row * dstStrideU.toInt() + col] = ((srcU[row * sStrideU + col].toInt() shr 2) and 0xFF).toByte()
            dstV[row * dstStrideV.toInt() + col] = ((srcV[row * sStrideV + col].toInt() shr 2) and 0xFF).toByte()
        }
    }
}

public fun nv12ToArgb(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcUv: ByteArray,
    srcStrideUv: UInt,
    dstArgb: ByteArray,
    dstStrideArgb: UInt,
    width: Int,
    height: Int,
) {
    nv12AssertSafety(srcY, srcStrideY, srcUv, srcStrideUv, width, height)
    argbAssertSafety(dstArgb, dstStrideArgb, width, height)
    WebrtcNativeBridge.yuvNv12ToArgb(
        srcY = srcY,
        srcStrideY = srcStrideY.toInt(),
        srcUv = srcUv,
        srcStrideUv = srcStrideUv.toInt(),
        dstArgb = dstArgb,
        dstStrideArgb = dstStrideArgb.toInt(),
        width = width,
        height = height,
    )
}

public fun nv12ToAbgr(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcUv: ByteArray,
    srcStrideUv: UInt,
    dstAbgr: ByteArray,
    dstStrideAbgr: UInt,
    width: Int,
    height: Int,
) {
    nv12AssertSafety(srcY, srcStrideY, srcUv, srcStrideUv, width, height)
    argbAssertSafety(dstAbgr, dstStrideAbgr, width, height)
    WebrtcNativeBridge.yuvNv12ToAbgr(
        srcY = srcY,
        srcStrideY = srcStrideY.toInt(),
        srcUv = srcUv,
        srcStrideUv = srcStrideUv.toInt(),
        dstAbgr = dstAbgr,
        dstStrideAbgr = dstStrideAbgr.toInt(),
        width = width,
        height = height,
    )
}

public fun i444ToArgb(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dstArgb: ByteArray,
    dstStrideArgb: UInt,
    width: Int,
    height: Int,
) {
    i444AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    argbAssertSafety(dstArgb, dstStrideArgb, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    i444ToI420(
        srcY,
        srcStrideY,
        srcU,
        srcStrideU,
        srcV,
        srcStrideV,
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    i420ToArgb(
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        dstArgb,
        dstStrideArgb,
        width,
        height,
    )
}

public fun i444ToAbgr(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dstAbgr: ByteArray,
    dstStrideAbgr: UInt,
    width: Int,
    height: Int,
) {
    i444AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    argbAssertSafety(dstAbgr, dstStrideAbgr, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    i444ToI420(
        srcY,
        srcStrideY,
        srcU,
        srcStrideU,
        srcV,
        srcStrideV,
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    i420ToAbgr(
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        dstAbgr,
        dstStrideAbgr,
        width,
        height,
    )
}

public fun i422ToArgb(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dstArgb: ByteArray,
    dstStrideArgb: UInt,
    width: Int,
    height: Int,
) {
    i422AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    argbAssertSafety(dstArgb, dstStrideArgb, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    i422ToI420(
        srcY,
        srcStrideY,
        srcU,
        srcStrideU,
        srcV,
        srcStrideV,
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    i420ToArgb(
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        dstArgb,
        dstStrideArgb,
        width,
        height,
    )
}

public fun i422ToAbgr(
    srcY: ByteArray,
    srcStrideY: UInt,
    srcU: ByteArray,
    srcStrideU: UInt,
    srcV: ByteArray,
    srcStrideV: UInt,
    dstAbgr: ByteArray,
    dstStrideAbgr: UInt,
    width: Int,
    height: Int,
) {
    i422AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    argbAssertSafety(dstAbgr, dstStrideAbgr, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    i422ToI420(
        srcY,
        srcStrideY,
        srcU,
        srcStrideU,
        srcV,
        srcStrideV,
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    i420ToAbgr(
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        dstAbgr,
        dstStrideAbgr,
        width,
        height,
    )
}

public fun i010ToArgb(
    srcY: UShortArray,
    srcStrideY: UInt,
    srcU: UShortArray,
    srcStrideU: UInt,
    srcV: UShortArray,
    srcStrideV: UInt,
    dstArgb: ByteArray,
    dstStrideArgb: UInt,
    width: Int,
    height: Int,
) {
    i010AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    argbAssertSafety(dstArgb, dstStrideArgb, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    i010ToI420(
        srcY,
        srcStrideY,
        srcU,
        srcStrideU,
        srcV,
        srcStrideV,
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    i420ToArgb(
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        dstArgb,
        dstStrideArgb,
        width,
        height,
    )
}

public fun i010ToAbgr(
    srcY: UShortArray,
    srcStrideY: UInt,
    srcU: UShortArray,
    srcStrideU: UInt,
    srcV: UShortArray,
    srcStrideV: UInt,
    dstAbgr: ByteArray,
    dstStrideAbgr: UInt,
    width: Int,
    height: Int,
) {
    i010AssertSafety(srcY, srcStrideY, srcU, srcStrideU, srcV, srcStrideV, width, height)
    argbAssertSafety(dstAbgr, dstStrideAbgr, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    i010ToI420(
        srcY,
        srcStrideY,
        srcU,
        srcStrideU,
        srcV,
        srcStrideV,
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    i420ToAbgr(
        yPlane,
        width.toUInt(),
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        dstAbgr,
        dstStrideAbgr,
        width,
        height,
    )
}

public fun abgrToNv12(
    srcAbgr: ByteArray,
    srcStrideAbgr: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstUv: ByteArray,
    dstStrideUv: UInt,
    width: Int,
    height: Int,
) {
    argbAssertSafety(srcAbgr, srcStrideAbgr, width, height)
    nv12AssertSafety(dstY, dstStrideY, dstUv, dstStrideUv, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    abgrToI420(
        srcAbgr,
        srcStrideAbgr,
        dstY,
        dstStrideY,
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    for (row in 0 until chromaH) {
        for (col in 0 until chromaW) {
            dstUv[row * dstStrideUv.toInt() + col * 2] = uPlane[row * chromaW + col]
            dstUv[row * dstStrideUv.toInt() + col * 2 + 1] = vPlane[row * chromaW + col]
        }
    }
}

public fun argbToNv12(
    srcArgb: ByteArray,
    srcStrideArgb: UInt,
    dstY: ByteArray,
    dstStrideY: UInt,
    dstUv: ByteArray,
    dstStrideUv: UInt,
    width: Int,
    height: Int,
) {
    argbAssertSafety(srcArgb, srcStrideArgb, width, height)
    nv12AssertSafety(dstY, dstStrideY, dstUv, dstStrideUv, width, height)
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    argbToI420(
        srcArgb,
        srcStrideArgb,
        dstY,
        dstStrideY,
        uPlane,
        chromaW.toUInt(),
        vPlane,
        chromaW.toUInt(),
        width,
        height,
    )
    for (row in 0 until chromaH) {
        for (col in 0 until chromaW) {
            dstUv[row * dstStrideUv.toInt() + col * 2] = uPlane[row * chromaW + col]
            dstUv[row * dstStrideUv.toInt() + col * 2 + 1] = vPlane[row * chromaW + col]
        }
    }
}
