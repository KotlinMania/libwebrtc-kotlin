// port-lint: tests lib.rs
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
        for (sample in frame.data) {
            assertEquals(0.toShort(), sample)
        }
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

    @Test
    fun videoFrameAndBuffer() {
        val i420 = I420Buffer(
            width = 640,
            height = 480,
            dataY = ByteArray(640 * 480),
            strideY = 640,
            dataU = ByteArray(320 * 240),
            strideU = 320,
            dataV = ByteArray(320 * 240),
            strideV = 320,
        )
        val frame = VideoFrame(
            rotation = VideoRotation.VideoRotation90,
            timestampUs = 1000L,
            buffer = i420,
        )
        assertEquals(VideoRotation.VideoRotation90, frame.rotation)
        assertEquals(1000L, frame.timestampUs)
        assertEquals(640, frame.buffer.width)
        assertEquals(480, frame.buffer.height)
        assertEquals(VideoBufferType.I420, frame.buffer.type)
        assertEquals(i420, frame.buffer.toI420())
    }

    @Test
    fun mediaStreamAndTracks() {
        val audioTrack = RtcAudioTrack("audio-1")
        val videoTrack = RtcVideoTrack("video-1")

        assertEquals("audio", audioTrack.kind)
        assertEquals(true, audioTrack.enabled)
        audioTrack.setEnabled(false)
        assertEquals(false, audioTrack.enabled)

        val stream = MediaStream("stream-1")
        stream.addTrack(audioTrack)
        stream.addTrack(videoTrack)
        assertEquals(1, stream.audioTracks().size)
        assertEquals(1, stream.videoTracks().size)
        assertEquals("audio-1", stream.audioTracks().first().id)
        assertEquals("video-1", stream.videoTracks().first().id)
    }

    @Test
    fun peerConnectionWorkflow() {
        val factory = PeerConnectionFactory.withPlatformAdm()
        val pc = factory.createPeerConnection()
        assertEquals(PeerConnectionState.New, pc.connectionState())
        assertEquals(SignalingState.Stable, pc.signalingState())

        val sdp = "v=0\r\no=- 12345 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n"
        val offer = SessionDescription.parse(sdp, SdpType.Offer)
        pc.setLocalDescription(offer)
        assertEquals(SignalingState.HaveLocalOffer, pc.signalingState())

        val answer = SessionDescription.parse(sdp, SdpType.Answer)
        pc.setRemoteDescription(answer)
        assertEquals(SignalingState.Stable, pc.signalingState())

        val track = factory.createAudioTrack("track-1")
        val sender = pc.addTrack(track)
        assertEquals(1, pc.senders().size)
        assertEquals("track-1", sender.track()?.id)

        val dc = pc.createDataChannel("chat")
        assertEquals("chat", dc.label)

        pc.close()
        assertEquals(PeerConnectionState.Closed, pc.connectionState())
    }

    @Test
    fun rtcStatsVariants() {
        val codec = RtcStats.Codec(
            id = "codec-1",
            timestampUs = 123456L,
            payloadType = 111,
            mimeType = "audio/opus",
            clockRate = 48000L,
            channels = 2,
        )
        assertEquals("codec-1", codec.id)
        assertEquals("audio/opus", codec.mimeType)

        val dataChannelStat = RtcStats.DataChannel(
            id = "dc-1",
            timestampUs = 123456L,
            label = "data",
            protocol = "",
            dataChannelIdentifier = 1,
            state = DataChannelState.Open,
            messagesSent = 10,
            bytesSent = 100,
            messagesReceived = 5,
            bytesReceived = 50,
        )
        assertEquals(DataChannelState.Open, dataChannelStat.state)
        assertEquals(10L, dataChannelStat.messagesSent)
    }
}
