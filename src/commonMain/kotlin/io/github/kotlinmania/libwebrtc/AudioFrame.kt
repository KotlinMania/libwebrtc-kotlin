// port-lint: source audio_frame.rs
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

public class AudioFrame(
    public val data: ShortArray,
    public val sampleRate: UInt,
    public val numChannels: UInt,
    public val samplesPerChannel: UInt,
) {
    public constructor(
        sampleRate: UInt,
        numChannels: UInt,
        samplesPerChannel: UInt,
    ) : this(
        data = ShortArray((numChannels * samplesPerChannel).toInt()),
        sampleRate = sampleRate,
        numChannels = numChannels,
        samplesPerChannel = samplesPerChannel,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AudioFrame) return false
        return sampleRate == other.sampleRate &&
            numChannels == other.numChannels &&
            samplesPerChannel == other.samplesPerChannel &&
            data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + sampleRate.hashCode()
        result = 31 * result + numChannels.hashCode()
        result = 31 * result + samplesPerChannel.hashCode()
        return result
    }

    override fun toString(): String =
        "AudioFrame(sampleRate=$sampleRate, numChannels=$numChannels, samplesPerChannel=$samplesPerChannel, samples=${data.size})"

    public companion object {
        public fun new(
            sampleRate: UInt,
            numChannels: UInt,
            samplesPerChannel: UInt,
        ): AudioFrame =
            AudioFrame(
                data = ShortArray((numChannels * samplesPerChannel).toInt()),
                sampleRate = sampleRate,
                numChannels = numChannels,
                samplesPerChannel = samplesPerChannel,
            )
    }
}
