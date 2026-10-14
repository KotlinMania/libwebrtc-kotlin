// port-lint: source audio_source.rs
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

import io.github.kotlinmania.libwebrtc.native.NativeAudioSource as ImpNativeAudioSource

public class AudioSourceOptions(
    public val echoCancellation: Boolean = false,
    public val noiseSuppression: Boolean = false,
    public val autoGainControl: Boolean = false,
) {
    override fun toString(): String =
        "AudioSourceOptions(echoCancellation=$echoCancellation, noiseSuppression=$noiseSuppression, autoGainControl=$autoGainControl)"
}

public sealed class RtcAudioSource {
    public class Native(
        public val source: NativeAudioSource,
    ) : RtcAudioSource()

    public fun setAudioOptions(options: AudioSourceOptions) {
        when (this) {
            is Native -> source.setAudioOptions(options)
        }
    }

    public fun audioOptions(): AudioSourceOptions =
        when (this) {
            is Native -> source.audioOptions()
        }

    public fun sampleRate(): UInt =
        when (this) {
            is Native -> source.sampleRate()
        }

    public fun numChannels(): UInt =
        when (this) {
            is Native -> source.numChannels()
        }

    public fun clearBuffer() {
        when (this) {
            is Native -> source.clearBuffer()
        }
    }

    public fun captureFrame(frame: AudioFrame) {
        when (this) {
            is Native -> source.captureFrame(frame)
        }
    }
}

public class NativeAudioSource internal constructor(
    internal val handle: ImpNativeAudioSource,
) {
    public constructor(
        options: AudioSourceOptions = AudioSourceOptions(),
        sampleRate: UInt = 48_000u,
        numChannels: UInt = 1u,
        queueSizeMs: UInt = 1000u,
    ) : this(
        handle =
            ImpNativeAudioSource.new(
                options = options,
                sampleRate = sampleRate,
                numChannels = numChannels,
                queueSizeMs = queueSizeMs,
            ),
    )

    public val queueSizeMs: UInt
        get() =
            if (handle.sampleRate() == 0u || handle.numChannels() == 0u) {
                0u
            } else {
                1000u
            }

    public fun setAudioOptions(options: AudioSourceOptions) {
        handle.setAudioOptions(options)
    }

    public fun audioOptions(): AudioSourceOptions = handle.audioOptions()

    public fun sampleRate(): UInt = handle.sampleRate()

    public fun numChannels(): UInt = handle.numChannels()

    public fun clearBuffer() {
        handle.clearBuffer()
    }

    public fun captureFrame(frame: AudioFrame) {
        handle.captureFrame(frame)
    }

    public fun capturedFrames(): List<AudioFrame> = handle.capturedFrames()

    override fun toString(): String =
        "NativeAudioSource(sampleRate=${sampleRate()}, numChannels=${numChannels()})"

    public companion object {
        public fun new(
            options: AudioSourceOptions,
            sampleRate: UInt,
            numChannels: UInt,
            queueSizeMs: UInt,
        ): NativeAudioSource =
            NativeAudioSource(
                handle =
                    ImpNativeAudioSource.new(
                        options = options,
                        sampleRate = sampleRate,
                        numChannels = numChannels,
                        queueSizeMs = queueSizeMs,
                    ),
            )

        public fun default(): NativeAudioSource =
            new(
                options = AudioSourceOptions(),
                sampleRate = 48_000u,
                numChannels = 1u,
                queueSizeMs = 1000u,
            )
    }
}
