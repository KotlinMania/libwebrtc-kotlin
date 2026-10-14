// port-lint: source native/audio_stream.rs
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
import io.github.kotlinmania.libwebrtc.RtcAudioTrack

internal class NativeAudioStream(
    private val audioTrack: RtcAudioTrack,
    internal val sampleRate: Int,
    internal val numChannels: Int,
    queueSizeFrames: Int? = null,
) {
    internal val frameQueue: AudioFrameQueue = AudioFrameQueue.new(queueSizeFrames)
    internal val observer: AudioTrackObserver = AudioTrackObserver(frameQueue)

    internal fun track(): RtcAudioTrack = audioTrack

    internal fun close() {
        frameQueue.close()
    }

    internal fun pollNext(): AudioFrame? = frameQueue.tryPop()

    internal companion object {
        internal fun new(
            audioTrack: RtcAudioTrack,
            sampleRate: Int,
            numChannels: Int,
            queueSizeFrames: Int?,
        ): NativeAudioStream =
            NativeAudioStream(
                audioTrack = audioTrack,
                sampleRate = sampleRate,
                numChannels = numChannels,
                queueSizeFrames = queueSizeFrames,
            )
    }
}

internal class AudioTrackObserver(
    internal val frameQueue: AudioFrameQueue,
) {
    internal fun onData(
        data: ShortArray,
        sampleRate: Int,
        nbChannels: Int,
        nbFrames: Int,
    ) {
        frameQueue.push(
            AudioFrame(
                data = data.copyOf(),
                sampleRate = sampleRate.toUInt(),
                numChannels = nbChannels.toUInt(),
                samplesPerChannel = nbFrames.toUInt(),
            ),
        )
    }
}

internal sealed class AudioFrameQueueKind {
    internal class Bounded(
        internal val queue: BoundedAudioFrameQueue,
    ) : AudioFrameQueueKind()

    internal class Unbounded(
        internal val queue: UnboundedAudioFrameQueue,
    ) : AudioFrameQueueKind()
}

internal class BoundedAudioFrameQueue(
    internal val capacity: Int,
) {
    internal val frames: ArrayDeque<AudioFrame> = ArrayDeque(capacity)
}

internal class UnboundedAudioFrameQueue {
    internal val frames: ArrayDeque<AudioFrame> = ArrayDeque()
}

internal class AudioFrameQueue private constructor(
    private val kind: AudioFrameQueueKind,
) {
    internal var closed: Boolean = false
        private set
    internal var droppedFrames: ULong = 0uL
        private set
    private var waker: (() -> Unit)? = null

    internal fun push(frame: AudioFrame) {
        if (closed) {
            return
        }

        when (val k = kind) {
            is AudioFrameQueueKind.Bounded -> pushBounded(k.queue, frame)
            is AudioFrameQueueKind.Unbounded -> k.queue.frames.addLast(frame)
        }

        wakeReceiver()
    }

    private fun pushBounded(queue: BoundedAudioFrameQueue, frame: AudioFrame) {
        while (queue.frames.size >= queue.capacity) {
            val dropped = queue.frames.removeFirstOrNull() != null
            if (dropped) {
                recordDrop()
            } else {
                return
            }
        }
        queue.frames.addLast(frame)
    }

    internal fun close() {
        closed = true
        wakeReceiver()

        when (val k = kind) {
            is AudioFrameQueueKind.Bounded -> k.queue.frames.clear()
            is AudioFrameQueueKind.Unbounded -> k.queue.frames.clear()
        }
    }

    internal fun pollRecv(onWake: (() -> Unit)? = null): AudioFrame? {
        val popped = tryPop()
        if (popped != null) {
            return popped
        }
        if (closed) {
            return null
        }
        waker = onWake
        val retry = tryPop()
        if (retry != null) {
            waker = null
            return retry
        }
        return null
    }

    internal fun tryPop(): AudioFrame? =
        when (val k = kind) {
            is AudioFrameQueueKind.Bounded -> k.queue.frames.removeFirstOrNull()
            is AudioFrameQueueKind.Unbounded -> k.queue.frames.removeFirstOrNull()
        }

    private fun wakeReceiver() {
        val currentWaker = waker
        waker = null
        currentWaker?.invoke()
    }

    private fun recordDrop() {
        droppedFrames += 1uL
    }

    internal companion object {
        internal fun new(capacity: Int?): AudioFrameQueue {
            val validCapacity = capacity?.takeIf { it > 0 }
            val kind =
                if (validCapacity != null) {
                    AudioFrameQueueKind.Bounded(BoundedAudioFrameQueue(validCapacity))
                } else {
                    AudioFrameQueueKind.Unbounded(UnboundedAudioFrameQueue())
                }
            return AudioFrameQueue(kind)
        }
    }
}
