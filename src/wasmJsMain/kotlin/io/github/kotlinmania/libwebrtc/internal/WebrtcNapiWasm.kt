// port-lint: source native/mod.rs
@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.libwebrtc.internal

@JsFun(
    """() => {
        const proc = globalThis.process;
        if (proc && typeof proc.getBuiltinModule === "function") {
            const modMod = proc.getBuiltinModule("module");
            const path = proc.getBuiltinModule("path");
            const fs = proc.getBuiltinModule("fs");
            const req = modMod.createRequire(path.join(proc.cwd(), "package.json"));
            const copiedMod = path.resolve(proc.cwd(), "../../../js/node_modules/@kotlinmania/webrtc-native-bindings");
            const sourceMod = path.resolve(proc.cwd(), "../../../../native/node-webrtc");
            return req(fs.existsSync(copiedMod) ? copiedMod : sourceMod);
        }
        return null;
    }""",
)
internal external fun loadWebrtcAddon(): JsAny?

@JsFun("(addon, jvmHandle) => addon.initAndroid(jvmHandle)")
internal external fun napiInitAndroid(
    addon: JsAny,
    jvmHandle: Double,
)

@JsFun("(addon) => addon.isAndroidInitialized()")
internal external fun napiIsAndroidInitialized(addon: JsAny): Boolean

@JsFun("(addon) => addon.createPeerConnectionFactory()")
internal external fun napiCreatePeerConnectionFactory(addon: JsAny): Double

@JsFun("(addon, factoryHandle) => addon.releasePeerConnectionFactory(factoryHandle)")
internal external fun napiReleasePeerConnectionFactory(
    addon: JsAny,
    factoryHandle: Double,
)

@JsFun("(addon, factoryHandle, iceType, policy, servers) => addon.createPeerConnection(factoryHandle, iceType, policy, servers)")
internal external fun napiCreatePeerConnection(
    addon: JsAny,
    factoryHandle: Double,
    iceType: Int,
    policy: Int,
    servers: Int,
): Double

@JsFun("(addon, pcHandle) => addon.closePeerConnection(pcHandle)")
internal external fun napiClosePeerConnection(
    addon: JsAny,
    pcHandle: Double,
)

@JsFun("(addon, pcHandle) => addon.pcConnectionState(pcHandle)")
internal external fun napiPcConnectionState(
    addon: JsAny,
    pcHandle: Double,
): Int

@JsFun("(addon, pcHandle) => addon.pcIceConnectionState(pcHandle)")
internal external fun napiPcIceConnectionState(
    addon: JsAny,
    pcHandle: Double,
): Int

@JsFun("(addon, pcHandle) => addon.pcIceGatheringState(pcHandle)")
internal external fun napiPcIceGatheringState(
    addon: JsAny,
    pcHandle: Double,
): Int

@JsFun("(addon, pcHandle) => addon.pcSignalingState(pcHandle)")
internal external fun napiPcSignalingState(
    addon: JsAny,
    pcHandle: Double,
): Int

@JsFun("(addon, pcHandle, iceRestart, offerAudio, offerVideo) => addon.pcCreateOfferSdp(pcHandle, iceRestart, offerAudio, offerVideo)")
internal external fun napiPcCreateOfferSdp(
    addon: JsAny,
    pcHandle: Double,
    iceRestart: Boolean,
    offerAudio: Boolean,
    offerVideo: Boolean,
): String

@JsFun("(addon, pcHandle, iceRestart, offerAudio, offerVideo) => addon.pcCreateAnswerSdp(pcHandle, iceRestart, offerAudio, offerVideo)")
internal external fun napiPcCreateAnswerSdp(
    addon: JsAny,
    pcHandle: Double,
    iceRestart: Boolean,
    offerAudio: Boolean,
    offerVideo: Boolean,
): String

@JsFun("(addon, pcHandle, sdpType, sdp) => addon.pcSetLocalDescription(pcHandle, sdpType, sdp)")
internal external fun napiPcSetLocalDescription(
    addon: JsAny,
    pcHandle: Double,
    sdpType: Int,
    sdp: String,
): Boolean

@JsFun("(addon, pcHandle, sdpType, sdp) => addon.pcSetRemoteDescription(pcHandle, sdpType, sdp)")
internal external fun napiPcSetRemoteDescription(
    addon: JsAny,
    pcHandle: Double,
    sdpType: Int,
    sdp: String,
): Boolean

@JsFun("(addon, pcHandle) => addon.pcRemotePeerHandle(pcHandle)")
internal external fun napiPcRemotePeerHandle(
    addon: JsAny,
    pcHandle: Double,
): Double

