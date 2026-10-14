// port-lint: source native/audio_mixer.rs
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

import io.github.kotlinmania.libwebrtc.AudioFrame

public enum class AudioFrameInfo {
    Normal,
    Muted,
    Error,
}

public interface AudioMixerSource {
    public fun ssrc(): Int

    public fun preferredSampleRate(): UInt

    public fun getAudioFrameWithInfo(targetSampleRate: UInt): AudioFrame?
}

internal class AudioMixerSourceImpl<T : AudioMixerSource>(
    internal val inner: T,
) {
    internal fun ssrc(): Int = inner.ssrc()

    internal fun preferredSampleRate(): Int = inner.preferredSampleRate().toInt()

    internal fun getAudioFrameWithInfo(
        targetSampleRate: Int,
        outBuffer: ShortArray,
        targetChannels: Int,
    ): AudioFrameInfo {
        val frame = inner.getAudioFrameWithInfo(targetSampleRate.toUInt()) ?: return AudioFrameInfo.Muted
        val samplesCount = frame.sampleRate.toInt() / 100
        require(frame.sampleRate == targetSampleRate.toUInt()) {
            "sample rate must match target_sample_rate"
        }
        require(frame.samplesPerChannel.toInt() == samplesCount) {
            "frame must contain 10ms of samples"
        }
        require(frame.data.size == samplesCount * frame.numChannels.toInt()) {
            "slice must contain 10ms of samples"
        }

        val srcChannels = frame.numChannels.toInt()
        for (s in 0 until samplesCount) {
            for (ch in 0 until targetChannels) {
                val srcCh = if (ch < srcChannels) ch else 0
                val sample = frame.data[s * srcChannels + srcCh].toInt()
                val dstIdx = s * targetChannels + ch
                val mixed = (outBuffer[dstIdx].toInt() + sample).coerceIn(
                    Short.MIN_VALUE.toInt(),
                    Short.MAX_VALUE.toInt(),
                )
                outBuffer[dstIdx] = mixed.toShort()
            }
        }
        return AudioFrameInfo.Normal
    }
}

public class AudioMixer {
    private val sources: MutableMap<Int, AudioMixerSourceImpl<AudioMixerSource>> = mutableMapOf()
    private var mixedData: ShortArray = ShortArray(0)

    public fun addSource(source: AudioMixerSource) {
        val sourceImpl = AudioMixerSourceImpl(source)
        sources[sourceImpl.ssrc()] = sourceImpl
    }

    public fun removeSource(ssrc: Int) {
        sources.remove(ssrc)
    }

    public fun mix(numChannels: Int): ShortArray {
        val targetRate =
            sources.values
                .maxOfOrNull { it.preferredSampleRate() }
                ?.takeIf { it > 0 }
                ?: 48_000
        val samplesPerChannel = targetRate / 100
        val totalSamples = samplesPerChannel * numChannels.coerceAtLeast(1)
        val out = ShortArray(totalSamples)
        for (source in sources.values) {
            source.getAudioFrameWithInfo(targetRate, out, numChannels.coerceAtLeast(1))
        }
        mixedData = out
        return mixedData
    }

    public companion object {
        public fun new(): AudioMixer = AudioMixer()
    }
}
