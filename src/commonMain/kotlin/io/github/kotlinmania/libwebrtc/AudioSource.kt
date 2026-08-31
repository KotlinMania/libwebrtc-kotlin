// port-lint: source libwebrtc/libwebrtc/src/audio_source.rs
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

public data class AudioSourceOptions(
    public val echoCancellation: Boolean = false,
    public val noiseSuppression: Boolean = false,
    public val autoGainControl: Boolean = false,
)

public class NativeAudioSource(
    options: AudioSourceOptions = AudioSourceOptions(),
    sampleRate: UInt = 48000u,
    numChannels: UInt = 2u,
    public val queueSizeMs: UInt = 1000u,
) {
    private var currentOptions: AudioSourceOptions = options
    private val currentSampleRate: UInt = sampleRate
    private val currentNumChannels: UInt = numChannels

    public fun clearBuffer() {
        // no-op buffer reset
    }

    public fun captureFrame(frame: AudioFrame) {
        // capture frame buffer
    }

    public fun setAudioOptions(options: AudioSourceOptions) {
        currentOptions = options
    }

    public fun audioOptions(): AudioSourceOptions = currentOptions

    public fun sampleRate(): UInt = currentSampleRate

    public fun numChannels(): UInt = currentNumChannels
}

public sealed class RtcAudioSource {
    public class Native(
        public val nativeSource: NativeAudioSource,
    ) : RtcAudioSource()

    public fun setAudioOptions(options: AudioSourceOptions) {
        when (this) {
            is Native -> nativeSource.setAudioOptions(options)
        }
    }

    public fun audioOptions(): AudioSourceOptions =
        when (this) {
            is Native -> nativeSource.audioOptions()
        }

    public fun sampleRate(): UInt =
        when (this) {
            is Native -> nativeSource.sampleRate()
        }

    public fun numChannels(): UInt =
        when (this) {
            is Native -> nativeSource.numChannels()
        }
}
