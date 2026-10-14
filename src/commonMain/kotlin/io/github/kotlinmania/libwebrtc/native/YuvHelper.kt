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
    width.toString()
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
    width.toString()
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
    width.toString()
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
    width.toString()
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
    width.toString()
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
    width.toString()
    val heightAbs = abs(height).toUInt()
    val chromaHeight = heightAbs / 2u
    val minY = (srcStrideY * heightAbs).toInt() / 2
    val minU = (srcStrideU * chromaHeight).toInt() / 2
    val minV = (srcStrideV * chromaHeight).toInt() / 2

    require(srcY.size >= minY) { "src_y isn't large enough" }
    require(srcU.size >= minU) { "src_u isn't large enough" }
    require(srcV.size >= minV) { "src_v isn't large enough" }
}

private fun yuvToRgbPixel(y: Int, u: Int, v: Int): Triple<Int, Int, Int> {
    val c = y - 16
    val d = u - 128
    val e = v - 128
    val r = ((298 * c + 409 * e + 128) shr 8).coerceIn(0, 255)
    val g = ((298 * c - 100 * d - 208 * e + 128) shr 8).coerceIn(0, 255)
    val b = ((298 * c + 516 * d + 128) shr 8).coerceIn(0, 255)
    return Triple(r, g, b)
}

private fun rgbToY(r: Int, g: Int, b: Int): Int =
    (((66 * r + 129 * g + 25 * b + 128) shr 8) + 16).coerceIn(0, 255)

private fun rgbToU(r: Int, g: Int, b: Int): Int =
    (((-38 * r - 74 * g + 112 * b + 128) shr 8) + 128).coerceIn(0, 255)

private fun rgbToV(r: Int, g: Int, b: Int): Int =
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
    val h = abs(height)
    val sStride = srcStrideArgb.toInt()
    val dStride = dstStrideRgb24.toInt()
    for (row in 0 until h) {
        for (col in 0 until width) {
            val sIdx = row * sStride + col * 4
            val dIdx = row * dStride + col * 3
            if (sIdx + 2 < srcArgb.size && dIdx + 2 < dstRgb24.size) {
                dstRgb24[dIdx] = srcArgb[sIdx]
                dstRgb24[dIdx + 1] = srcArgb[sIdx + 1]
                dstRgb24[dIdx + 2] = srcArgb[sIdx + 2]
            }
        }
    }
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
    val h = abs(height)
    val sStride = srcStrideArgb.toInt()
    val yStride = dstStrideY.toInt()
    val uStride = dstStrideU.toInt()
    val vStride = dstStrideV.toInt()
    for (row in 0 until h) {
        for (col in 0 until width) {
            val idx = row * sStride + col * 4
            val b = srcArgb[idx].toInt() and 0xFF
            val g = srcArgb[idx + 1].toInt() and 0xFF
            val r = srcArgb[idx + 2].toInt() and 0xFF
            dstY[row * yStride + col] = rgbToY(r, g, b).toByte()
            if (row % 2 == 0 && col % 2 == 0) {
                dstU[(row / 2) * uStride + (col / 2)] = rgbToU(r, g, b).toByte()
                dstV[(row / 2) * vStride + (col / 2)] = rgbToV(r, g, b).toByte()
            }
        }
    }
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
    val h = abs(height)
    val sStride = srcStrideArgb.toInt()
    val yStride = dstStrideY.toInt()
    val uStride = dstStrideU.toInt()
    val vStride = dstStrideV.toInt()
    for (row in 0 until h) {
        for (col in 0 until width) {
            val idx = row * sStride + col * 4
            val r = srcArgb[idx].toInt() and 0xFF
            val g = srcArgb[idx + 1].toInt() and 0xFF
            val b = srcArgb[idx + 2].toInt() and 0xFF
            dstY[row * yStride + col] = rgbToY(r, g, b).toByte()
            if (row % 2 == 0 && col % 2 == 0) {
                dstU[(row / 2) * uStride + (col / 2)] = rgbToU(r, g, b).toByte()
                dstV[(row / 2) * vStride + (col / 2)] = rgbToV(r, g, b).toByte()
            }
        }
    }
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
    val h = abs(height)
    val yStride = srcStrideY.toInt()
    val uStride = srcStrideU.toInt()
    val vStride = srcStrideV.toInt()
    val dStride = dstStride.toInt()
    for (row in 0 until h) {
        for (col in 0 until width) {
            val y = srcY[row * yStride + col].toInt() and 0xFF
            val u = srcU[(row / 2) * uStride + (col / 2)].toInt() and 0xFF
            val v = srcV[(row / 2) * vStride + (col / 2)].toInt() and 0xFF
            val (r, g, b) = yuvToRgbPixel(y, u, v)
            val dIdx = row * dStride + col * 4
            if (dIdx + 3 < dst.size) {
                when (order) {
                    0 -> { // ARGB (little-endian BGRA in memory)
                        dst[dIdx] = b.toByte()
                        dst[dIdx + 1] = g.toByte()
                        dst[dIdx + 2] = r.toByte()
                        dst[dIdx + 3] = 0xFF.toByte()
                    }
                    1 -> { // BGRA (little-endian ARGB in memory)
                        dst[dIdx] = 0xFF.toByte()
                        dst[dIdx + 1] = r.toByte()
                        dst[dIdx + 2] = g.toByte()
                        dst[dIdx + 3] = b.toByte()
                    }
                    2 -> { // ABGR (little-endian RGBA in memory)
                        dst[dIdx] = r.toByte()
                        dst[dIdx + 1] = g.toByte()
                        dst[dIdx + 2] = b.toByte()
                        dst[dIdx + 3] = 0xFF.toByte()
                    }
                    else -> { // RGBA (little-endian ABGR in memory)
                        dst[dIdx] = 0xFF.toByte()
                        dst[dIdx + 1] = b.toByte()
                        dst[dIdx + 2] = g.toByte()
                        dst[dIdx + 3] = r.toByte()
                    }
                }
            }
        }
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
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    for (row in 0 until h) {
        srcY.copyInto(dstY, row * dstStrideY.toInt(), row * srcStrideY.toInt(), row * srcStrideY.toInt() + width)
    }
    for (row in 0 until chromaH) {
        for (col in 0 until chromaW) {
            dstUv[row * dstStrideUv.toInt() + col * 2] = srcU[row * srcStrideU.toInt() + col]
            dstUv[row * dstStrideUv.toInt() + col * 2 + 1] = srcV[row * srcStrideV.toInt() + col]
        }
    }
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
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    for (row in 0 until h) {
        srcY.copyInto(dstY, row * dstStrideY.toInt(), row * srcStrideY.toInt(), row * srcStrideY.toInt() + width)
    }
    for (row in 0 until chromaH) {
        for (col in 0 until chromaW) {
            dstU[row * dstStrideU.toInt() + col] = srcUv[row * srcStrideUv.toInt() + col * 2]
            dstV[row * dstStrideV.toInt() + col] = srcUv[row * srcStrideUv.toInt() + col * 2 + 1]
        }
    }
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
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    nv12ToI420(
        srcY,
        srcStrideY,
        srcUv,
        srcStrideUv,
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
    val h = abs(height)
    val chromaH = (h + 1) / 2
    val chromaW = (width + 1) / 2
    val yPlane = ByteArray(width * h)
    val uPlane = ByteArray(chromaW * chromaH)
    val vPlane = ByteArray(chromaW * chromaH)
    nv12ToI420(
        srcY,
        srcStrideY,
        srcUv,
        srcStrideUv,
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
