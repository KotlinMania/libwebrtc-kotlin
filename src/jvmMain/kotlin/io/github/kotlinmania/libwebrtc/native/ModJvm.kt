// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.internal.WebrtcJni

internal actual val isWebPlatform: Boolean = false

internal actual val isNativePlatform: Boolean = true

internal actual val isAndroidPlatform: Boolean = false

internal actual fun platformInitializeAndroid(vm: Any?) {
    val handle = (vm as? Number)?.toLong() ?: 1L
    WebrtcNativeBridge.initAndroid(handle)
}

internal actual object WebrtcNativeBridge {
    init {
        check(WebrtcJni.isLoaded)
    }

    actual fun initAndroid(jvmHandle: Long) {
        WebrtcJni.initAndroid(jvmHandle)
    }

    actual fun isAndroidInitialized(): Boolean = WebrtcJni.isAndroidInitialized()

    actual fun createPeerConnectionFactory(): Long = WebrtcJni.createPeerConnectionFactory()

    actual fun releasePeerConnectionFactory(factoryHandle: Long) {
        WebrtcJni.releasePeerConnectionFactory(factoryHandle)
    }

    actual fun createPeerConnection(
        factoryHandle: Long,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Long =
        WebrtcJni.createPeerConnection(
            factoryHandle,
            iceTransportType,
            continualGatheringPolicy,
            iceServersCount,
        )

    actual fun closePeerConnection(pcHandle: Long) {
        WebrtcJni.closePeerConnection(pcHandle)
    }

    actual fun pcConnectionState(pcHandle: Long): Int = WebrtcJni.pcConnectionState(pcHandle)

    actual fun pcIceConnectionState(pcHandle: Long): Int = WebrtcJni.pcIceConnectionState(pcHandle)

    actual fun pcIceGatheringState(pcHandle: Long): Int = WebrtcJni.pcIceGatheringState(pcHandle)

    actual fun pcSignalingState(pcHandle: Long): Int = WebrtcJni.pcSignalingState(pcHandle)

    actual fun pcCreateOfferSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String = WebrtcJni.pcCreateOfferSdp(pcHandle, iceRestart, offerAudio, offerVideo)

    actual fun pcCreateAnswerSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String = WebrtcJni.pcCreateAnswerSdp(pcHandle, iceRestart, offerAudio, offerVideo)

    actual fun pcSetLocalDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = WebrtcJni.pcSetLocalDescription(pcHandle, sdpType, sdp)

    actual fun pcSetRemoteDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = WebrtcJni.pcSetRemoteDescription(pcHandle, sdpType, sdp)

    actual fun pcRemotePeerHandle(pcHandle: Long): Long = WebrtcJni.pcRemotePeerHandle(pcHandle)

    actual fun pcAddIceCandidate(
        pcHandle: Long,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean = WebrtcJni.pcAddIceCandidate(pcHandle, sdpMid, sdpMlineIndex, candidate)

    actual fun pcRestartIce(pcHandle: Long) {
        WebrtcJni.pcRestartIce(pcHandle)
    }

    actual fun pcGetStatsJson(pcHandle: Long): String = WebrtcJni.pcGetStatsJson(pcHandle)

    actual fun createDataChannel(
        pcHandle: Long,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Long = WebrtcJni.createDataChannel(pcHandle, label, id, ordered, negotiated)

    actual fun dcState(dcHandle: Long): Int = WebrtcJni.dcState(dcHandle)

    actual fun dcBufferedAmount(dcHandle: Long): Long = WebrtcJni.dcBufferedAmount(dcHandle)

    actual fun dcSend(
        dcHandle: Long,
        data: ByteArray,
        binary: Boolean,
    ): Boolean = WebrtcJni.dcSend(dcHandle, data, binary)

    actual fun dcClose(dcHandle: Long) {
        WebrtcJni.dcClose(dcHandle)
    }

    actual fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Long =
        WebrtcJni.createAudioSource(
            sampleRate,
            numChannels,
            echoCancellation,
            noiseSuppression,
            autoGainControl,
        )

    actual fun audioSourceCaptureFrame(
        sourceHandle: Long,
        samples: ShortArray,
        sampleRate: Int,
        numChannels: Int,
        samplesPerChannel: Int,
    ): Boolean =
        WebrtcJni.audioSourceCaptureFrame(
            sourceHandle,
            samples,
            sampleRate,
            numChannels,
            samplesPerChannel,
        )

    actual fun createVideoSource(
        width: Int,
        height: Int,
        isScreencast: Boolean,
    ): Long = WebrtcJni.createVideoSource(width, height, isScreencast)

    actual fun videoSourceCaptureFrame(
        sourceHandle: Long,
        width: Int,
        height: Int,
        timestampUs: Long,
        rotation: Int,
    ) {
        WebrtcJni.videoSourceCaptureFrame(sourceHandle, width, height, timestampUs, rotation)
    }

    actual fun createAudioTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long = WebrtcJni.createAudioTrack(factoryHandle, id, sourceHandle)

    actual fun createVideoTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long = WebrtcJni.createVideoTrack(factoryHandle, id, sourceHandle)

    actual fun trackEnabled(trackHandle: Long): Boolean = WebrtcJni.trackEnabled(trackHandle)

    actual fun trackSetEnabled(
        trackHandle: Long,
        enabled: Boolean,
    ): Boolean = WebrtcJni.trackSetEnabled(trackHandle, enabled)

    actual fun trackState(trackHandle: Long): Int = WebrtcJni.trackState(trackHandle)

    actual fun trackSetState(
        trackHandle: Long,
        state: Int,
    ) {
        WebrtcJni.trackSetState(trackHandle, state)
    }

    actual fun createRtpSender(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = WebrtcJni.createRtpSender(pcHandle, trackHandle)

    actual fun rtpSenderGetStatsJson(senderHandle: Long): String = WebrtcJni.rtpSenderGetStatsJson(senderHandle)

    actual fun createRtpReceiver(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = WebrtcJni.createRtpReceiver(pcHandle, trackHandle)

    actual fun rtpReceiverGetStatsJson(receiverHandle: Long): String = WebrtcJni.rtpReceiverGetStatsJson(receiverHandle)

    actual fun createRtpTransceiver(
        pcHandle: Long,
        mediaType: Int,
        direction: Int,
    ): Long = WebrtcJni.createRtpTransceiver(pcHandle, mediaType, direction)

    actual fun rtpTransceiverDirection(transceiverHandle: Long): Int =
        WebrtcJni.rtpTransceiverDirection(transceiverHandle)

    actual fun rtpTransceiverSetDirection(
        transceiverHandle: Long,
        direction: Int,
    ) {
        WebrtcJni.rtpTransceiverSetDirection(transceiverHandle, direction)
    }

    actual fun rtpTransceiverStop(transceiverHandle: Long) {
        WebrtcJni.rtpTransceiverStop(transceiverHandle)
    }

    actual fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Long = WebrtcJni.apmCreate(echoCanceller, gainController, highPassFilter, noiseSuppression)

    actual fun apmProcessStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int = WebrtcJni.apmProcessStream(apmHandle, data, sampleRate, numChannels)

    actual fun apmProcessReverseStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int = WebrtcJni.apmProcessReverseStream(apmHandle, data, sampleRate, numChannels)

    actual fun apmSetStreamDelayMs(
        apmHandle: Long,
        delayMs: Int,
    ): Int = WebrtcJni.apmSetStreamDelayMs(apmHandle, delayMs)

    actual fun audioResample(
        src: ShortArray,
        numFrames: Int,
        numChannels: Int,
        srcRate: Int,
        dstChannels: Int,
        dstRate: Int,
        dst: ShortArray,
    ): Int =
        WebrtcJni.audioResample(
            src,
            numFrames,
            numChannels,
            srcRate,
            dstChannels,
            dstRate,
            dst,
        )

    actual fun audioMixFrames(
        src: ShortArray,
        dstAccum: ShortArray,
        totalSamples: Int,
    ) {
        WebrtcJni.audioMixFrames(src, dstAccum, totalSamples)
    }

    actual fun cryptorTransform(
        algorithm: Int,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray = WebrtcJni.cryptorTransform(algorithm, key, iv, input)

    actual fun ratchetKey(
        currentKey: ByteArray,
        salt: ByteArray,
    ): ByteArray = WebrtcJni.ratchetKey(currentKey, salt)

    actual fun desktopCapturerSourceCount(sourceType: Int): Int = WebrtcJni.desktopCapturerSourceCount(sourceType)

    actual fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Long = WebrtcJni.desktopCapturerSourceId(sourceType, index)

    actual fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String = WebrtcJni.desktopCapturerSourceTitle(sourceType, index)

    actual fun desktopCapturerCaptureRgba(
        sourceId: Long,
        width: Int,
        height: Int,
    ): ByteArray = WebrtcJni.desktopCapturerCaptureRgba(sourceId, width, height)

    actual fun yuvI420ToArgb(
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
    ) {
        WebrtcJni.yuvI420ToArgb(
            srcY,
            srcStrideY,
            srcU,
            srcStrideU,
            srcV,
            srcStrideV,
            dstArgb,
            dstStrideArgb,
            width,
            height,
        )
    }

    actual fun yuvI420ToBgra(
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
    ) {
        WebrtcJni.yuvI420ToBgra(
            srcY,
            srcStrideY,
            srcU,
            srcStrideU,
            srcV,
            srcStrideV,
            dstBgra,
            dstStrideBgra,
            width,
            height,
        )
    }

    actual fun yuvI420ToAbgr(
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
    ) {
        WebrtcJni.yuvI420ToAbgr(
            srcY,
            srcStrideY,
            srcU,
            srcStrideU,
            srcV,
            srcStrideV,
            dstAbgr,
            dstStrideAbgr,
            width,
            height,
        )
    }

    actual fun yuvI420ToRgba(
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
    ) {
        WebrtcJni.yuvI420ToRgba(
            srcY,
            srcStrideY,
            srcU,
            srcStrideU,
            srcV,
            srcStrideV,
            dstRgba,
            dstStrideRgba,
            width,
            height,
        )
    }

    actual fun yuvArgbToI420(
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
    ) {
        WebrtcJni.yuvArgbToI420(
            srcArgb,
            srcStrideArgb,
            dstY,
            dstStrideY,
            dstU,
            dstStrideU,
            dstV,
            dstStrideV,
            width,
            height,
        )
    }

    actual fun yuvAbgrToI420(
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
    ) {
        WebrtcJni.yuvAbgrToI420(
            srcAbgr,
            srcStrideAbgr,
            dstY,
            dstStrideY,
            dstU,
            dstStrideU,
            dstV,
            dstStrideV,
            width,
            height,
        )
    }

    actual fun yuvArgbToRgb24(
        srcArgb: ByteArray,
        srcStrideArgb: Int,
        dstRgb24: ByteArray,
        dstStrideRgb24: Int,
        width: Int,
        height: Int,
    ) {
        WebrtcJni.yuvArgbToRgb24(
            srcArgb,
            srcStrideArgb,
            dstRgb24,
            dstStrideRgb24,
            width,
            height,
        )
    }

    actual fun yuvI420ToNv12(
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
    ) {
        WebrtcJni.yuvI420ToNv12(
            srcY,
            srcStrideY,
            srcU,
            srcStrideU,
            srcV,
            srcStrideV,
            dstY,
            dstStrideY,
            dstUv,
            dstStrideUv,
            width,
            height,
        )
    }

    actual fun yuvNv12ToI420(
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
    ) {
        WebrtcJni.yuvNv12ToI420(
            srcY,
            srcStrideY,
            srcUv,
            srcStrideUv,
            dstY,
            dstStrideY,
            dstU,
            dstStrideU,
            dstV,
            dstStrideV,
            width,
            height,
        )
    }

    actual fun yuvNv12ToArgb(
        srcY: ByteArray,
        srcStrideY: Int,
        srcUv: ByteArray,
        srcStrideUv: Int,
        dstArgb: ByteArray,
        dstStrideArgb: Int,
        width: Int,
        height: Int,
    ) {
        WebrtcJni.yuvNv12ToArgb(
            srcY,
            srcStrideY,
            srcUv,
            srcStrideUv,
            dstArgb,
            dstStrideArgb,
            width,
            height,
        )
    }

    actual fun yuvNv12ToAbgr(
        srcY: ByteArray,
        srcStrideY: Int,
        srcUv: ByteArray,
        srcStrideUv: Int,
        dstAbgr: ByteArray,
        dstStrideAbgr: Int,
        width: Int,
        height: Int,
    ) {
        WebrtcJni.yuvNv12ToAbgr(
            srcY,
            srcStrideY,
            srcUv,
            srcStrideUv,
            dstAbgr,
            dstStrideAbgr,
            width,
            height,
        )
    }
}