@JsFun("(addon, pcHandle, sdpMid, sdpMlineIndex, candidate) => addon.pcAddIceCandidate(pcHandle, sdpMid, sdpMlineIndex, candidate)")
internal external fun napiPcAddIceCandidate(
    addon: JsAny,
    pcHandle: Double,
    sdpMid: String,
    sdpMlineIndex: Int,
    candidate: String,
): Boolean

@JsFun("(addon, pcHandle) => addon.pcRestartIce(pcHandle)")
internal external fun napiPcRestartIce(
    addon: JsAny,
    pcHandle: Double,
)

@JsFun("(addon, pcHandle) => addon.pcGetStatsJson(pcHandle)")
internal external fun napiPcGetStatsJson(
    addon: JsAny,
    pcHandle: Double,
): String

@JsFun("(addon, pcHandle, label, id, ordered, negotiated) => addon.createDataChannel(pcHandle, label, id, ordered, negotiated)")
internal external fun napiCreateDataChannel(
    addon: JsAny,
    pcHandle: Double,
    label: String,
    id: Int,
    ordered: Boolean,
    negotiated: Boolean,
): Double

@JsFun("(addon, dcHandle) => addon.dcState(dcHandle)")
internal external fun napiDcState(
    addon: JsAny,
    dcHandle: Double,
): Int

@JsFun("(addon, dcHandle) => addon.dcBufferedAmount(dcHandle)")
internal external fun napiDcBufferedAmount(
    addon: JsAny,
    dcHandle: Double,
): Double

@JsFun("(addon, dcHandle, hex, binary) => addon.dcSendHex(dcHandle, hex, binary)")
internal external fun napiDcSendHex(
    addon: JsAny,
    dcHandle: Double,
    hex: String,
    binary: Boolean,
): Boolean

@JsFun("(addon, dcHandle) => addon.dcClose(dcHandle)")
internal external fun napiDcClose(
    addon: JsAny,
    dcHandle: Double,
)

@JsFun("(addon, sampleRate, numChannels, ec, ns, agc) => addon.createAudioSource(sampleRate, numChannels, ec, ns, agc)")
internal external fun napiCreateAudioSource(
    addon: JsAny,
    sampleRate: Int,
    numChannels: Int,
    ec: Boolean,
    ns: Boolean,
    agc: Boolean,
): Double

@JsFun("(addon, sourceHandle, hex, sampleRate, numChannels, spc) => addon.audioSourceCaptureFrameHex(sourceHandle, hex, sampleRate, numChannels, spc)")
internal external fun napiAudioSourceCaptureFrameHex(
    addon: JsAny,
    sourceHandle: Double,
    hex: String,
    sampleRate: Int,
    numChannels: Int,
    spc: Int,
): Boolean

@JsFun("(addon, width, height, isScreencast) => addon.createVideoSource(width, height, isScreencast)")
internal external fun napiCreateVideoSource(
    addon: JsAny,
    width: Int,
    height: Int,
    isScreencast: Boolean,
): Double

@JsFun("(addon, sourceHandle, width, height, timestampUs, rotation) => addon.videoSourceCaptureFrame(sourceHandle, width, height, timestampUs, rotation)")
internal external fun napiVideoSourceCaptureFrame(
    addon: JsAny,
    sourceHandle: Double,
    width: Int,
    height: Int,
    timestampUs: Double,
    rotation: Int,
)

@JsFun("(addon, factoryHandle, id, sourceHandle) => addon.createAudioTrack(factoryHandle, id, sourceHandle)")
internal external fun napiCreateAudioTrack(
    addon: JsAny,
    factoryHandle: Double,
    id: String,
    sourceHandle: Double,
): Double

@JsFun("(addon, factoryHandle, id, sourceHandle) => addon.createVideoTrack(factoryHandle, id, sourceHandle)")
internal external fun napiCreateVideoTrack(
    addon: JsAny,
    factoryHandle: Double,
    id: String,
    sourceHandle: Double,
): Double

@JsFun("(addon, trackHandle) => addon.trackEnabled(trackHandle)")
internal external fun napiTrackEnabled(
    addon: JsAny,
    trackHandle: Double,
): Boolean

@JsFun("(addon, trackHandle, enabled) => addon.trackSetEnabled(trackHandle, enabled)")
internal external fun napiTrackSetEnabled(
    addon: JsAny,
    trackHandle: Double,
    enabled: Boolean,
): Boolean

@JsFun("(addon, trackHandle) => addon.trackState(trackHandle)")
internal external fun napiTrackState(
    addon: JsAny,
    trackHandle: Double,
): Int

@JsFun("(addon, trackHandle, state) => addon.trackSetState(trackHandle, state)")
internal external fun napiTrackSetState(
    addon: JsAny,
    trackHandle: Double,
    state: Int,
)

@JsFun("(addon, pcHandle, trackHandle) => addon.createRtpSender(pcHandle, trackHandle)")
internal external fun napiCreateRtpSender(
    addon: JsAny,
    pcHandle: Double,
    trackHandle: Double,
): Double

