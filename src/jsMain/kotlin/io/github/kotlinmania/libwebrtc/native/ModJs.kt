// port-lint: source native/mod.rs
package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.WebrtcNativeBindings

private const val HEX_DIGITS = "0123456789abcdef"

private fun hexNibble(c: Char): Int =
    when (c) {
        in '0'..'9' -> c - '0'
        in 'a'..'f' -> c - 'a' + 10
        in 'A'..'F' -> c - 'A' + 10
        else -> 0
    }

private fun ByteArray.toHex(): String {
    val chars = CharArray(size * 2)
    for (i in indices) {
        val v = this[i].toInt() and 0xFF
        chars[i * 2] = HEX_DIGITS[v ushr 4]
        chars[i * 2 + 1] = HEX_DIGITS[v and 0x0F]
    }
    return chars.concatToString()
}

private fun String.fromHexBytes(dst: ByteArray? = null): ByteArray {
    val count = length / 2
    val out = dst ?: ByteArray(count)
    val limit = minOf(count, out.size)
    for (i in 0 until limit) {
        val hi = hexNibble(this[i * 2])
        val lo = hexNibble(this[i * 2 + 1])
        out[i] = ((hi shl 4) or lo).toByte()
    }
    return out
}

private fun ShortArray.toHex16(): String {
    val chars = CharArray(size * 4)
    for (i in indices) {
        val v = this[i].toInt() and 0xFFFF
        chars[i * 4] = HEX_DIGITS[(v ushr 12) and 0x0F]
        chars[i * 4 + 1] = HEX_DIGITS[(v ushr 8) and 0x0F]
        chars[i * 4 + 2] = HEX_DIGITS[(v ushr 4) and 0x0F]
        chars[i * 4 + 3] = HEX_DIGITS[v and 0x0F]
    }
    return chars.concatToString()
}

private fun String.fromHex16Shorts(dst: ShortArray) {
    val count = minOf(length / 4, dst.size)
    for (i in 0 until count) {
        val n0 = hexNibble(this[i * 4])
        val n1 = hexNibble(this[i * 4 + 1])
        val n2 = hexNibble(this[i * 4 + 2])
        val n3 = hexNibble(this[i * 4 + 3])
        dst[i] = ((n0 shl 12) or (n1 shl 8) or (n2 shl 4) or n3).toShort()
    }
}

internal actual val isWebPlatform: Boolean = true

internal actual val isNativePlatform: Boolean = false

internal actual val isAndroidPlatform: Boolean = false

internal actual fun platformInitializeAndroid(vm: Any?) {
    val handle = (vm as? Number)?.toLong() ?: 1L
    WebrtcNativeBridge.initAndroid(handle)
}

