// port-lint: source audio_stream.rs
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

import io.github.kotlinmania.libwebrtc.native.NativeAudioStream as ImpNativeAudioStream

public class NativeAudioStreamOptions(
    public val audioTrack: RtcAudioTrack,
    public val sampleRate: Int,
    public val numChannels: Int,
    public val queueSizeFrames: Int? = null,
)

public class NativeAudioStream internal constructor(
    internal val handle: ImpNativeAudioStream,
) {
    private var closedState: Boolean = false

    public constructor(
        audioTrack: RtcAudioTrack,
        sampleRate: Int,
        numChannels: Int,
    ) : this(
        handle =
            ImpNativeAudioStream.new(
                audioTrack = audioTrack,
                sampleRate = sampleRate,
                numChannels = numChannels,
                queueSizeFrames = null,
            ),
    )

    public constructor(options: NativeAudioStreamOptions) : this(
        handle =
            ImpNativeAudioStream.new(
                audioTrack = options.audioTrack,
                sampleRate = options.sampleRate,
                numChannels = options.numChannels,
                queueSizeFrames = options.queueSizeFrames,
            ),
    )

    public val sampleRate: Int
        get() = handle.sampleRate

    public val numChannels: Int
        get() = handle.numChannels

    public fun track(): RtcAudioTrack = handle.track()

    public fun close() {
        closedState = true
        handle.close()
    }

    public fun isClosed(): Boolean = closedState || handle.frameQueue.closed

    public fun pollNext(): AudioFrame? = handle.pollNext()

    override fun toString(): String = "NativeAudioStream(track=${track()})"

    public companion object {
        public fun new(
            audioTrack: RtcAudioTrack,
            sampleRate: Int,
            numChannels: Int,
        ): NativeAudioStream =
            NativeAudioStream(
                handle =
                    ImpNativeAudioStream.new(
                        audioTrack = audioTrack,
                        sampleRate = sampleRate,
                        numChannels = numChannels,
                        queueSizeFrames = null,
                    ),
            )

        public fun withOptions(options: NativeAudioStreamOptions): NativeAudioStream =
            NativeAudioStream(
                handle =
                    ImpNativeAudioStream.new(
                        audioTrack = options.audioTrack,
                        sampleRate = options.sampleRate,
                        numChannels = options.numChannels,
                        queueSizeFrames = options.queueSizeFrames,
                    ),
            )
    }
}
