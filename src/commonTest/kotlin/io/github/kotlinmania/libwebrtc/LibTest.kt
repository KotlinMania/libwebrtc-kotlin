package io.github.kotlinmania.libwebrtc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LibTest {
    @Test
    fun mediaTypeCarriesFourVariantsInUpstreamOrder() {
        val values = MediaType.entries
        assertEquals(4, values.size)
        assertEquals(MediaType.Audio, values[0])
        assertEquals(MediaType.Video, values[1])
        assertEquals(MediaType.Data, values[2])
        assertEquals(MediaType.Unsupported, values[3])
    }

    @Test
    fun rtcErrorTypeCarriesThreeVariantsInUpstreamOrder() {
        val values = RtcErrorType.entries
        assertEquals(3, values.size)
        assertEquals(RtcErrorType.Internal, values[0])
        assertEquals(RtcErrorType.InvalidSdp, values[1])
        assertEquals(RtcErrorType.InvalidState, values[2])
    }

    @Test
    fun rtcErrorFormatsLikeUpstreamThiserror() {
        val e = RtcError(RtcErrorType.InvalidSdp, "could not parse offer")
        assertEquals(RtcErrorType.InvalidSdp, e.errorType)
        assertEquals("could not parse offer", e.rtcMessage)
        assertEquals("an RtcError occured: InvalidSdp - could not parse offer", e.message)
    }

    @Test
    fun audioFrameOwnedConstructorZeroFillsForChannelTimesSamples() {
        val frame = AudioFrame(
            sampleRate = 48_000u,
            numChannels = 2u,
            samplesPerChannel = 480u,
        )
        assertEquals(48_000u, frame.sampleRate)
        assertEquals(2u, frame.numChannels)
        assertEquals(480u, frame.samplesPerChannel)
        assertEquals(960, frame.data.size)
        assertTrue(frame.data.all { it == 0.toShort() })
    }

    @Test
    fun audioFrameDirectConstructorPreservesProvidedSamples() {
        val samples = shortArrayOf(1, 2, 3, 4)
        val frame = AudioFrame(
            data = samples,
            sampleRate = 16_000u,
            numChannels = 1u,
            samplesPerChannel = 4u,
        )
        assertEquals(samples, frame.data)
    }

    @Test
    fun rtcErrorIsAThrowable() {
        val e = RtcError(RtcErrorType.Internal, "boom")
        val caught: Throwable = try {
            throw e
        } catch (t: Throwable) {
            t
        }
        assertNotNull(caught.message)
        assertTrue(caught is RtcError)
        assertEquals(RtcErrorType.Internal, caught.errorType)
    }
}