internal actual object WebrtcNativeBridge {
    actual fun initAndroid(jvmHandle: Long) {
        WebrtcNativeBindings.initAndroid(jvmHandle.toDouble())
    }

    actual fun isAndroidInitialized(): Boolean = WebrtcNativeBindings.isAndroidInitialized()

    actual fun createPeerConnectionFactory(): Long = WebrtcNativeBindings.createPeerConnectionFactory().toLong()

    actual fun releasePeerConnectionFactory(factoryHandle: Long) {
        WebrtcNativeBindings.releasePeerConnectionFactory(factoryHandle.toDouble())
    }

    actual fun createPeerConnection(
        factoryHandle: Long,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Long =
        WebrtcNativeBindings
            .createPeerConnection(
                factoryHandle.toDouble(),
                iceTransportType,
                continualGatheringPolicy,
                iceServersCount,
            ).toLong()

    actual fun closePeerConnection(pcHandle: Long) {
        WebrtcNativeBindings.closePeerConnection(pcHandle.toDouble())
    }

    actual fun pcConnectionState(pcHandle: Long): Int = WebrtcNativeBindings.pcConnectionState(pcHandle.toDouble())

    actual fun pcIceConnectionState(pcHandle: Long): Int =
        WebrtcNativeBindings.pcIceConnectionState(pcHandle.toDouble())

    actual fun pcIceGatheringState(pcHandle: Long): Int = WebrtcNativeBindings.pcIceGatheringState(pcHandle.toDouble())

    actual fun pcSignalingState(pcHandle: Long): Int = WebrtcNativeBindings.pcSignalingState(pcHandle.toDouble())

    actual fun pcCreateOfferSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String =
        WebrtcNativeBindings.pcCreateOfferSdp(
            pcHandle.toDouble(),
            iceRestart,
            offerAudio,
            offerVideo,
        )

    actual fun pcCreateAnswerSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String =
        WebrtcNativeBindings.pcCreateAnswerSdp(
            pcHandle.toDouble(),
            iceRestart,
            offerAudio,
            offerVideo,
        )

    actual fun pcSetLocalDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = WebrtcNativeBindings.pcSetLocalDescription(pcHandle.toDouble(), sdpType, sdp)

    actual fun pcSetRemoteDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = WebrtcNativeBindings.pcSetRemoteDescription(pcHandle.toDouble(), sdpType, sdp)

    actual fun pcRemotePeerHandle(pcHandle: Long): Long =
        WebrtcNativeBindings.pcRemotePeerHandle(pcHandle.toDouble()).toLong()

    actual fun pcAddIceCandidate(
        pcHandle: Long,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean = WebrtcNativeBindings.pcAddIceCandidate(pcHandle.toDouble(), sdpMid, sdpMlineIndex, candidate)

    actual fun pcRestartIce(pcHandle: Long) {
        WebrtcNativeBindings.pcRestartIce(pcHandle.toDouble())
    }

    actual fun pcGetStatsJson(pcHandle: Long): String = WebrtcNativeBindings.pcGetStatsJson(pcHandle.toDouble())

    actual fun createDataChannel(
        pcHandle: Long,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Long =
        WebrtcNativeBindings
            .createDataChannel(pcHandle.toDouble(), label, id, ordered, negotiated)
            .toLong()

    actual fun dcState(dcHandle: Long): Int = WebrtcNativeBindings.dcState(dcHandle.toDouble())

    actual fun dcBufferedAmount(dcHandle: Long): Long =
        WebrtcNativeBindings.dcBufferedAmount(dcHandle.toDouble()).toLong()

    actual fun dcSend(
        dcHandle: Long,
        data: ByteArray,
        binary: Boolean,
    ): Boolean = WebrtcNativeBindings.dcSendHex(dcHandle.toDouble(), data.toHex(), binary)

    actual fun dcClose(dcHandle: Long) {
        WebrtcNativeBindings.dcClose(dcHandle.toDouble())
    }

    actual fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Long =
        WebrtcNativeBindings
            .createAudioSource(
                sampleRate,
                numChannels,
                echoCancellation,
                noiseSuppression,
                autoGainControl,
            ).toLong()

    actual fun audioSourceCaptureFrame(
        sourceHandle: Long,
        samples: ShortArray,
        sampleRate: Int,
        numChannels: Int,
        samplesPerChannel: Int,
    ): Boolean =
        WebrtcNativeBindings.audioSourceCaptureFrameHex(
            sourceHandle.toDouble(),
            samples.toHex16(),
            sampleRate,
            numChannels,
            samplesPerChannel,
        )

    actual fun createVideoSource(
        width: Int,
        height: Int,
        isScreencast: Boolean,
    ): Long = WebrtcNativeBindings.createVideoSource(width, height, isScreencast).toLong()

    actual fun videoSourceCaptureFrame(
        sourceHandle: Long,
        width: Int,
        height: Int,
        timestampUs: Long,
        rotation: Int,
    ) {
        WebrtcNativeBindings.videoSourceCaptureFrame(
            sourceHandle.toDouble(),
            width,
            height,
            timestampUs.toDouble(),
            rotation,
        )
    }

    actual fun createAudioTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long = WebrtcNativeBindings.createAudioTrack(factoryHandle.toDouble(), id, sourceHandle.toDouble()).toLong()

    actual fun createVideoTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long = WebrtcNativeBindings.createVideoTrack(factoryHandle.toDouble(), id, sourceHandle.toDouble()).toLong()

    actual fun trackEnabled(trackHandle: Long): Boolean = WebrtcNativeBindings.trackEnabled(trackHandle.toDouble())

    actual fun trackSetEnabled(
        trackHandle: Long,
        enabled: Boolean,
    ): Boolean = WebrtcNativeBindings.trackSetEnabled(trackHandle.toDouble(), enabled)

    actual fun trackState(trackHandle: Long): Int = WebrtcNativeBindings.trackState(trackHandle.toDouble())

    actual fun trackSetState(
        trackHandle: Long,
        state: Int,
    ) {
        WebrtcNativeBindings.trackSetState(trackHandle.toDouble(), state)
    }

    actual fun createRtpSender(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = WebrtcNativeBindings.createRtpSender(pcHandle.toDouble(), trackHandle.toDouble()).toLong()

    actual fun rtpSenderGetStatsJson(senderHandle: Long): String =
        WebrtcNativeBindings.rtpSenderGetStatsJson(senderHandle.toDouble())

    actual fun createRtpReceiver(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = WebrtcNativeBindings.createRtpReceiver(pcHandle.toDouble(), trackHandle.toDouble()).toLong()

    actual fun rtpReceiverGetStatsJson(receiverHandle: Long): String =
        WebrtcNativeBindings.rtpReceiverGetStatsJson(receiverHandle.toDouble())

    actual fun createRtpTransceiver(
        pcHandle: Long,
        mediaType: Int,
        direction: Int,
    ): Long = WebrtcNativeBindings.createRtpTransceiver(pcHandle.toDouble(), mediaType, direction).toLong()

    actual fun rtpTransceiverDirection(transceiverHandle: Long): Int =
        WebrtcNativeBindings.rtpTransceiverDirection(transceiverHandle.toDouble())

    actual fun rtpTransceiverSetDirection(
        transceiverHandle: Long,
        direction: Int,
    ) {
        WebrtcNativeBindings.rtpTransceiverSetDirection(transceiverHandle.toDouble(), direction)
    }

    actual fun rtpTransceiverStop(transceiverHandle: Long) {
        WebrtcNativeBindings.rtpTransceiverStop(transceiverHandle.toDouble())
    }

    actual fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Long = WebrtcNativeBindings.apmCreate(echoCanceller, gainController, highPassFilter, noiseSuppression).toLong()

    actual fun apmProcessStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int {
        val raw = WebrtcNativeBindings.apmProcessStreamHex(apmHandle.toDouble(), data.toHex16(), sampleRate, numChannels)
        val sep = raw.indexOf(':')
        if (sep < 0) return -1
        val code = raw.substring(0, sep).toIntOrNull() ?: -1
        raw.substring(sep + 1).fromHex16Shorts(data)
        return code
    }

    actual fun apmProcessReverseStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int {
        val raw =
            WebrtcNativeBindings.apmProcessReverseStreamHex(
                apmHandle.toDouble(),
                data.toHex16(),
                sampleRate,
                numChannels,
            )
        val sep = raw.indexOf(':')
        if (sep < 0) return -1
        val code = raw.substring(0, sep).toIntOrNull() ?: -1
        raw.substring(sep + 1).fromHex16Shorts(data)
        return code
    }

    actual fun apmSetStreamDelayMs(
        apmHandle: Long,
        delayMs: Int,
    ): Int = WebrtcNativeBindings.apmSetStreamDelayMs(apmHandle.toDouble(), delayMs)

    actual fun audioResample(
        src: ShortArray,
        numFrames: Int,
        numChannels: Int,
        srcRate: Int,
        dstChannels: Int,
        dstRate: Int,
        dst: ShortArray,
    ): Int {
        val raw =
            WebrtcNativeBindings.audioResampleHex(
                src.toHex16(),
                numFrames,
                numChannels,
                srcRate,
                dstChannels,
                dstRate,
                dst.size,
            )
        val sep = raw.indexOf(':')
        if (sep < 0) return 0
        val produced = raw.substring(0, sep).toIntOrNull() ?: 0
        raw.substring(sep + 1).fromHex16Shorts(dst)
        return produced
    }

    actual fun audioMixFrames(
        src: ShortArray,
        dstAccum: ShortArray,
        totalSamples: Int,
    ) {
        val outHex = WebrtcNativeBindings.audioMixFramesHex(src.toHex16(), dstAccum.toHex16(), totalSamples)
        outHex.fromHex16Shorts(dstAccum)
    }

    actual fun cryptorTransform(
        algorithm: Int,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray {
        val hex = WebrtcNativeBindings.cryptorTransformHex(algorithm, key.toHex(), iv.toHex(), input.toHex())
        return hex.fromHexBytes()
    }

    actual fun ratchetKey(
        currentKey: ByteArray,
        salt: ByteArray,
    ): ByteArray {
        val hex = WebrtcNativeBindings.ratchetKeyHex(currentKey.toHex(), salt.toHex())
        return hex.fromHexBytes()
    }

    actual fun desktopCapturerSourceCount(sourceType: Int): Int =
        WebrtcNativeBindings.desktopCapturerSourceCount(sourceType)

    actual fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Long = WebrtcNativeBindings.desktopCapturerSourceId(sourceType, index).toLong()

    actual fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String = WebrtcNativeBindings.desktopCapturerSourceTitle(sourceType, index)

    actual fun desktopCapturerCaptureRgba(
        sourceId: Long,
        width: Int,
        height: Int,
    ): ByteArray {
        val hex = WebrtcNativeBindings.desktopCapturerCaptureRgbaHex(sourceId.toDouble(), width, height)
        return hex.fromHexBytes()
    }

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
        val hex =
            WebrtcNativeBindings.yuvI420ToArgbHex(
                srcY.toHex(),
                srcStrideY,
                srcU.toHex(),
                srcStrideU,
                srcV.toHex(),
                srcStrideV,
                dstArgb.size,
                dstStrideArgb,
                width,
                height,
            )
        hex.fromHexBytes(dstArgb)
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
        val hex =
            WebrtcNativeBindings.yuvI420ToBgraHex(
                srcY.toHex(),
                srcStrideY,
                srcU.toHex(),
                srcStrideU,
                srcV.toHex(),
                srcStrideV,
                dstBgra.size,
                dstStrideBgra,
                width,
                height,
            )
        hex.fromHexBytes(dstBgra)
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
        val hex =
            WebrtcNativeBindings.yuvI420ToAbgrHex(
                srcY.toHex(),
                srcStrideY,
                srcU.toHex(),
                srcStrideU,
                srcV.toHex(),
                srcStrideV,
                dstAbgr.size,
                dstStrideAbgr,
                width,
                height,
            )
        hex.fromHexBytes(dstAbgr)
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
        val hex =
            WebrtcNativeBindings.yuvI420ToRgbaHex(
                srcY.toHex(),
                srcStrideY,
                srcU.toHex(),
                srcStrideU,
                srcV.toHex(),
                srcStrideV,
                dstRgba.size,
                dstStrideRgba,
                width,
                height,
            )
        hex.fromHexBytes(dstRgba)
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
        val raw =
            WebrtcNativeBindings.yuvArgbToI420Hex(
                srcArgb.toHex(),
                srcStrideArgb,
                dstY.size,
                dstStrideY,
                dstU.size,
                dstStrideU,
                dstV.size,
                dstStrideV,
                width,
                height,
            )
        val parts = raw.split(':')
        if (parts.size == 3) {
            parts[0].fromHexBytes(dstY)
            parts[1].fromHexBytes(dstU)
            parts[2].fromHexBytes(dstV)
        }
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
        val raw =
            WebrtcNativeBindings.yuvAbgrToI420Hex(
                srcAbgr.toHex(),
                srcStrideAbgr,
                dstY.size,
                dstStrideY,
                dstU.size,
                dstStrideU,
                dstV.size,
                dstStrideV,
                width,
                height,
            )
        val parts = raw.split(':')
        if (parts.size == 3) {
            parts[0].fromHexBytes(dstY)
            parts[1].fromHexBytes(dstU)
            parts[2].fromHexBytes(dstV)
        }
    }

    actual fun yuvArgbToRgb24(
        srcArgb: ByteArray,
        srcStrideArgb: Int,
        dstRgb24: ByteArray,
        dstStrideRgb24: Int,
        width: Int,
        height: Int,
    ) {
        val hex =
            WebrtcNativeBindings.yuvArgbToRgb24Hex(
                srcArgb.toHex(),
                srcStrideArgb,
                dstRgb24.size,
                dstStrideRgb24,
                width,
                height,
            )
        hex.fromHexBytes(dstRgb24)
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
        val raw =
            WebrtcNativeBindings.yuvI420ToNv12Hex(
                srcY.toHex(),
                srcStrideY,
                srcU.toHex(),
                srcStrideU,
                srcV.toHex(),
                srcStrideV,
                dstY.size,
                dstStrideY,
                dstUv.size,
                dstStrideUv,
                width,
                height,
            )
        val parts = raw.split(':')
        if (parts.size == 2) {
            parts[0].fromHexBytes(dstY)
            parts[1].fromHexBytes(dstUv)
        }
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
        val raw =
            WebrtcNativeBindings.yuvNv12ToI420Hex(
                srcY.toHex(),
                srcStrideY,
                srcUv.toHex(),
                srcStrideUv,
                dstY.size,
                dstStrideY,
                dstU.size,
                dstStrideU,
                dstV.size,
                dstStrideV,
                width,
                height,
            )
        val parts = raw.split(':')
        if (parts.size == 3) {
            parts[0].fromHexBytes(dstY)
            parts[1].fromHexBytes(dstU)
            parts[2].fromHexBytes(dstV)
        }
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
        val hex =
            WebrtcNativeBindings.yuvNv12ToArgbHex(
                srcY.toHex(),
                srcStrideY,
                srcUv.toHex(),
                srcStrideUv,
                dstArgb.size,
                dstStrideArgb,
                width,
                height,
            )
        hex.fromHexBytes(dstArgb)
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
        val hex =
            WebrtcNativeBindings.yuvNv12ToAbgrHex(
                srcY.toHex(),
                srcStrideY,
                srcUv.toHex(),
                srcStrideUv,
                dstAbgr.size,
                dstStrideAbgr,
                width,
                height,
            )
        hex.fromHexBytes(dstAbgr)
    }
}
