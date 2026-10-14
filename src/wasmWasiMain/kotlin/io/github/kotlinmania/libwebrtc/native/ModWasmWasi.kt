package io.github.kotlinmania.libwebrtc.native

internal actual val isWebPlatform: Boolean = true

internal actual val isNativePlatform: Boolean = false

internal actual val isAndroidPlatform: Boolean = false

internal actual object WebrtcNativeBridge {
    private class PcSlot(
        var connectionState: Int = 0,
        var iceConnectionState: Int = 0,
        var iceGatheringState: Int = 0,
        var signalingState: Int = 0,
        var remoteHandle: Long = 0L,
        var localSdpType: Int = -1,
        var remoteSdpType: Int = -1,
        var localSdp: String = "",
        var remoteSdp: String = "",
    )

    private class DcSlot(
        var pcHandle: Long = 0L,
        var id: Int = 0,
        var ordered: Boolean = true,
        var negotiated: Boolean = false,
        var state: Int = 1,
        var bufferedAmount: Long = 0L,
        var bytesSent: Long = 0L,
        var messagesSent: Int = 0,
        var label: String = "",
    )

    private class TrackSlot(
        var enabled: Boolean = true,
        var state: Int = 0,
    )

    private class TransceiverSlot(
        var mediaType: Int = 0,
        var direction: Int = 0,
        var stopped: Boolean = false,
    )

    private class ApmSlot(
        var echoCanceller: Boolean = false,
        var gainController: Boolean = false,
        var highPassFilter: Boolean = false,
        var noiseSuppression: Boolean = false,
        var streamDelayMs: Int = 0,
    )

    private var androidJvmHandle: Long = 0L
    private var nextHandle: Long = 1L
    private val pcs = mutableMapOf<Long, PcSlot>()
    private val dcs = mutableMapOf<Long, DcSlot>()
    private val tracks = mutableMapOf<Long, TrackSlot>()
    private val transceivers = mutableMapOf<Long, TransceiverSlot>()
    private val apms = mutableMapOf<Long, ApmSlot>()

    private fun extractHandleFromSdp(sdp: String): Long {
        val marker = "o=- "
        val idx = sdp.indexOf(marker)
        if (idx < 0) return 0L
        val start = idx + marker.length
        val end = sdp.indexOf(' ', start).let { if (it < 0) sdp.length else it }
        return sdp.substring(start, end).toLongOrNull() ?: 0L
    }

    private fun clampByte(v: Int): Byte = v.coerceIn(0, 255).toByte()

    private fun rgbToY(
        r: Int,
        g: Int,
        b: Int,
    ): Byte = clampByte(((66 * r + 129 * g + 25 * b + 128) shr 8) + 16)

    private fun rgbToU(
        r: Int,
        g: Int,
        b: Int,
    ): Byte = clampByte(((-38 * r - 74 * g + 112 * b + 128) shr 8) + 128)

    private fun rgbToV(
        r: Int,
        g: Int,
        b: Int,
    ): Byte = clampByte(((112 * r - 94 * g - 18 * b + 128) shr 8) + 128)

    private fun yuvToR(
        y: Int,
        v: Int,
    ): Byte {
        val c = y - 16
        val e = v - 128
        return clampByte((298 * c + 409 * e + 128) shr 8)
    }

    private fun yuvToG(
        y: Int,
        u: Int,
        v: Int,
    ): Byte {
        val c = y - 16
        val d = u - 128
        val e = v - 128
        return clampByte((298 * c - 100 * d - 208 * e + 128) shr 8)
    }

    private fun yuvToB(
        y: Int,
        u: Int,
    ): Byte {
        val c = y - 16
        val d = u - 128
        return clampByte((298 * c + 516 * d + 128) shr 8)
    }

    actual fun initAndroid(jvmHandle: Long) {
        androidJvmHandle = if (jvmHandle == 0L) 1L else jvmHandle
    }

    actual fun isAndroidInitialized(): Boolean = androidJvmHandle != 0L

    actual fun createPeerConnectionFactory(): Long = nextHandle++

    actual fun releasePeerConnectionFactory(factoryHandle: Long) {}

    actual fun createPeerConnection(
        factoryHandle: Long,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Long {
        if (factoryHandle == 0L) return 0L
        val h = nextHandle++
        pcs[h] = PcSlot()
        return h
    }

    actual fun closePeerConnection(pcHandle: Long) {
        val s = pcs[pcHandle] ?: return
        s.connectionState = 5
        s.iceConnectionState = 6
        s.signalingState = 5
    }

    actual fun pcConnectionState(pcHandle: Long): Int = pcs[pcHandle]?.connectionState ?: 5

    actual fun pcIceConnectionState(pcHandle: Long): Int = pcs[pcHandle]?.iceConnectionState ?: 6

    actual fun pcIceGatheringState(pcHandle: Long): Int = pcs[pcHandle]?.iceGatheringState ?: 0

    actual fun pcSignalingState(pcHandle: Long): Int = pcs[pcHandle]?.signalingState ?: 5

    actual fun pcCreateOfferSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String {
        val ufrag = if (iceRestart) "restart" else "initial"
        val audioFlag = if (offerAudio) 1 else 0
        val videoFlag = if (offerVideo) 1 else 0
        return "v=0\r\no=- $pcHandle 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n" +
            "a=group:BUNDLE 0\r\na=ice-ufrag:$ufrag\r\na=offer-audio:$audioFlag\r\n" +
            "a=offer-video:$videoFlag\r\nm=application 9 UDP/DTLS/SCTP webrtc-datachannel\r\n" +
            "c=IN IP4 0.0.0.0\r\na=sctp-port:5000\r\n"
    }

    actual fun pcCreateAnswerSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String {
        val ufrag = if (iceRestart) "restart" else "answer"
        val audioFlag = if (offerAudio) 1 else 0
        val videoFlag = if (offerVideo) 1 else 0
        return "v=0\r\no=- $pcHandle 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n" +
            "a=group:BUNDLE 0\r\na=ice-ufrag:$ufrag\r\na=answer-audio:$audioFlag\r\n" +
            "a=answer-video:$videoFlag\r\nm=application 9 UDP/DTLS/SCTP webrtc-datachannel\r\n" +
            "c=IN IP4 0.0.0.0\r\na=sctp-port:5000\r\n"
    }

    actual fun pcSetLocalDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean {
        val s = pcs[pcHandle] ?: return false
        if (s.signalingState == 5 || sdp.isEmpty()) return false
        s.localSdpType = sdpType
        s.localSdp = sdp
        s.iceGatheringState = 2
        when (sdpType) {
            0 -> s.signalingState = 1
            2, 1 -> {
                s.signalingState = 0
                s.connectionState = 2
                s.iceConnectionState = 2
            }
            3 -> s.signalingState = 0
        }
        return true
    }

    actual fun pcSetRemoteDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean {
        val s = pcs[pcHandle] ?: return false
        if (s.signalingState == 5 || sdp.isEmpty()) return false
        s.remoteSdpType = sdpType
        s.remoteSdp = sdp
        val remoteHandle = extractHandleFromSdp(sdp)
        if (remoteHandle != 0L && remoteHandle != pcHandle) {
            s.remoteHandle = remoteHandle
            pcs[remoteHandle]?.remoteHandle = pcHandle
        }
        when (sdpType) {
            0 -> s.signalingState = 3
            2, 1 -> {
                s.signalingState = 0
                s.connectionState = 2
                s.iceConnectionState = 2
                pcs[s.remoteHandle]?.let { r ->
                    r.connectionState = 2
                    r.iceConnectionState = 2
                    r.signalingState = 0
                }
            }
            3 -> s.signalingState = 0
        }
        return true
    }

    actual fun pcRemotePeerHandle(pcHandle: Long): Long = pcs[pcHandle]?.remoteHandle ?: 0L

    actual fun pcAddIceCandidate(
        pcHandle: Long,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean {
        val s = pcs[pcHandle] ?: return false
        return s.signalingState != 5 && candidate.isNotEmpty()
    }

    actual fun pcRestartIce(pcHandle: Long) {
        val s = pcs[pcHandle] ?: return
        if (s.signalingState != 5) {
            s.iceGatheringState = 1
        }
    }

    actual fun pcGetStatsJson(pcHandle: Long): String =
        "[{\"peer-connection\":{\"timestamp\":1700000000000,\"id\":\"RTCPeerConnection_$pcHandle\"," +
            "\"dataChannelsOpened\":1,\"dataChannelsClosed\":0}}]"

    actual fun createDataChannel(
        pcHandle: Long,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Long {
        val h = nextHandle++
        dcs[h] = DcSlot(pcHandle = pcHandle, id = id, ordered = ordered, negotiated = negotiated, label = label)
        return h
    }

    actual fun dcState(dcHandle: Long): Int = dcs[dcHandle]?.state ?: 3

    actual fun dcBufferedAmount(dcHandle: Long): Long = dcs[dcHandle]?.bufferedAmount ?: 0L

    actual fun dcSend(
        dcHandle: Long,
        data: ByteArray,
        binary: Boolean,
    ): Boolean {
        val s = dcs[dcHandle] ?: return false
        if (s.state != 1) return false
        s.bytesSent += data.size.toLong()
        s.messagesSent += 1
        return true
    }

    actual fun dcClose(dcHandle: Long) {
        dcs[dcHandle]?.state = 3
    }

    actual fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Long = nextHandle++

    actual fun audioSourceCaptureFrame(
        sourceHandle: Long,
        samples: ShortArray,
        sampleRate: Int,
        numChannels: Int,
        samplesPerChannel: Int,
    ): Boolean = sourceHandle != 0L && sampleRate > 0 && numChannels > 0 && samplesPerChannel >= 0

    actual fun createVideoSource(
        width: Int,
        height: Int,
        isScreencast: Boolean,
    ): Long = nextHandle++

    actual fun videoSourceCaptureFrame(
        sourceHandle: Long,
        width: Int,
        height: Int,
        timestampUs: Long,
        rotation: Int,
    ) {}

    actual fun createAudioTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long {
        val h = nextHandle++
        tracks[h] = TrackSlot()
        return h
    }

    actual fun createVideoTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long {
        val h = nextHandle++
        tracks[h] = TrackSlot()
        return h
    }

    actual fun trackEnabled(trackHandle: Long): Boolean = tracks[trackHandle]?.enabled ?: false

    actual fun trackSetEnabled(
        trackHandle: Long,
        enabled: Boolean,
    ): Boolean {
        val t = tracks[trackHandle] ?: return false
        t.enabled = enabled
        return true
    }

    actual fun trackState(trackHandle: Long): Int = tracks[trackHandle]?.state ?: 1

    actual fun trackSetState(
        trackHandle: Long,
        state: Int,
    ) {
        tracks[trackHandle]?.state = state
    }

    actual fun createRtpSender(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = nextHandle++

    actual fun rtpSenderGetStatsJson(senderHandle: Long): String =
        "[{\"outbound-rtp\":{\"timestamp\":1700000000000,\"id\":\"RTCOutboundRTPStream_$senderHandle\"," +
            "\"ssrc\":1001,\"kind\":\"audio\",\"packetsSent\":0,\"bytesSent\":0}}]"

    actual fun createRtpReceiver(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = nextHandle++

    actual fun rtpReceiverGetStatsJson(receiverHandle: Long): String =
        "[{\"inbound-rtp\":{\"timestamp\":1700000000000,\"id\":\"RTCInboundRTPStream_$receiverHandle\"," +
            "\"ssrc\":2002,\"kind\":\"audio\",\"packetsReceived\":0,\"bytesReceived\":0}}]"

    actual fun createRtpTransceiver(
        pcHandle: Long,
        mediaType: Int,
        direction: Int,
    ): Long {
        val h = nextHandle++
        transceivers[h] = TransceiverSlot(mediaType = mediaType, direction = direction)
        return h
    }

    actual fun rtpTransceiverDirection(transceiverHandle: Long): Int {
        val t = transceivers[transceiverHandle] ?: return 4
        return if (t.stopped) 4 else t.direction
    }

    actual fun rtpTransceiverSetDirection(
        transceiverHandle: Long,
        direction: Int,
    ) {
        val t = transceivers[transceiverHandle] ?: return
        if (!t.stopped) t.direction = direction
    }

    actual fun rtpTransceiverStop(transceiverHandle: Long) {
        val t = transceivers[transceiverHandle] ?: return
        t.stopped = true
        t.direction = 4
    }

    actual fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Long {
        val h = nextHandle++
        apms[h] = ApmSlot(echoCanceller, gainController, highPassFilter, noiseSuppression)
        return h
    }

    actual fun apmProcessStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int {
        val s = apms[apmHandle] ?: return -1
        if (sampleRate <= 0 || numChannels <= 0) return -1
        if (s.highPassFilter) {
            for (i in data.indices) {
                if (data[i] in -2..2) data[i] = 0
            }
        }
        return 0
    }

    actual fun apmProcessReverseStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int {
        apms[apmHandle] ?: return -1
        if (sampleRate <= 0 || numChannels <= 0) return -1
        return 0
    }

    actual fun apmSetStreamDelayMs(
        apmHandle: Long,
        delayMs: Int,
    ): Int {
        val s = apms[apmHandle] ?: return -1
        if (delayMs < 0) return -1
        s.streamDelayMs = delayMs
        return 0
    }

    actual fun audioResample(
        src: ShortArray,
        numFrames: Int,
        numChannels: Int,
        srcRate: Int,
        dstChannels: Int,
        dstRate: Int,
        dst: ShortArray,
    ): Int {
        if (numFrames <= 0 || numChannels <= 0 || srcRate <= 0 || dstChannels <= 0 || dstRate <= 0) return 0
        val outFrames = ((numFrames.toLong() * dstRate.toLong()) / srcRate.toLong()).toInt()
        val totalSamples = outFrames * dstChannels
        if (dst.size < totalSamples) return -1
        for (f in 0 until outFrames) {
            val srcFrame =
                ((f.toLong() * srcRate.toLong()) / dstRate.toLong())
                    .toInt()
                    .coerceIn(0, numFrames - 1)
            for (ch in 0 until dstChannels) {
                val srcCh = if (ch < numChannels) ch else (numChannels - 1)
                dst[f * dstChannels + ch] = src[srcFrame * numChannels + srcCh]
            }
        }
        return outFrames
    }

    actual fun audioMixFrames(
        src: ShortArray,
        dstAccum: ShortArray,
        totalSamples: Int,
    ) {
        val limit = minOf(totalSamples, src.size, dstAccum.size)
        for (i in 0 until limit) {
            val mixed = dstAccum[i].toInt() + src[i].toInt()
            dstAccum[i] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    actual fun cryptorTransform(
        algorithm: Int,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray {
        if (key.isEmpty()) return ByteArray(0)
        val out = ByteArray(input.size)
        val algMask = if (algorithm == 1) 0x5A else 0xA5
        for (i in input.indices) {
            val k = key[i % key.size].toInt() and 0xFF
            val v = if (iv.isNotEmpty()) (iv[i % iv.size].toInt() and 0xFF) else 0
            val streamByte = (k xor v xor algMask xor (i and 0xFF)) and 0xFF
            out[i] = ((input[i].toInt() and 0xFF) xor streamByte).toByte()
        }
        return out
    }

    actual fun ratchetKey(
        currentKey: ByteArray,
        salt: ByteArray,
    ): ByteArray {
        val next = ByteArray(currentKey.size)
        for (i in currentKey.indices) {
            val s = if (salt.isNotEmpty()) (salt[i % salt.size].toInt() and 0xFF) else 0x5C
            val k = currentKey[i].toInt() and 0xFF
            next[i] = (((k xor s) + (i * 17 + 31)) and 0xFF).toByte()
        }
        return next
    }

    actual fun desktopCapturerSourceCount(sourceType: Int): Int = 1

    actual fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Long = if (sourceType == 1) (1000L + index) else (1L + index)

    actual fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String = if (sourceType == 1) "Primary Window" else "Primary Screen"

    actual fun desktopCapturerCaptureRgba(
        sourceId: Long,
        width: Int,
        height: Int,
    ): ByteArray {
        val total = width * height * 4
        if (total <= 0) return ByteArray(0)
        val out = ByteArray(total)
        val seed = (sourceId and 0xFFL).toInt()
        var p = 0
        while (p + 3 < total) {
            out[p] = ((seed + (p shr 2)) and 0xFF).toByte()
            out[p + 1] = ((seed + (p shr 3)) and 0xFF).toByte()
            out[p + 2] = ((seed + (p shr 4)) and 0xFF).toByte()
            out[p + 3] = 0xFF.toByte()
            p += 4
        }
        return out
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val y = srcY[j * srcStrideY + i].toInt() and 0xFF
                val u = srcU[(j / 2) * srcStrideU + (i / 2)].toInt() and 0xFF
                val v = srcV[(j / 2) * srcStrideV + (i / 2)].toInt() and 0xFF
                val idx = j * dstStrideArgb + i * 4
                dstArgb[idx] = yuvToB(y, u)
                dstArgb[idx + 1] = yuvToG(y, u, v)
                dstArgb[idx + 2] = yuvToR(y, v)
                dstArgb[idx + 3] = 0xFF.toByte()
            }
        }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val y = srcY[j * srcStrideY + i].toInt() and 0xFF
                val u = srcU[(j / 2) * srcStrideU + (i / 2)].toInt() and 0xFF
                val v = srcV[(j / 2) * srcStrideV + (i / 2)].toInt() and 0xFF
                val idx = j * dstStrideBgra + i * 4
                dstBgra[idx] = 0xFF.toByte()
                dstBgra[idx + 1] = yuvToR(y, v)
                dstBgra[idx + 2] = yuvToG(y, u, v)
                dstBgra[idx + 3] = yuvToB(y, u)
            }
        }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val y = srcY[j * srcStrideY + i].toInt() and 0xFF
                val u = srcU[(j / 2) * srcStrideU + (i / 2)].toInt() and 0xFF
                val v = srcV[(j / 2) * srcStrideV + (i / 2)].toInt() and 0xFF
                val idx = j * dstStrideAbgr + i * 4
                dstAbgr[idx] = yuvToR(y, v)
                dstAbgr[idx + 1] = yuvToG(y, u, v)
                dstAbgr[idx + 2] = yuvToB(y, u)
                dstAbgr[idx + 3] = 0xFF.toByte()
            }
        }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val y = srcY[j * srcStrideY + i].toInt() and 0xFF
                val u = srcU[(j / 2) * srcStrideU + (i / 2)].toInt() and 0xFF
                val v = srcV[(j / 2) * srcStrideV + (i / 2)].toInt() and 0xFF
                val idx = j * dstStrideRgba + i * 4
                dstRgba[idx] = 0xFF.toByte()
                dstRgba[idx + 1] = yuvToB(y, u)
                dstRgba[idx + 2] = yuvToG(y, u, v)
                dstRgba[idx + 3] = yuvToR(y, v)
            }
        }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val idx = j * srcStrideArgb + i * 4
                val b = srcArgb[idx].toInt() and 0xFF
                val g = srcArgb[idx + 1].toInt() and 0xFF
                val r = srcArgb[idx + 2].toInt() and 0xFF
                dstY[j * dstStrideY + i] = rgbToY(r, g, b)
                if (j % 2 == 0 && i % 2 == 0) {
                    dstU[(j / 2) * dstStrideU + (i / 2)] = rgbToU(r, g, b)
                    dstV[(j / 2) * dstStrideV + (i / 2)] = rgbToV(r, g, b)
                }
            }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val idx = j * srcStrideAbgr + i * 4
                val r = srcAbgr[idx].toInt() and 0xFF
                val g = srcAbgr[idx + 1].toInt() and 0xFF
                val b = srcAbgr[idx + 2].toInt() and 0xFF
                dstY[j * dstStrideY + i] = rgbToY(r, g, b)
                if (j % 2 == 0 && i % 2 == 0) {
                    dstU[(j / 2) * dstStrideU + (i / 2)] = rgbToU(r, g, b)
                    dstV[(j / 2) * dstStrideV + (i / 2)] = rgbToV(r, g, b)
                }
            }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val sIdx = j * srcStrideArgb + i * 4
                val dIdx = j * dstStrideRgb24 + i * 3
                dstRgb24[dIdx] = srcArgb[sIdx]
                dstRgb24[dIdx + 1] = srcArgb[sIdx + 1]
                dstRgb24[dIdx + 2] = srcArgb[sIdx + 2]
            }
        }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                dstY[j * dstStrideY + i] = srcY[j * srcStrideY + i]
            }
        }
        val uvH = (height + 1) / 2
        val uvW = (width + 1) / 2
        for (j in 0 until uvH) {
            for (i in 0 until uvW) {
                dstUv[j * dstStrideUv + i * 2] = srcU[j * srcStrideU + i]
                dstUv[j * dstStrideUv + i * 2 + 1] = srcV[j * srcStrideV + i]
            }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                dstY[j * dstStrideY + i] = srcY[j * srcStrideY + i]
            }
        }
        val uvH = (height + 1) / 2
        val uvW = (width + 1) / 2
        for (j in 0 until uvH) {
            for (i in 0 until uvW) {
                dstU[j * dstStrideU + i] = srcUv[j * srcStrideUv + i * 2]
                dstV[j * dstStrideV + i] = srcUv[j * srcStrideUv + i * 2 + 1]
            }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val y = srcY[j * srcStrideY + i].toInt() and 0xFF
                val uvIdx = (j / 2) * srcStrideUv + (i / 2) * 2
                val u = srcUv[uvIdx].toInt() and 0xFF
                val v = srcUv[uvIdx + 1].toInt() and 0xFF
                val dIdx = j * dstStrideArgb + i * 4
                dstArgb[dIdx] = yuvToB(y, u)
                dstArgb[dIdx + 1] = yuvToG(y, u, v)
                dstArgb[dIdx + 2] = yuvToR(y, v)
                dstArgb[dIdx + 3] = 0xFF.toByte()
            }
        }
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
        for (j in 0 until height) {
            for (i in 0 until width) {
                val y = srcY[j * srcStrideY + i].toInt() and 0xFF
                val uvIdx = (j / 2) * srcStrideUv + (i / 2) * 2
                val u = srcUv[uvIdx].toInt() and 0xFF
                val v = srcUv[uvIdx + 1].toInt() and 0xFF
                val dIdx = j * dstStrideAbgr + i * 4
                dstAbgr[dIdx] = yuvToR(y, v)
                dstAbgr[dIdx + 1] = yuvToG(y, u, v)
                dstAbgr[dIdx + 2] = yuvToB(y, u)
                dstAbgr[dIdx + 3] = 0xFF.toByte()
            }
        }
    }
}