@JsFun("(addon, senderHandle) => addon.rtpSenderGetStatsJson(senderHandle)")
internal external fun napiRtpSenderGetStatsJson(
    addon: JsAny,
    senderHandle: Double,
): String

@JsFun("(addon, pcHandle, trackHandle) => addon.createRtpReceiver(pcHandle, trackHandle)")
internal external fun napiCreateRtpReceiver(
    addon: JsAny,
    pcHandle: Double,
    trackHandle: Double,
): Double

@JsFun("(addon, receiverHandle) => addon.rtpReceiverGetStatsJson(receiverHandle)")
internal external fun napiRtpReceiverGetStatsJson(
    addon: JsAny,
    receiverHandle: Double,
): String

@JsFun("(addon, pcHandle, mediaType, direction) => addon.createRtpTransceiver(pcHandle, mediaType, direction)")
internal external fun napiCreateRtpTransceiver(
    addon: JsAny,
    pcHandle: Double,
    mediaType: Int,
    direction: Int,
): Double

@JsFun("(addon, transceiverHandle) => addon.rtpTransceiverDirection(transceiverHandle)")
internal external fun napiRtpTransceiverDirection(
    addon: JsAny,
    transceiverHandle: Double,
): Int

@JsFun("(addon, transceiverHandle, direction) => addon.rtpTransceiverSetDirection(transceiverHandle, direction)")
internal external fun napiRtpTransceiverSetDirection(
    addon: JsAny,
    transceiverHandle: Double,
    direction: Int,
)

@JsFun("(addon, transceiverHandle) => addon.rtpTransceiverStop(transceiverHandle)")
internal external fun napiRtpTransceiverStop(
    addon: JsAny,
    transceiverHandle: Double,
)

@JsFun("(addon, ec, gc, hpf, ns) => addon.apmCreate(ec, gc, hpf, ns)")
internal external fun napiApmCreate(
    addon: JsAny,
    ec: Boolean,
    gc: Boolean,
    hpf: Boolean,
    ns: Boolean,
): Double

@JsFun("(addon, apmHandle, hex, sampleRate, numChannels) => addon.apmProcessStreamHex(apmHandle, hex, sampleRate, numChannels)")
internal external fun napiApmProcessStreamHex(
    addon: JsAny,
    apmHandle: Double,
    hex: String,
    sampleRate: Int,
    numChannels: Int,
): String

@JsFun("(addon, apmHandle, hex, sampleRate, numChannels) => addon.apmProcessReverseStreamHex(apmHandle, hex, sampleRate, numChannels)")
internal external fun napiApmProcessReverseStreamHex(
    addon: JsAny,
    apmHandle: Double,
    hex: String,
    sampleRate: Int,
    numChannels: Int,
): String

@JsFun("(addon, apmHandle, delayMs) => addon.apmSetStreamDelayMs(apmHandle, delayMs)")
internal external fun napiApmSetStreamDelayMs(
    addon: JsAny,
    apmHandle: Double,
    delayMs: Int,
): Int

@JsFun("(addon, srcHex, numFrames, numChannels, srcRate, dstChannels, dstRate, dstCap) => addon.audioResampleHex(srcHex, numFrames, numChannels, srcRate, dstChannels, dstRate, dstCap)")
internal external fun napiAudioResampleHex(
    addon: JsAny,
    srcHex: String,
    numFrames: Int,
    numChannels: Int,
    srcRate: Int,
    dstChannels: Int,
    dstRate: Int,
    dstCap: Int,
): String

@JsFun("(addon, srcHex, dstHex, totalSamples) => addon.audioMixFramesHex(srcHex, dstHex, totalSamples)")
internal external fun napiAudioMixFramesHex(
    addon: JsAny,
    srcHex: String,
    dstHex: String,
    totalSamples: Int,
): String

@JsFun("(addon, algorithm, keyHex, ivHex, inputHex) => addon.cryptorTransformHex(algorithm, keyHex, ivHex, inputHex)")
internal external fun napiCryptorTransformHex(
    addon: JsAny,
    algorithm: Int,
    keyHex: String,
    ivHex: String,
    inputHex: String,
): String

@JsFun("(addon, currentKeyHex, saltHex) => addon.ratchetKeyHex(currentKeyHex, saltHex)")
internal external fun napiRatchetKeyHex(
    addon: JsAny,
    currentKeyHex: String,
    saltHex: String,
): String

@JsFun("(addon, sourceType) => addon.desktopCapturerSourceCount(sourceType)")
internal external fun napiDesktopCapturerSourceCount(
    addon: JsAny,
    sourceType: Int,
): Int

