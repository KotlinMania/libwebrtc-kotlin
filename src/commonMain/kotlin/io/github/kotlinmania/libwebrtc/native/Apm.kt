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
    private val nativeHandle: Long =
        WebrtcNativeBridge.apmCreate(
            echoCanceller = echoCancellerEnabled,
            gainController = gainControllerEnabled,
            highPassFilter = highPassFilterEnabled,
            noiseSuppression = noiseSuppressionEnabled,
        )

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

        val rc = WebrtcNativeBridge.apmProcessStream(nativeHandle, data, sampleRate, numChannels)
        if (rc != 0) {
            throw RtcError(RtcErrorType.Internal, "Failed to process stream")
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

        val rc = WebrtcNativeBridge.apmProcessReverseStream(nativeHandle, data, sampleRate, numChannels)
        if (rc != 0) {
            throw RtcError(RtcErrorType.Internal, "Failed to process reverse stream")
        }
    }

    public fun setStreamDelayMs(delayMs: Int) {
        if (WebrtcNativeBridge.apmSetStreamDelayMs(nativeHandle, delayMs) != 0) {
            throw RtcError(RtcErrorType.Internal, "Failed to set stream delay")
        }
    }

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
