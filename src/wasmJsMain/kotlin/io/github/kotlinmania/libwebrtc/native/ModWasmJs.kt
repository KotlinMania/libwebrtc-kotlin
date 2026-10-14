@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.internal.loadWebrtcAddon
import io.github.kotlinmania.libwebrtc.internal.napiApmCreate
import io.github.kotlinmania.libwebrtc.internal.napiApmProcessReverseStreamHex
import io.github.kotlinmania.libwebrtc.internal.napiApmProcessStreamHex
import io.github.kotlinmania.libwebrtc.internal.napiApmSetStreamDelayMs
import io.github.kotlinmania.libwebrtc.internal.napiAudioMixFramesHex
import io.github.kotlinmania.libwebrtc.internal.napiAudioResampleHex
import io.github.kotlinmania.libwebrtc.internal.napiAudioSourceCaptureFrameHex
import io.github.kotlinmania.libwebrtc.internal.napiClosePeerConnection
import io.github.kotlinmania.libwebrtc.internal.napiCreateAudioSource
import io.github.kotlinmania.libwebrtc.internal.napiCreateAudioTrack
import io.github.kotlinmania.libwebrtc.internal.napiCreateDataChannel
import io.github.kotlinmania.libwebrtc.internal.napiCreatePeerConnection
import io.github.kotlinmania.libwebrtc.internal.napiCreatePeerConnectionFactory
import io.github.kotlinmania.libwebrtc.internal.napiCreateRtpReceiver
import io.github.kotlinmania.libwebrtc.internal.napiCreateRtpSender
import io.github.kotlinmania.libwebrtc.internal.napiCreateRtpTransceiver
import io.github.kotlinmania.libwebrtc.internal.napiCreateVideoSource
import io.github.kotlinmania.libwebrtc.internal.napiCreateVideoTrack
import io.github.kotlinmania.libwebrtc.internal.napiCryptorTransformHex
import io.github.kotlinmania.libwebrtc.internal.napiDcBufferedAmount
import io.github.kotlinmania.libwebrtc.internal.napiDcClose
import io.github.kotlinmania.libwebrtc.internal.napiDcSendHex
import io.github.kotlinmania.libwebrtc.internal.napiDcState
import io.github.kotlinmania.libwebrtc.internal.napiDesktopCapturerCaptureRgbaHex
import io.github.kotlinmania.libwebrtc.internal.napiDesktopCapturerSourceCount
import io.github.kotlinmania.libwebrtc.internal.napiDesktopCapturerSourceId
import io.github.kotlinmania.libwebrtc.internal.napiDesktopCapturerSourceTitle
import io.github.kotlinmania.libwebrtc.internal.napiInitAndroid
import io.github.kotlinmania.libwebrtc.internal.napiIsAndroidInitialized
import io.github.kotlinmania.libwebrtc.internal.napiPcAddIceCandidate
import io.github.kotlinmania.libwebrtc.internal.napiPcConnectionState
import io.github.kotlinmania.libwebrtc.internal.napiPcCreateAnswerSdp
import io.github.kotlinmania.libwebrtc.internal.napiPcCreateOfferSdp
import io.github.kotlinmania.libwebrtc.internal.napiPcGetStatsJson
import io.github.kotlinmania.libwebrtc.internal.napiPcIceConnectionState
import io.github.kotlinmania.libwebrtc.internal.napiPcIceGatheringState
import io.github.kotlinmania.libwebrtc.internal.napiPcRemotePeerHandle
import io.github.kotlinmania.libwebrtc.internal.napiPcRestartIce
import io.github.kotlinmania.libwebrtc.internal.napiPcSetLocalDescription
import io.github.kotlinmania.libwebrtc.internal.napiPcSetRemoteDescription
import io.github.kotlinmania.libwebrtc.internal.napiPcSignalingState
import io.github.kotlinmania.libwebrtc.internal.napiRatchetKeyHex
import io.github.kotlinmania.libwebrtc.internal.napiReleasePeerConnectionFactory
import io.github.kotlinmania.libwebrtc.internal.napiRtpReceiverGetStatsJson
import io.github.kotlinmania.libwebrtc.internal.napiRtpSenderGetStatsJson
import io.github.kotlinmania.libwebrtc.internal.napiRtpTransceiverDirection
import io.github.kotlinmania.libwebrtc.internal.napiRtpTransceiverSetDirection
import io.github.kotlinmania.libwebrtc.internal.napiRtpTransceiverStop
import io.github.kotlinmania.libwebrtc.internal.napiTrackEnabled
import io.github.kotlinmania.libwebrtc.internal.napiTrackSetEnabled
import io.github.kotlinmania.libwebrtc.internal.napiTrackSetState
import io.github.kotlinmania.libwebrtc.internal.napiTrackState
import io.github.kotlinmania.libwebrtc.internal.napiVideoSourceCaptureFrame
import io.github.kotlinmania.libwebrtc.internal.napiYuvAbgrToI420Hex
import io.github.kotlinmania.libwebrtc.internal.napiYuvArgbToI420Hex
import io.github.kotlinmania.libwebrtc.internal.napiYuvArgbToRgb24Hex
import io.github.kotlinmania.libwebrtc.internal.napiYuvI420ToAbgrHex
import io.github.kotlinmania.libwebrtc.internal.napiYuvI420ToArgbHex
import io.github.kotlinmania.libwebrtc.internal.napiYuvI420ToBgraHex
import io.github.kotlinmania.libwebrtc.internal.napiYuvI420ToNv12Hex
import io.github.kotlinmania.libwebrtc.internal.napiYuvI420ToRgbaHex
import io.github.kotlinmania.libwebrtc.internal.napiYuvNv12ToAbgrHex
import io.github.kotlinmania.libwebrtc.internal.napiYuvNv12ToArgbHex
import io.github.kotlinmania.libwebrtc.internal.napiYuvNv12ToI420Hex

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