@JsFun("(addon, sourceType, index) => addon.desktopCapturerSourceId(sourceType, index)")
internal external fun napiDesktopCapturerSourceId(
    addon: JsAny,
    sourceType: Int,
    index: Int,
): Double

@JsFun("(addon, sourceType, index) => addon.desktopCapturerSourceTitle(sourceType, index)")
internal external fun napiDesktopCapturerSourceTitle(
    addon: JsAny,
    sourceType: Int,
    index: Int,
): String

@JsFun("(addon, sourceId, width, height) => addon.desktopCapturerCaptureRgbaHex(sourceId, width, height)")
internal external fun napiDesktopCapturerCaptureRgbaHex(
    addon: JsAny,
    sourceId: Double,
    width: Int,
    height: Int,
): String

@JsFun("(addon, sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h) => addon.yuvI420ToArgbHex(sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h)")
internal external fun napiYuvI420ToArgbHex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    su: String,
    ssu: Int,
    sv: String,
    ssv: Int,
    dstLen: Int,
    dstStride: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h) => addon.yuvI420ToBgraHex(sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h)")
internal external fun napiYuvI420ToBgraHex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    su: String,
    ssu: Int,
    sv: String,
    ssv: Int,
    dstLen: Int,
    dstStride: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h) => addon.yuvI420ToAbgrHex(sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h)")
internal external fun napiYuvI420ToAbgrHex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    su: String,
    ssu: Int,
    sv: String,
    ssv: Int,
    dstLen: Int,
    dstStride: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h) => addon.yuvI420ToRgbaHex(sy, ssy, su, ssu, sv, ssv, dstLen, dstStride, w, h)")
internal external fun napiYuvI420ToRgbaHex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    su: String,
    ssu: Int,
    sv: String,
    ssv: Int,
    dstLen: Int,
    dstStride: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, src, ss, yLen, dsy, uLen, dsu, vLen, dsv, w, h) => addon.yuvArgbToI420Hex(src, ss, yLen, dsy, uLen, dsu, vLen, dsv, w, h)")
internal external fun napiYuvArgbToI420Hex(
    addon: JsAny,
    src: String,
    ss: Int,
    yLen: Int,
    dsy: Int,
    uLen: Int,
    dsu: Int,
    vLen: Int,
    dsv: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, src, ss, yLen, dsy, uLen, dsu, vLen, dsv, w, h) => addon.yuvAbgrToI420Hex(src, ss, yLen, dsy, uLen, dsu, vLen, dsv, w, h)")
internal external fun napiYuvAbgrToI420Hex(
    addon: JsAny,
    src: String,
    ss: Int,
    yLen: Int,
    dsy: Int,
    uLen: Int,
    dsu: Int,
    vLen: Int,
    dsv: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, src, ss, dstLen, dstStride, w, h) => addon.yuvArgbToRgb24Hex(src, ss, dstLen, dstStride, w, h)")
internal external fun napiYuvArgbToRgb24Hex(
    addon: JsAny,
    src: String,
    ss: Int,
    dstLen: Int,
    dstStride: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, sy, ssy, su, ssu, sv, ssv, yLen, dsy, uvLen, dsuv, w, h) => addon.yuvI420ToNv12Hex(sy, ssy, su, ssu, sv, ssv, yLen, dsy, uvLen, dsuv, w, h)")
internal external fun napiYuvI420ToNv12Hex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    su: String,
    ssu: Int,
    sv: String,
    ssv: Int,
    yLen: Int,
    dsy: Int,
    uvLen: Int,
    dsuv: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, sy, ssy, suv, ssuv, yLen, dsy, uLen, dsu, vLen, dsv, w, h) => addon.yuvNv12ToI420Hex(sy, ssy, suv, ssuv, yLen, dsy, uLen, dsu, vLen, dsv, w, h)")
internal external fun napiYuvNv12ToI420Hex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    suv: String,
    ssuv: Int,
    yLen: Int,
    dsy: Int,
    uLen: Int,
    dsu: Int,
    vLen: Int,
    dsv: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, sy, ssy, suv, ssuv, dstLen, dstStride, w, h) => addon.yuvNv12ToArgbHex(sy, ssy, suv, ssuv, dstLen, dstStride, w, h)")
internal external fun napiYuvNv12ToArgbHex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    suv: String,
    ssuv: Int,
    dstLen: Int,
    dstStride: Int,
    w: Int,
    h: Int,
): String

@JsFun("(addon, sy, ssy, suv, ssuv, dstLen, dstStride, w, h) => addon.yuvNv12ToAbgrHex(sy, ssy, suv, ssuv, dstLen, dstStride, w, h)")
internal external fun napiYuvNv12ToAbgrHex(
    addon: JsAny,
    sy: String,
    ssy: Int,
    suv: String,
    ssuv: Int,
    dstLen: Int,
    dstStride: Int,
    w: Int,
    h: Int,
): String
