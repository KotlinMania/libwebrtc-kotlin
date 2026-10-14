// port-lint: source native/audio_resampler.rs
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

public class AudioResampler {
    private var buffer: ShortArray = ShortArray(0)

    public fun remixAndResample(
        src: ShortArray,
        samplesPerChannel: UInt,
        numChannels: UInt,
        sampleRate: UInt,
        dstNumChannels: UInt,
        dstSampleRate: UInt,
    ): ShortArray {
        val srcSamplesPerCh = samplesPerChannel.toInt()
        val srcCh = numChannels.toInt()
        val dstCh = dstNumChannels.toInt()
        require(src.size >= srcSamplesPerCh * srcCh) {
            "src buffer too small"
        }
        if (srcSamplesPerCh == 0 || srcCh == 0 || dstCh == 0 || sampleRate == 0u || dstSampleRate == 0u) {
            buffer = ShortArray(0)
            return buffer
        }

        val dstSamplesPerCh = ((samplesPerChannel.toLong() * dstSampleRate.toLong()) / sampleRate.toLong()).toInt()
        val out = ShortArray(dstSamplesPerCh * dstCh)

        for (dstFrame in 0 until dstSamplesPerCh) {
            val srcPos =
                if (dstSamplesPerCh <= 1) {
                    0.0
                } else {
                    (dstFrame.toDouble() * (srcSamplesPerCh - 1).coerceAtLeast(0)) / (dstSamplesPerCh - 1)
                }
            val idx0 = srcPos.toInt().coerceIn(0, srcSamplesPerCh - 1)
            val idx1 = (idx0 + 1).coerceIn(0, srcSamplesPerCh - 1)
            val frac = srcPos - idx0

            for (ch in 0 until dstCh) {
                val sample0: Double
                val sample1: Double
                if (srcCh == dstCh) {
                    sample0 = src[idx0 * srcCh + ch].toDouble()
                    sample1 = src[idx1 * srcCh + ch].toDouble()
                } else if (dstCh == 1) {
                    var sum0 = 0.0
                    var sum1 = 0.0
                    for (c in 0 until srcCh) {
                        sum0 += src[idx0 * srcCh + c].toDouble()
                        sum1 += src[idx1 * srcCh + c].toDouble()
                    }
                    sample0 = sum0 / srcCh
                    sample1 = sum1 / srcCh
                } else {
                    val mappedCh = ch % srcCh
                    sample0 = src[idx0 * srcCh + mappedCh].toDouble()
                    sample1 = src[idx1 * srcCh + mappedCh].toDouble()
                }
                val interpolated = (sample0 + (sample1 - sample0) * frac).toInt()
                out[dstFrame * dstCh + ch] =
                    interpolated.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        buffer = out
        return buffer
    }

    public companion object {
        public fun default(): AudioResampler = AudioResampler()
    }
}