internal actual object WebrtcNativeBridge {
    private val addon: JsAny = checkNotNull(loadWebrtcAddon()) { "Failed to load @kotlinmania/webrtc-native-bindings" }

    actual fun initAndroid(jvmHandle: Long) {
        napiInitAndroid(addon, jvmHandle.toDouble())
    }

    actual fun isAndroidInitialized(): Boolean = napiIsAndroidInitialized(addon)

    actual fun createPeerConnectionFactory(): Long = napiCreatePeerConnectionFactory(addon).toLong()

    actual fun releasePeerConnectionFactory(factoryHandle: Long) {
        napiReleasePeerConnectionFactory(addon, factoryHandle.toDouble())
    }

    actual fun createPeerConnection(
        factoryHandle: Long,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Long =
        napiCreatePeerConnection(
            addon,
            factoryHandle.toDouble(),
            iceTransportType,
            continualGatheringPolicy,
            iceServersCount,
        ).toLong()

    actual fun closePeerConnection(pcHandle: Long) {
        napiClosePeerConnection(addon, pcHandle.toDouble())
    }

    actual fun pcConnectionState(pcHandle: Long): Int = napiPcConnectionState(addon, pcHandle.toDouble())

    actual fun pcIceConnectionState(pcHandle: Long): Int = napiPcIceConnectionState(addon, pcHandle.toDouble())

    actual fun pcIceGatheringState(pcHandle: Long): Int = napiPcIceGatheringState(addon, pcHandle.toDouble())

    actual fun pcSignalingState(pcHandle: Long): Int = napiPcSignalingState(addon, pcHandle.toDouble())

    actual fun pcCreateOfferSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String = napiPcCreateOfferSdp(addon, pcHandle.toDouble(), iceRestart, offerAudio, offerVideo)

    actual fun pcCreateAnswerSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String = napiPcCreateAnswerSdp(addon, pcHandle.toDouble(), iceRestart, offerAudio, offerVideo)

    actual fun pcSetLocalDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = napiPcSetLocalDescription(addon, pcHandle.toDouble(), sdpType, sdp)

    actual fun pcSetRemoteDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = napiPcSetRemoteDescription(addon, pcHandle.toDouble(), sdpType, sdp)

    actual fun pcRemotePeerHandle(pcHandle: Long): Long = napiPcRemotePeerHandle(addon, pcHandle.toDouble()).toLong()

    actual fun pcAddIceCandidate(
        pcHandle: Long,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean = napiPcAddIceCandidate(addon, pcHandle.toDouble(), sdpMid, sdpMlineIndex, candidate)

    actual fun pcRestartIce(pcHandle: Long) {
        napiPcRestartIce(addon, pcHandle.toDouble())
    }

    actual fun pcGetStatsJson(pcHandle: Long): String = napiPcGetStatsJson(addon, pcHandle.toDouble())

    actual fun createDataChannel(
        pcHandle: Long,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Long = napiCreateDataChannel(addon, pcHandle.toDouble(), label, id, ordered, negotiated).toLong()

    actual fun dcState(dcHandle: Long): Int = napiDcState(addon, dcHandle.toDouble())

    actual fun dcBufferedAmount(dcHandle: Long): Long = napiDcBufferedAmount(addon, dcHandle.toDouble()).toLong()

    actual fun dcSend(
        dcHandle: Long,
        data: ByteArray,
        binary: Boolean,
    ): Boolean = napiDcSendHex(addon, dcHandle.toDouble(), data.toHex(), binary)

    actual fun dcClose(dcHandle: Long) {
        napiDcClose(addon, dcHandle.toDouble())
    }

    actual fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Long =
        napiCreateAudioSource(
            addon,
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
        napiAudioSourceCaptureFrameHex(
            addon,
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
    ): Long = napiCreateVideoSource(addon, width, height, isScreencast).toLong()

    actual fun videoSourceCaptureFrame(
        sourceHandle: Long,
        width: Int,
        height: Int,
        timestampUs: Long,
        rotation: Int,
    ) {
        napiVideoSourceCaptureFrame(
            addon,
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
    ): Long = napiCreateAudioTrack(addon, factoryHandle.toDouble(), id, sourceHandle.toDouble()).toLong()

    actual fun createVideoTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long = napiCreateVideoTrack(addon, factoryHandle.toDouble(), id, sourceHandle.toDouble()).toLong()

    actual fun trackEnabled(trackHandle: Long): Boolean = napiTrackEnabled(addon, trackHandle.toDouble())

    actual fun trackSetEnabled(
        trackHandle: Long,
        enabled: Boolean,
    ): Boolean = napiTrackSetEnabled(addon, trackHandle.toDouble(), enabled)

    actual fun trackState(trackHandle: Long): Int = napiTrackState(addon, trackHandle.toDouble())

    actual fun trackSetState(
        trackHandle: Long,
        state: Int,
    ) {
        napiTrackSetState(addon, trackHandle.toDouble(), state)
    }

    actual fun createRtpSender(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = napiCreateRtpSender(addon, pcHandle.toDouble(), trackHandle.toDouble()).toLong()

    actual fun rtpSenderGetStatsJson(senderHandle: Long): String =
        napiRtpSenderGetStatsJson(addon, senderHandle.toDouble())

    actual fun createRtpReceiver(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = napiCreateRtpReceiver(addon, pcHandle.toDouble(), trackHandle.toDouble()).toLong()

    actual fun rtpReceiverGetStatsJson(receiverHandle: Long): String =
        napiRtpReceiverGetStatsJson(addon, receiverHandle.toDouble())

    actual fun createRtpTransceiver(
        pcHandle: Long,
        mediaType: Int,
        direction: Int,
    ): Long = napiCreateRtpTransceiver(addon, pcHandle.toDouble(), mediaType, direction).toLong()

    actual fun rtpTransceiverDirection(transceiverHandle: Long): Int =
        napiRtpTransceiverDirection(addon, transceiverHandle.toDouble())

    actual fun rtpTransceiverSetDirection(
        transceiverHandle: Long,
        direction: Int,
    ) {
        napiRtpTransceiverSetDirection(addon, transceiverHandle.toDouble(), direction)
    }

    actual fun rtpTransceiverStop(transceiverHandle: Long) {
        napiRtpTransceiverStop(addon, transceiverHandle.toDouble())
    }

    actual fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Long = napiApmCreate(addon, echoCanceller, gainController, highPassFilter, noiseSuppression).toLong()

    actual fun apmProcessStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int {
        val raw = napiApmProcessStreamHex(addon, apmHandle.toDouble(), data.toHex16(), sampleRate, numChannels)
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
        val raw = napiApmProcessReverseStreamHex(addon, apmHandle.toDouble(), data.toHex16(), sampleRate, numChannels)
        val sep = raw.indexOf(':')
        if (sep < 0) return -1
        val code = raw.substring(0, sep).toIntOrNull() ?: -1
        raw.substring(sep + 1).fromHex16Shorts(data)
        return code
    }

    actual fun apmSetStreamDelayMs(
        apmHandle: Long,
        delayMs: Int,
    ): Int = napiApmSetStreamDelayMs(addon, apmHandle.toDouble(), delayMs)

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
            napiAudioResampleHex(
                addon,
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
        val outHex = napiAudioMixFramesHex(addon, src.toHex16(), dstAccum.toHex16(), totalSamples)
        outHex.fromHex16Shorts(dstAccum)
    }

    actual fun cryptorTransform(
        algorithm: Int,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray {
        val hex = napiCryptorTransformHex(addon, algorithm, key.toHex(), iv.toHex(), input.toHex())
        return hex.fromHexBytes()
    }

    actual fun ratchetKey(
        currentKey: ByteArray,
        salt: ByteArray,
    ): ByteArray {
        val hex = napiRatchetKeyHex(addon, currentKey.toHex(), salt.toHex())
        return hex.fromHexBytes()
    }

    actual fun desktopCapturerSourceCount(sourceType: Int): Int = napiDesktopCapturerSourceCount(addon, sourceType)

    actual fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Long = napiDesktopCapturerSourceId(addon, sourceType, index).toLong()

    actual fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String = napiDesktopCapturerSourceTitle(addon, sourceType, index)

    actual fun desktopCapturerCaptureRgba(
        sourceId: Long,
        width: Int,
        height: Int,
    ): ByteArray {
        val hex = napiDesktopCapturerCaptureRgbaHex(addon, sourceId.toDouble(), width, height)
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
            napiYuvI420ToArgbHex(
                addon,
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
            napiYuvI420ToBgraHex(
                addon,
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
            napiYuvI420ToAbgrHex(
                addon,
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
            napiYuvI420ToRgbaHex(
                addon,
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
            napiYuvArgbToI420Hex(
                addon,
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
            napiYuvAbgrToI420Hex(
                addon,
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
            napiYuvArgbToRgb24Hex(
                addon,
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
            napiYuvI420ToNv12Hex(
                addon,
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
            napiYuvNv12ToI420Hex(
                addon,
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
            napiYuvNv12ToArgbHex(
                addon,
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
            napiYuvNv12ToAbgrHex(
                addon,
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
