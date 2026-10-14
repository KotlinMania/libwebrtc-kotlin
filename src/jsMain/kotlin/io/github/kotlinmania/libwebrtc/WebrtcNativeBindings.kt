// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc

internal external interface WebrtcNativeAddon {
    fun initAndroid(jvmHandle: Double)

    fun isAndroidInitialized(): Boolean

    fun createPeerConnectionFactory(): Double

    fun releasePeerConnectionFactory(factoryHandle: Double)

    fun createPeerConnection(
        factoryHandle: Double,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Double

    fun closePeerConnection(pcHandle: Double)

    fun pcConnectionState(pcHandle: Double): Int

    fun pcIceConnectionState(pcHandle: Double): Int

    fun pcIceGatheringState(pcHandle: Double): Int

    fun pcSignalingState(pcHandle: Double): Int

    fun pcCreateOfferSdp(
        pcHandle: Double,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String

    fun pcCreateAnswerSdp(
        pcHandle: Double,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String

    fun pcSetLocalDescription(
        pcHandle: Double,
        sdpType: Int,
        sdp: String,
    ): Boolean

    fun pcSetRemoteDescription(
        pcHandle: Double,
        sdpType: Int,
        sdp: String,
    ): Boolean

    fun pcRemotePeerHandle(pcHandle: Double): Double

    fun pcAddIceCandidate(
        pcHandle: Double,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean

    fun pcRestartIce(pcHandle: Double)

    fun pcGetStatsJson(pcHandle: Double): String

    fun createDataChannel(
        pcHandle: Double,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Double

    fun dcState(dcHandle: Double): Int

    fun dcBufferedAmount(dcHandle: Double): Double

    fun dcSendHex(
        dcHandle: Double,
        hex: String,
        binary: Boolean,
    ): Boolean

    fun dcClose(dcHandle: Double)

    fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Double

    fun audioSourceCaptureFrameHex(
        sourceHandle: Double,
        hex: String,
        sampleRate: Int,
        numChannels: Int,
        samplesPerChannel: Int,
    ): Boolean

    fun createVideoSource(
        width: Int,
        height: Int,
        isScreencast: Boolean,
    ): Double

    fun videoSourceCaptureFrame(
        sourceHandle: Double,
        width: Int,
        height: Int,
        timestampUs: Double,
        rotation: Int,
    )

    fun createAudioTrack(
        factoryHandle: Double,
        id: String,
        sourceHandle: Double,
    ): Double

    fun createVideoTrack(
        factoryHandle: Double,
        id: String,
        sourceHandle: Double,
    ): Double

    fun trackEnabled(trackHandle: Double): Boolean

    fun trackSetEnabled(
        trackHandle: Double,
        enabled: Boolean,
    ): Boolean

    fun trackState(trackHandle: Double): Int

    fun trackSetState(
        trackHandle: Double,
        state: Int,
    )

    fun createRtpSender(
        pcHandle: Double,
        trackHandle: Double,
    ): Double

    fun rtpSenderGetStatsJson(senderHandle: Double): String

    fun createRtpReceiver(
        pcHandle: Double,
        trackHandle: Double,
    ): Double

    fun rtpReceiverGetStatsJson(receiverHandle: Double): String

    fun createRtpTransceiver(
        pcHandle: Double,
        mediaType: Int,
        direction: Int,
    ): Double

    fun rtpTransceiverDirection(transceiverHandle: Double): Int

    fun rtpTransceiverSetDirection(
        transceiverHandle: Double,
        direction: Int,
    )

    fun rtpTransceiverStop(transceiverHandle: Double)

    fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Double

    fun apmProcessStreamHex(
        apmHandle: Double,
        hex: String,
        sampleRate: Int,
        numChannels: Int,
    ): String

    fun apmProcessReverseStreamHex(
        apmHandle: Double,
        hex: String,
        sampleRate: Int,
        numChannels: Int,
    ): String

    fun apmSetStreamDelayMs(
        apmHandle: Double,
        delayMs: Int,
    ): Int

    fun audioResampleHex(
        srcHex: String,
        numFrames: Int,
        numChannels: Int,
        srcRate: Int,
        dstChannels: Int,
        dstRate: Int,
        dstCap: Int,
    ): String

    fun audioMixFramesHex(
        srcHex: String,
        dstHex: String,
        totalSamples: Int,
    ): String

    fun cryptorTransformHex(
        algorithm: Int,
        keyHex: String,
        ivHex: String,
        inputHex: String,
    ): String

    fun ratchetKeyHex(
        currentKeyHex: String,
        saltHex: String,
    ): String

    fun desktopCapturerSourceCount(sourceType: Int): Int

    fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Double

    fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String

    fun desktopCapturerCaptureRgbaHex(
        sourceId: Double,
        width: Int,
        height: Int,
    ): String

    fun yuvI420ToArgbHex(
        srcYHex: String,
        srcStrideY: Int,
        srcUHex: String,
        srcStrideU: Int,
        srcVHex: String,
        srcStrideV: Int,
        dstLen: Int,
        dstStrideArgb: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvI420ToBgraHex(
        srcYHex: String,
        srcStrideY: Int,
        srcUHex: String,
        srcStrideU: Int,
        srcVHex: String,
        srcStrideV: Int,
        dstLen: Int,
        dstStrideBgra: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvI420ToAbgrHex(
        srcYHex: String,
        srcStrideY: Int,
        srcUHex: String,
        srcStrideU: Int,
        srcVHex: String,
        srcStrideV: Int,
        dstLen: Int,
        dstStrideAbgr: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvI420ToRgbaHex(
        srcYHex: String,
        srcStrideY: Int,
        srcUHex: String,
        srcStrideU: Int,
        srcVHex: String,
        srcStrideV: Int,
        dstLen: Int,
        dstStrideRgba: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvArgbToI420Hex(
        srcArgbHex: String,
        srcStrideArgb: Int,
        yLen: Int,
        dstStrideY: Int,
        uLen: Int,
        dstStrideU: Int,
        vLen: Int,
        dstStrideV: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvAbgrToI420Hex(
        srcAbgrHex: String,
        srcStrideAbgr: Int,
        yLen: Int,
        dstStrideY: Int,
        uLen: Int,
        dstStrideU: Int,
        vLen: Int,
        dstStrideV: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvArgbToRgb24Hex(
        srcArgbHex: String,
        srcStrideArgb: Int,
        dstLen: Int,
        dstStrideRgb24: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvI420ToNv12Hex(
        srcYHex: String,
        srcStrideY: Int,
        srcUHex: String,
        srcStrideU: Int,
        srcVHex: String,
        srcStrideV: Int,
        yLen: Int,
        dstStrideY: Int,
        uvLen: Int,
        dstStrideUv: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvNv12ToI420Hex(
        srcYHex: String,
        srcStrideY: Int,
        srcUvHex: String,
        srcStrideUv: Int,
        yLen: Int,
        dstStrideY: Int,
        uLen: Int,
        dstStrideU: Int,
        vLen: Int,
        dstStrideV: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvNv12ToArgbHex(
        srcYHex: String,
        srcStrideY: Int,
        srcUvHex: String,
        srcStrideUv: Int,
        dstLen: Int,
        dstStrideArgb: Int,
        width: Int,
        height: Int,
    ): String

    fun yuvNv12ToAbgrHex(
        srcYHex: String,
        srcStrideY: Int,
        srcUvHex: String,
        srcStrideUv: Int,
        dstLen: Int,
        dstStrideAbgr: Int,
        width: Int,
        height: Int,
    ): String
}

internal val WebrtcNativeBindings: WebrtcNativeAddon =
    checkNotNull(
        js(
            """(() => {
                const proc = globalThis.process;
                if (proc && typeof proc.getBuiltinModule === "function") {
                    const modMod = proc.getBuiltinModule("module");
                    const path = proc.getBuiltinModule("path");
                    const fs = proc.getBuiltinModule("fs");
                    const req = modMod.createRequire(path.join(proc.cwd(), "package.json"));
                    const copiedMod = path.resolve(proc.cwd(), "../../node_modules/@kotlinmania/webrtc-native-bindings");
                    const sourceMod = path.resolve(proc.cwd(), "../../../../native/node-webrtc");
                    return req(fs.existsSync(copiedMod) ? copiedMod : sourceMod);
                }
                return null;
            })()""",
        ).unsafeCast<WebrtcNativeAddon?>(),
    ) { "Failed to load @kotlinmania/webrtc-native-bindings" }
