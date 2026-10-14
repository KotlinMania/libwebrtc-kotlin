// port-lint: tests native/peer_connection.rs
package io.github.kotlinmania.libwebrtc

import io.github.kotlinmania.libwebrtc.native.AudioMixer
import io.github.kotlinmania.libwebrtc.native.AudioMixerSource
import io.github.kotlinmania.libwebrtc.native.AudioProcessingModule
import io.github.kotlinmania.libwebrtc.native.AudioResampler
import io.github.kotlinmania.libwebrtc.native.DataPacketCryptor
import io.github.kotlinmania.libwebrtc.native.EncryptedPacket
import io.github.kotlinmania.libwebrtc.native.EncryptionAlgorithm
import io.github.kotlinmania.libwebrtc.native.EncryptionState
import io.github.kotlinmania.libwebrtc.native.FrameCryptor
import io.github.kotlinmania.libwebrtc.native.KeyProvider
import io.github.kotlinmania.libwebrtc.native.KeyProviderOptions
import io.github.kotlinmania.libwebrtc.native.abgrToI420
import io.github.kotlinmania.libwebrtc.native.i420ToAbgr
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
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
    fun videoFrameAndBuffers() {
        val i420 = I420Buffer.new(width = 64u, height = 48u)
        val frame =
            VideoFrame(
                rotation = VideoRotation.VideoRotation90,
                timestampUs = 1000L,
                buffer = i420,
            )
        assertEquals(VideoRotation.VideoRotation90, frame.rotation)
        assertEquals(1000L, frame.timestampUs)
        assertEquals(64u, frame.buffer.width())
        assertEquals(48u, frame.buffer.height())
        assertEquals(VideoBufferType.I420, frame.buffer.bufferType())
        assertEquals(64u, frame.buffer.toI420().width())

        val i420a = I420ABuffer(width = 64u, height = 48u)
        assertEquals(VideoBufferType.I420A, i420a.bufferType())
        assertEquals(64u, i420a.toI420().width())
        assertEquals(4u, i420a.strides().strideA)

        val i422 = I422Buffer.new(width = 64u, height = 48u)
        assertEquals(VideoBufferType.I422, i422.bufferType())
        assertEquals(64u, i422.toI420().width())

        val i444 = I444Buffer.new(width = 64u, height = 48u)
        assertEquals(VideoBufferType.I444, i444.bufferType())
        assertEquals(64u, i444.toI420().width())

        val i010 = I010Buffer.new(width = 64u, height = 48u)
        assertEquals(VideoBufferType.I010, i010.bufferType())
        assertEquals(64u, i010.toI420().width())

        val nv12 = NV12Buffer.new(width = 64u, height = 48u)
        assertEquals(VideoBufferType.NV12, nv12.bufferType())
        assertEquals(64u, nv12.toI420().width())
    }

    @Test
    fun mediaStreamAndTracks() {
        val audioTrack = RtcAudioTrack("audio-1")
        val videoTrack = RtcVideoTrack("video-1")

        assertEquals("audio", audioTrack.kind)
        assertEquals(true, audioTrack.enabled())
        audioTrack.setEnabled(false)
        assertEquals(false, audioTrack.enabled())

        val stream = MediaStream("stream-1")
        stream.addTrack(audioTrack)
        stream.addTrack(videoTrack)
        assertEquals(1, stream.audioTracks().size)
        assertEquals(1, stream.videoTracks().size)
        assertEquals("audio-1", stream.audioTracks().first().id())
        assertEquals("video-1", stream.videoTracks().first().id())
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
        assertEquals("track-1", sender.track()?.id())

        val dc = pc.createDataChannel("chat")
        assertEquals("chat", dc.label())

        pc.close()
        assertEquals(PeerConnectionState.Closed, pc.connectionState())
    }

    @Test
    fun rtcStatsVariants() {
        val codec =
            RtcStats.Codec(
                id = "codec-1",
                timestampUs = 123456L,
                payloadType = 111,
                mimeType = "audio/opus",
                clockRate = 48000L,
                channels = 2,
            )
        assertEquals("codec-1", codec.id)
        assertEquals("audio/opus", codec.mimeType)

        val dataChannelStat =
            RtcStats.DataChannel(
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

        val mediaSourceStat =
            RtcStats.MediaSource(
                id = "ms-1",
                timestampUs = 123456L,
                source = MediaSourceStats(trackIdentifier = "track-1", kind = "audio"),
                audio = AudioSourceStats(audioLevel = 0.5),
            )
        assertEquals("ms-1", mediaSourceStat.id)
        assertEquals("audio", mediaSourceStat.source.kind)
    }

    @Test
    fun audioSourceAndStream() =
        runTest {
            val options = AudioSourceOptions(echoCancellation = true)
            val source = NativeAudioSource(options, sampleRate = 48000u, numChannels = 2u)
            assertEquals(true, source.audioOptions().echoCancellation)
            assertEquals(48000u, source.sampleRate())
            assertEquals(2u, source.numChannels())

            val rtcSource = RtcAudioSource.Native(source)
            assertEquals(48000u, rtcSource.sampleRate())
            rtcSource.captureFrame(AudioFrame(sampleRate = 48000u, numChannels = 2u, samplesPerChannel = 480u))

            val track = RtcAudioTrack("audio-stream-track")
            val stream = NativeAudioStream(track, sampleRate = 48000, numChannels = 2)
            assertEquals(track, stream.track())
            assertEquals(false, stream.isClosed())
            stream.close()
            assertEquals(true, stream.isClosed())
        }

    @Test
    fun videoSourceAndStream() {
        val res = VideoResolution(1920u, 1080u)
        val source = NativeVideoSource(res)
        assertEquals(1920u, source.videoResolution().width)
        assertEquals(1080u, source.videoResolution().height)

        val rtcSource = RtcVideoSource.Native(source)
        assertEquals(1920u, rtcSource.videoResolution().width)

        val track = RtcVideoTrack("video-stream-track")
        val stream = NativeVideoStream(track)
        assertEquals(track, stream.track())
        assertEquals(false, stream.isClosed())
        stream.close()
        assertEquals(true, stream.isClosed())
    }

    @Test
    fun desktopCapturerOptionsAndState() {
        val options = DesktopCapturerOptions(DesktopCaptureSourceType.Window)
        assertEquals(DesktopCaptureSourceType.Window, options.sourceType)
        assertEquals(false, options.includeCursor)
        options.setIncludeCursor(true)
        assertEquals(true, options.includeCursor)

        val capturer = DesktopCapturer.new(options)
        assertNotNull(capturer)
        val sources = capturer.getSourceList()
        assertEquals(1, sources.size)
        assertEquals(1uL, sources[0].id())
        assertEquals("Primary Display", sources[0].title())

        var capturedFrame: DesktopFrame? = null
        var capturedError: CaptureError? = null
        assertTrue(capturer.selectSource(sources[0].id()))
        capturer.start { frame, error ->
            capturedFrame = frame
            capturedError = error
        }
        assertEquals(true, capturer.isCapturing())
        capturer.captureFrame()
        val desktopFrame = assertNotNull(capturedFrame)
        assertEquals(null, capturedError)
        assertTrue(desktopFrame.width() > 0)
        assertTrue(desktopFrame.height() > 0)
    }

    @Test
    fun nativeAudioProcessingMixerResamplerAndCryptor() {
        val apm =
            AudioProcessingModule.new(
                echoCancellerEnabled = true,
                gainControllerEnabled = true,
                highPassFilterEnabled = true,
                noiseSuppressionEnabled = true,
            )
        val samples = ShortArray(480) { 100 }
        apm.processStream(samples, 48000, 1)
        apm.processReverseStream(samples, 48000, 1)
        apm.setStreamDelayMs(10)

        val resampler = AudioResampler()
        val resampled = resampler.remixAndResample(samples, 480u, 1u, 48000u, 1u, 16000u)
        assertEquals(160, resampled.size)

        val mixer = AudioMixer.new()
        val source =
            object : AudioMixerSource {
                override fun ssrc(): Int = 1

                override fun preferredSampleRate(): UInt = 48000u

                override fun getAudioFrameWithInfo(targetSampleRate: UInt): AudioFrame =
                    AudioFrame(
                        data = ShortArray((targetSampleRate / 100u).toInt()) { 42 },
                        sampleRate = targetSampleRate,
                        numChannels = 1u,
                        samplesPerChannel = targetSampleRate / 100u,
                    )
            }
        mixer.addSource(source)
        val mixed = mixer.mix(1)
        assertEquals(480, mixed.size)
        assertEquals(42.toShort(), mixed[0])
        mixer.removeSource(1)

        val keyProvider = KeyProvider.new(KeyProviderOptions())
        val key = ByteArray(16) { it.toByte() }
        assertTrue(keyProvider.setSharedKey(0, key))
        assertNotNull(keyProvider.ratchetSharedKey(0))

        val dataCryptor = DataPacketCryptor.new(EncryptionAlgorithm.AesGcm, keyProvider)
        val payload = "secret webrtc data".encodeToByteArray()
        val encrypted: EncryptedPacket = dataCryptor.encrypt("alice", 0u, payload)
        val decrypted = dataCryptor.decrypt("alice", encrypted)
        assertContentEquals(payload, decrypted)

        val factory = PeerConnectionFactory.default()
        val pc = factory.createPeerConnection()
        val track = factory.createAudioTrack("enc-track")
        val sender = pc.addTrack(track)
        val frameCryptor = FrameCryptor.newForRtpSender(factory, "alice", EncryptionAlgorithm.AesGcm, keyProvider, sender)
        var observedState: EncryptionState? = null
        frameCryptor.onStateChange { _, state ->
            observedState = state
        }
        frameCryptor.setEnabled(true)
        assertEquals(true, frameCryptor.enabled())
        assertEquals(EncryptionState.Ok, observedState)
        frameCryptor.setKeyIndex(2)
        assertEquals(2, frameCryptor.keyIndex())
        frameCryptor.onStateChange(null)
        pc.close()
    }

    @Test
    fun yuvHelperConversions() {
        val width = 4
        val height = 4
        val srcY = ByteArray(width * height) { 128.toByte() }
        val srcU = ByteArray((width / 2) * (height / 2)) { 128.toByte() }
        val srcV = ByteArray((width / 2) * (height / 2)) { 128.toByte() }
        val dstRgba = ByteArray(width * height * 4)

        i420ToAbgr(
            srcY = srcY,
            srcStrideY = width.toUInt(),
            srcU = srcU,
            srcStrideU = (width / 2).toUInt(),
            srcV = srcV,
            srcStrideV = (width / 2).toUInt(),
            dst = dstRgba,
            dstStride = (width * 4).toUInt(),
            width = width,
            height = height,
        )
        assertEquals(255.toByte(), dstRgba[3])

        val dstY = ByteArray(width * height)
        val dstU = ByteArray((width / 2) * (height / 2))
        val dstV = ByteArray((width / 2) * (height / 2))
        abgrToI420(
            srcArgb = dstRgba,
            srcStrideArgb = (width * 4).toUInt(),
            dstY = dstY,
            dstStrideY = width.toUInt(),
            dstU = dstU,
            dstStrideU = (width / 2).toUInt(),
            dstV = dstV,
            dstStrideV = (width / 2).toUInt(),
            width = width,
            height = height,
        )
        assertTrue(dstY.isNotEmpty())
    }
}
