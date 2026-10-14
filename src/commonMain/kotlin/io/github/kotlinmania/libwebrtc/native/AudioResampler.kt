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
        val written =
            WebrtcNativeBridge.audioResample(
                src = src,
                numFrames = srcSamplesPerCh,
                numChannels = srcCh,
                srcRate = sampleRate.toInt(),
                dstChannels = dstCh,
                dstRate = dstSampleRate.toInt(),
                dst = out,
            )
        buffer = if (written == out.size) out else out.copyOf(written.coerceIn(0, out.size))
        return buffer
    }

    public companion object {
        public fun default(): AudioResampler = AudioResampler()
    }
}
