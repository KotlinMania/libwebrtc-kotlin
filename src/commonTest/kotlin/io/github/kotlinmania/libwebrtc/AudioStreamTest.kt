// port-lint: tests native/audio_stream.rs
package io.github.kotlinmania.libwebrtc

import io.github.kotlinmania.libwebrtc.native.AudioFrameQueue
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalAtomicApi::class)
class AudioStreamTest {
    private fun testFrame(marker: Short): AudioFrame =
        AudioFrame(
            data = shortArrayOf(marker),
            sampleRate = 48_000u,
            numChannels = 1u,
            samplesPerChannel = 1u,
        )

    private fun popMarker(queue: AudioFrameQueue): Short? = queue.tryPop()?.data?.get(0)

    @Test
    fun boundedQueuePreservesFifoOrderUnderCapacity() {
        val queue = AudioFrameQueue.new(3)

        queue.push(testFrame(1))
        queue.push(testFrame(2))
        queue.push(testFrame(3))

        assertEquals(1.toShort(), popMarker(queue))
        assertEquals(2.toShort(), popMarker(queue))
        assertEquals(3.toShort(), popMarker(queue))
        assertNull(popMarker(queue))
    }

    @Test
    fun boundedQueueDropsOldestWhenFull() {
        val queue = AudioFrameQueue.new(2)

        queue.push(testFrame(1))
        queue.push(testFrame(2))
        queue.push(testFrame(3))

        assertEquals(1uL, queue.droppedFrames.load())
        assertEquals(2.toShort(), popMarker(queue))
        assertEquals(3.toShort(), popMarker(queue))
        assertNull(popMarker(queue))
    }

    @Test
    fun unboundedQueueRetainsAllFrames() {
        val queue = AudioFrameQueue.new(null)

        for (marker in 1..4) {
            queue.push(testFrame(marker.toShort()))
        }

        for (marker in 1..4) {
            assertEquals(marker.toShort(), popMarker(queue))
        }
        assertNull(popMarker(queue))
        assertEquals(0uL, queue.droppedFrames.load())
    }

    @Test
    fun closeClearsBufferAndRejectsFuturePushes() {
        val queue = AudioFrameQueue.new(2)

        queue.push(testFrame(1))
        queue.close()
        queue.push(testFrame(2))

        assertNull(popMarker(queue))
    }
}
