package io.github.kotlinmania.libwebrtc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        val frame =
            AudioFrame(
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
        val frame =
            AudioFrame(
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
        val caught: Throwable =
            try {
                throw e
            } catch (t: Throwable) {
                t
            }
        assertNotNull(caught.message)
        assertTrue(caught is RtcError)
        assertEquals(RtcErrorType.Internal, caught.errorType)
    }

    @Test
    fun sdpTypeParsingAndFormatting() {
        assertEquals(SdpType.Offer, SdpType.fromString("offer"))
        assertEquals(SdpType.PrAnswer, SdpType.fromString("pranswer"))
        assertEquals(SdpType.Answer, SdpType.fromString("answer"))
        assertEquals(SdpType.Rollback, SdpType.fromString("rollback"))

        assertEquals("offer", SdpType.Offer.value)
        assertEquals("pranswer", SdpType.PrAnswer.value)
        assertEquals("answer", SdpType.Answer.value)
        assertEquals("rollback", SdpType.Rollback.value)

        assertFailsWith<IllegalArgumentException> {
            SdpType.fromString("unknown")
        }
    }

    @Test
    fun sessionDescriptionParse() {
        val sdp = "v=0\r\no=- 12345 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n"
        val desc = SessionDescription.parse(sdp, SdpType.Offer)
        assertEquals(sdp, desc.sdp)
        assertEquals(SdpType.Offer, desc.sdpType)
        assertEquals(sdp, desc.toString())

        assertFailsWith<SdpParseError> {
            SessionDescription.parse("", SdpType.Offer)
        }
    }

    @Test
    fun iceCandidateParse() {
        val candStr = "candidate:1 1 UDP 2130706431 192.168.1.1 5000 typ host"
        val cand = IceCandidate.parse("audio", 0, candStr)
        assertEquals("audio", cand.sdpMid)
        assertEquals(0, cand.sdpMLineIndex)
        assertEquals(candStr, cand.candidate)
        assertEquals(candStr, cand.toString())

        assertFailsWith<SdpParseError> {
            IceCandidate.parse("audio", 0, "")
        }
    }

    @Test
    fun rtpParametersDefaults() {
        val params = RtpParameters()
        assertTrue(params.codecs.isEmpty())
        assertTrue(params.headerExtensions.isEmpty())
        assertEquals("", params.rtcp.cname)
        assertEquals(false, params.rtcp.reducedSize)

        val enc = RtpEncodingParameters()
        assertEquals(true, enc.active)
        assertEquals(Priority.Low, enc.priority)
        assertEquals("", enc.rid)
    }

    @Test
    fun rtpTransceiverInitDefaults() {
        val init = RtpTransceiverInit()
        assertEquals(RtpTransceiverDirection.SendRecv, init.direction)
        assertTrue(init.streamIds.isEmpty())
        assertTrue(init.sendEncodings.isEmpty())
    }

    @Test
    fun dataChannelInitDefaults() {
        val dc = DataChannelInit()
        assertEquals(true, dc.ordered)
        assertEquals(-1, dc.id)
        assertEquals("", dc.protocol)
        assertEquals(false, dc.negotiated)

        val buf = DataBuffer(byteArrayOf(1, 2, 3), true)
        assertEquals(3, buf.data.size)
        assertTrue(buf.binary)
    }
}
