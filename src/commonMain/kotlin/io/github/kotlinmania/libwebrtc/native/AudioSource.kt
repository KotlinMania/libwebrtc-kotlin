// port-lint: source native/audio_source.rs
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
import io.github.kotlinmania.libwebrtc.AudioSourceOptions
import io.github.kotlinmania.libwebrtc.RtcError
import io.github.kotlinmania.libwebrtc.RtcErrorType

internal class NativeAudioSource(
    private var currentOptions: AudioSourceOptions,
    private val sourceSampleRate: UInt,
    private val sourceNumChannels: UInt,
    private val queueSizeSamples: UInt,
) {
    private val bufferedFrames: MutableList<AudioFrame> = mutableListOf()

    /**
     * Creates a new [NativeAudioSource].
     *
     * Arguments:
     * - `options` - Configuration options for the source (e.g. echo cancellation, noise suppression).
     * - `sampleRate` - Sampling rate in Hz (for example, `48000`).
     * - `numChannels` - Number of audio channels (`1` for mono, `2` for stereo, etc.).
     * - `queueSizeMs` - Size of the internal buffering queue, in milliseconds.
     *
     * Behavior:
     * - If `queueSizeMs` is zero, buffering is disabled and audio frames are delivered directly to
     *   webrtc sinks. In this mode, the caller must provide 10 ms frames (`sampleRate / 100` samples
     *   per channel) when calling [captureFrame].
     * - If `queueSizeMs` is non-zero, buffering is enabled. The value must be a multiple of 10,
     *   representing the total buffering duration in milliseconds. Frames will be queued and flushed
     *   to sinks asynchronously once the buffer reaches the configured threshold.
     *
     * Throws [IllegalArgumentException] if `queueSizeMs` is not a multiple of 10.
     */
    internal fun sysHandle(): NativeAudioSource = this

    internal fun setAudioOptions(options: AudioSourceOptions) {
        currentOptions = options
    }

    internal fun audioOptions(): AudioSourceOptions = currentOptions

    internal fun sampleRate(): UInt = sourceSampleRate

    internal fun numChannels(): UInt = sourceNumChannels

    internal fun clearBuffer() {
        bufferedFrames.clear()
    }

    internal fun captureFrame(frame: AudioFrame) {
        if (sourceSampleRate != frame.sampleRate || sourceNumChannels != frame.numChannels) {
            throw RtcError(
                RtcErrorType.InvalidState,
                "sample_rate and num_channels don't match",
            )
        }

        val numCh = sourceNumChannels.toInt()
        if (numCh <= 0) {
            throw RtcError(RtcErrorType.InvalidState, "invalid channel count")
        }

        if (queueSizeSamples == 0u) {
            val expectedFramesPerCh = (sourceSampleRate / 100u).toInt()
            if (frame.data.size % numCh != 0) {
                throw RtcError(
                    RtcErrorType.InvalidState,
                    "frame.data length not divisible by channel count",
                )
            }
            val nbFrames = frame.data.size / numCh
            if (nbFrames != expectedFramesPerCh) {
                throw RtcError(
                    RtcErrorType.InvalidState,
                    "direct capture requires 10ms frames: got $nbFrames frames, expected $expectedFramesPerCh",
                )
            }
            bufferedFrames.add(frame)
            return
        }

        val chunkSize = queueSizeSamples.toInt().coerceAtLeast(numCh)
        var offset = 0
        while (offset < frame.data.size) {
            val end = (offset + chunkSize).coerceAtMost(frame.data.size)
            val chunk = frame.data.copyOfRange(offset, end)
            val nbFrames = chunk.size / numCh
            bufferedFrames.add(
                AudioFrame(
                    data = chunk,
                    sampleRate = sourceSampleRate,
                    numChannels = sourceNumChannels,
                    samplesPerChannel = nbFrames.toUInt(),
                ),
            )
            offset = end
        }
    }

    internal fun capturedFrames(): List<AudioFrame> = bufferedFrames.toList()

    internal companion object {
        internal fun new(
            options: AudioSourceOptions,
            sampleRate: UInt,
            numChannels: UInt,
            queueSizeMs: UInt,
        ): NativeAudioSource {
            require(queueSizeMs % 10u == 0u) {
                "queue_size_ms must be a multiple of 10"
            }
            val queueSizeSamples = (queueSizeMs * sampleRate * numChannels) / 1000u
            return NativeAudioSource(
                currentOptions = options,
                sourceSampleRate = sampleRate,
                sourceNumChannels = numChannels,
                queueSizeSamples = queueSizeSamples,
            )
        }

        internal fun fromSysHandle(sysHandle: NativeAudioSource? = null): NativeAudioSource =
            sysHandle ?: NativeAudioSource(
                currentOptions = AudioSourceOptions(),
                sourceSampleRate = 0u,
                sourceNumChannels = 0u,
                queueSizeSamples = 0u,
            )
    }
}
