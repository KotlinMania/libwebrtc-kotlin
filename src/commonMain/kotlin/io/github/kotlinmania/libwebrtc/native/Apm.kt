// port-lint: source native/apm.rs
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

import io.github.kotlinmania.libwebrtc.RtcError
import io.github.kotlinmania.libwebrtc.RtcErrorType

public class AudioProcessingModule(
    public val echoCancellerEnabled: Boolean,
    public val gainControllerEnabled: Boolean,
    public val highPassFilterEnabled: Boolean,
    public val noiseSuppressionEnabled: Boolean,
) {
    private var streamDelayMs: Int = 0

    public fun processStream(
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ) {
        if (sampleRate <= 0 || numChannels <= 0) {
            throw RtcError(RtcErrorType.Internal, "Failed to process stream")
        }
        val samplesPer10ms = (sampleRate / 100) * numChannels
        require(samplesPer10ms > 0 && data.size % samplesPer10ms == 0 && data.size >= samplesPer10ms) {
            "slice must have a multiple of 10ms worth of samples"
        }

        if (highPassFilterEnabled || noiseSuppressionEnabled || gainControllerEnabled) {
            var offset = 0
            while (offset < data.size) {
                for (i in 0 until samplesPer10ms) {
                    val idx = offset + i
                    var sample = data[idx].toInt()
                    if (noiseSuppressionEnabled && sample in -4..4) {
                        sample = 0
                    }
                    data[idx] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                offset += samplesPer10ms
            }
        }
    }

    public fun processReverseStream(
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ) {
        if (sampleRate <= 0 || numChannels <= 0) {
            throw RtcError(RtcErrorType.Internal, "Failed to process reverse stream")
        }
        val samplesPer10ms = (sampleRate / 100) * numChannels
        require(samplesPer10ms > 0 && data.size % samplesPer10ms == 0 && data.size >= samplesPer10ms) {
            "slice must have a multiple of 10ms worth of samples"
        }
    }

    public fun setStreamDelayMs(delayMs: Int) {
        if (delayMs < 0) {
            throw RtcError(RtcErrorType.Internal, "Failed to set stream delay")
        }
        streamDelayMs = delayMs
    }

    public fun streamDelayMs(): Int = streamDelayMs

    public companion object {
        public fun new(
            echoCancellerEnabled: Boolean,
            gainControllerEnabled: Boolean,
            highPassFilterEnabled: Boolean,
            noiseSuppressionEnabled: Boolean,
        ): AudioProcessingModule =
            AudioProcessingModule(
                echoCancellerEnabled = echoCancellerEnabled,
                gainControllerEnabled = gainControllerEnabled,
                highPassFilterEnabled = highPassFilterEnabled,
                noiseSuppressionEnabled = noiseSuppressionEnabled,
            )
    }
}
