// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc.internal

internal object WebrtcJni {
    val isLoaded: Boolean =
        run {
            System.loadLibrary("webrtc_jni")
            true
        }

    external fun initAndroid(jvmHandle: Long)

    external fun isAndroidInitialized(): Boolean

    external fun createPeerConnectionFactory(): Long

    external fun releasePeerConnectionFactory(factoryHandle: Long)

    external fun createPeerConnection(
        factoryHandle: Long,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Long

    external fun closePeerConnection(pcHandle: Long)

    external fun pcConnectionState(pcHandle: Long): Int

    external fun pcIceConnectionState(pcHandle: Long): Int

    external fun pcIceGatheringState(pcHandle: Long): Int

    external fun pcSignalingState(pcHandle: Long): Int

    external fun pcCreateOfferSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String

    external fun pcCreateAnswerSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String

    external fun pcSetLocalDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean

    external fun pcSetRemoteDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean

    external fun pcRemotePeerHandle(pcHandle: Long): Long

    external fun pcAddIceCandidate(
        pcHandle: Long,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean

    external fun pcRestartIce(pcHandle: Long)

    external fun pcGetStatsJson(pcHandle: Long): String

    external fun createDataChannel(
        pcHandle: Long,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Long

    external fun dcState(dcHandle: Long): Int

    external fun dcBufferedAmount(dcHandle: Long): Long

    external fun dcSend(
        dcHandle: Long,
        data: ByteArray,
        binary: Boolean,
    ): Boolean

    external fun dcClose(dcHandle: Long)

    external fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Long

    external fun audioSourceCaptureFrame(
        sourceHandle: Long,
        samples: ShortArray,
        sampleRate: Int,
        numChannels: Int,
        samplesPerChannel: Int,
    ): Boolean

    external fun createVideoSource(
        width: Int,
        height: Int,
        isScreencast: Boolean,
    ): Long

    external fun videoSourceCaptureFrame(
        sourceHandle: Long,
        width: Int,
        height: Int,
        timestampUs: Long,
        rotation: Int,
    )

    external fun createAudioTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long

    external fun createVideoTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long

    external fun trackEnabled(trackHandle: Long): Boolean

    external fun trackSetEnabled(
        trackHandle: Long,
        enabled: Boolean,
    ): Boolean

    external fun trackState(trackHandle: Long): Int

    external fun trackSetState(
        trackHandle: Long,
        state: Int,
    )

    external fun createRtpSender(
        pcHandle: Long,
        trackHandle: Long,
    ): Long

    external fun rtpSenderGetStatsJson(senderHandle: Long): String

    external fun createRtpReceiver(
        pcHandle: Long,
        trackHandle: Long,
    ): Long

    external fun rtpReceiverGetStatsJson(receiverHandle: Long): String

    external fun createRtpTransceiver(
        pcHandle: Long,
        mediaType: Int,
        direction: Int,
    ): Long

    external fun rtpTransceiverDirection(transceiverHandle: Long): Int

    external fun rtpTransceiverSetDirection(
        transceiverHandle: Long,
        direction: Int,
    )

    external fun rtpTransceiverStop(transceiverHandle: Long)

    external fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Long

    external fun apmProcessStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int

    external fun apmProcessReverseStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int

    external fun apmSetStreamDelayMs(
        apmHandle: Long,
        delayMs: Int,
    ): Int

    external fun audioResample(
        src: ShortArray,
        numFrames: Int,
        numChannels: Int,
        srcRate: Int,
        dstChannels: Int,
        dstRate: Int,
        dst: ShortArray,
    ): Int

    external fun audioMixFrames(
        src: ShortArray,
        dstAccum: ShortArray,
        totalSamples: Int,
    )

    external fun cryptorTransform(
        algorithm: Int,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray

    external fun ratchetKey(
        currentKey: ByteArray,
        salt: ByteArray,
    ): ByteArray

    external fun desktopCapturerSourceCount(sourceType: Int): Int

    external fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Long

    external fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String

    external fun desktopCapturerCaptureRgba(
        sourceId: Long,
        width: Int,
        height: Int,
    ): ByteArray

    external fun yuvI420ToArgb(
        srcY: ByteArray,
        srcStrideY: Int,
        srcU: ByteArray,
        srcStrideU: Int,
        srcV: ByteArray,
        srcStrideV: Int,
        dstArgb: ByteArray,
        dstStrideArgb: Int,
        width: Int,
        height: Int,
    )

    external fun yuvI420ToBgra(
        srcY: ByteArray,
        srcStrideY: Int,
        srcU: ByteArray,
        srcStrideU: Int,
        srcV: ByteArray,
        srcStrideV: Int,
        dstBgra: ByteArray,
        dstStrideBgra: Int,
        width: Int,
        height: Int,
    )

    external fun yuvI420ToAbgr(
        srcY: ByteArray,
        srcStrideY: Int,
        srcU: ByteArray,
        srcStrideU: Int,
        srcV: ByteArray,
        srcStrideV: Int,
        dstAbgr: ByteArray,
        dstStrideAbgr: Int,
        width: Int,
        height: Int,
    )

    external fun yuvI420ToRgba(
        srcY: ByteArray,
        srcStrideY: Int,
        srcU: ByteArray,
        srcStrideU: Int,
        srcV: ByteArray,
        srcStrideV: Int,
        dstRgba: ByteArray,
        dstStrideRgba: Int,
        width: Int,
        height: Int,
    )

    external fun yuvArgbToI420(
        srcArgb: ByteArray,
        srcStrideArgb: Int,
        dstY: ByteArray,
        dstStrideY: Int,
        dstU: ByteArray,
        dstStrideU: Int,
        dstV: ByteArray,
        dstStrideV: Int,
        width: Int,
        height: Int,
    )

    external fun yuvAbgrToI420(
        srcAbgr: ByteArray,
        srcStrideAbgr: Int,
        dstY: ByteArray,
        dstStrideY: Int,
        dstU: ByteArray,
        dstStrideU: Int,
        dstV: ByteArray,
        dstStrideV: Int,
        width: Int,
        height: Int,
    )

    external fun yuvArgbToRgb24(
        srcArgb: ByteArray,
        srcStrideArgb: Int,
        dstRgb24: ByteArray,
        dstStrideRgb24: Int,
        width: Int,
        height: Int,
    )

    external fun yuvI420ToNv12(
        srcY: ByteArray,
        srcStrideY: Int,
        srcU: ByteArray,
        srcStrideU: Int,
        srcV: ByteArray,
        srcStrideV: Int,
        dstY: ByteArray,
        dstStrideY: Int,
        dstUv: ByteArray,
        dstStrideUv: Int,
        width: Int,
        height: Int,
    )

    external fun yuvNv12ToI420(
        srcY: ByteArray,
        srcStrideY: Int,
        srcUv: ByteArray,
        srcStrideUv: Int,
        dstY: ByteArray,
        dstStrideY: Int,
        dstU: ByteArray,
        dstStrideU: Int,
        dstV: ByteArray,
        dstStrideV: Int,
        width: Int,
        height: Int,
    )

    external fun yuvNv12ToArgb(
        srcY: ByteArray,
        srcStrideY: Int,
        srcUv: ByteArray,
        srcStrideUv: Int,
        dstArgb: ByteArray,
        dstStrideArgb: Int,
        width: Int,
        height: Int,
    )

    external fun yuvNv12ToAbgr(
        srcY: ByteArray,
        srcStrideY: Int,
        srcUv: ByteArray,
        srcStrideUv: Int,
        dstAbgr: ByteArray,
        dstStrideAbgr: Int,
        width: Int,
        height: Int,
    )
}
