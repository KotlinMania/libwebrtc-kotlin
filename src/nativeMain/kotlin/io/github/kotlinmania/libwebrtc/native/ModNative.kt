@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.kotlinmania.libwebrtc.native

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ShortVar
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import webrtc.cinterop.webrtc_apm_create
import webrtc.cinterop.webrtc_apm_process_reverse_stream
import webrtc.cinterop.webrtc_apm_process_stream
import webrtc.cinterop.webrtc_apm_set_stream_delay_ms
import webrtc.cinterop.webrtc_audio_mix_frames
import webrtc.cinterop.webrtc_audio_resample
import webrtc.cinterop.webrtc_audio_source_capture_frame
import webrtc.cinterop.webrtc_close_peer_connection
import webrtc.cinterop.webrtc_create_audio_source
import webrtc.cinterop.webrtc_create_audio_track
import webrtc.cinterop.webrtc_create_data_channel
import webrtc.cinterop.webrtc_create_peer_connection
import webrtc.cinterop.webrtc_create_peer_connection_factory
import webrtc.cinterop.webrtc_create_rtp_receiver
import webrtc.cinterop.webrtc_create_rtp_sender
import webrtc.cinterop.webrtc_create_rtp_transceiver
import webrtc.cinterop.webrtc_create_video_source
import webrtc.cinterop.webrtc_create_video_track
import webrtc.cinterop.webrtc_cryptor_transform
import webrtc.cinterop.webrtc_dc_buffered_amount
import webrtc.cinterop.webrtc_dc_close
import webrtc.cinterop.webrtc_dc_send
import webrtc.cinterop.webrtc_dc_state
import webrtc.cinterop.webrtc_desktop_capturer_capture_rgba
import webrtc.cinterop.webrtc_desktop_capturer_source_count
import webrtc.cinterop.webrtc_desktop_capturer_source_id
import webrtc.cinterop.webrtc_desktop_capturer_source_title
import webrtc.cinterop.webrtc_init_android
import webrtc.cinterop.webrtc_is_android_initialized
import webrtc.cinterop.webrtc_pc_add_ice_candidate
import webrtc.cinterop.webrtc_pc_connection_state
import webrtc.cinterop.webrtc_pc_create_answer_sdp
import webrtc.cinterop.webrtc_pc_create_offer_sdp
import webrtc.cinterop.webrtc_pc_get_stats_json
import webrtc.cinterop.webrtc_pc_ice_connection_state
import webrtc.cinterop.webrtc_pc_ice_gathering_state
import webrtc.cinterop.webrtc_pc_remote_peer_handle
import webrtc.cinterop.webrtc_pc_restart_ice
import webrtc.cinterop.webrtc_pc_set_local_description
import webrtc.cinterop.webrtc_pc_set_remote_description
import webrtc.cinterop.webrtc_pc_signaling_state
import webrtc.cinterop.webrtc_ratchet_key
import webrtc.cinterop.webrtc_release_peer_connection_factory
import webrtc.cinterop.webrtc_rtp_receiver_get_stats_json
import webrtc.cinterop.webrtc_rtp_sender_get_stats_json
import webrtc.cinterop.webrtc_rtp_transceiver_direction
import webrtc.cinterop.webrtc_rtp_transceiver_set_direction
import webrtc.cinterop.webrtc_rtp_transceiver_stop
import webrtc.cinterop.webrtc_track_enabled
import webrtc.cinterop.webrtc_track_set_enabled
import webrtc.cinterop.webrtc_track_set_state
import webrtc.cinterop.webrtc_track_state
import webrtc.cinterop.webrtc_video_source_capture_frame
import webrtc.cinterop.webrtc_yuv_abgr_to_i420
import webrtc.cinterop.webrtc_yuv_argb_to_i420
import webrtc.cinterop.webrtc_yuv_argb_to_rgb24
import webrtc.cinterop.webrtc_yuv_i420_to_abgr
import webrtc.cinterop.webrtc_yuv_i420_to_argb
import webrtc.cinterop.webrtc_yuv_i420_to_bgra
import webrtc.cinterop.webrtc_yuv_i420_to_nv12
import webrtc.cinterop.webrtc_yuv_i420_to_rgba
import webrtc.cinterop.webrtc_yuv_nv12_to_abgr
import webrtc.cinterop.webrtc_yuv_nv12_to_argb
import webrtc.cinterop.webrtc_yuv_nv12_to_i420

private inline fun <R> ByteArray.withUBytePtr(block: (CPointer<UByteVar>?) -> R): R =
    if (isEmpty()) {
        block(null)
    } else {
        usePinned { pinned ->
            block(pinned.addressOf(0).reinterpret())
        }
    }

private inline fun <R> ShortArray.withShortPtr(block: (CPointer<ShortVar>?) -> R): R =
    if (isEmpty()) {
        block(null)
    } else {
        usePinned { pinned ->
            block(pinned.addressOf(0))
        }
    }

internal actual val isWebPlatform: Boolean = false

internal actual val isNativePlatform: Boolean = true

internal actual val isAndroidPlatform: Boolean = false

internal actual object WebrtcNativeBridge {
    actual fun initAndroid(jvmHandle: Long) {
        webrtc_init_android(jvmHandle.toULong())
    }

    actual fun isAndroidInitialized(): Boolean = webrtc_is_android_initialized() != 0

    actual fun createPeerConnectionFactory(): Long = webrtc_create_peer_connection_factory().toLong()

    actual fun releasePeerConnectionFactory(factoryHandle: Long) {
        webrtc_release_peer_connection_factory(factoryHandle.toULong())
    }

    actual fun createPeerConnection(
        factoryHandle: Long,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Long =
        webrtc_create_peer_connection(
            factoryHandle.toULong(),
            iceTransportType,
            continualGatheringPolicy,
            iceServersCount,
        ).toLong()

    actual fun closePeerConnection(pcHandle: Long) {
        webrtc_close_peer_connection(pcHandle.toULong())
    }

    actual fun pcConnectionState(pcHandle: Long): Int = webrtc_pc_connection_state(pcHandle.toULong())

    actual fun pcIceConnectionState(pcHandle: Long): Int = webrtc_pc_ice_connection_state(pcHandle.toULong())

    actual fun pcIceGatheringState(pcHandle: Long): Int = webrtc_pc_ice_gathering_state(pcHandle.toULong())

    actual fun pcSignalingState(pcHandle: Long): Int = webrtc_pc_signaling_state(pcHandle.toULong())

    actual fun pcCreateOfferSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String =
        webrtc_pc_create_offer_sdp(
            pcHandle.toULong(),
            if (iceRestart) 1 else 0,
            if (offerAudio) 1 else 0,
            if (offerVideo) 1 else 0,
        )?.toKString() ?: ""

    actual fun pcCreateAnswerSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String =
        webrtc_pc_create_answer_sdp(
            pcHandle.toULong(),
            if (iceRestart) 1 else 0,
            if (offerAudio) 1 else 0,
            if (offerVideo) 1 else 0,
        )?.toKString() ?: ""

    actual fun pcSetLocalDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = webrtc_pc_set_local_description(pcHandle.toULong(), sdpType, sdp) != 0

    actual fun pcSetRemoteDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean = webrtc_pc_set_remote_description(pcHandle.toULong(), sdpType, sdp) != 0

    actual fun pcRemotePeerHandle(pcHandle: Long): Long = webrtc_pc_remote_peer_handle(pcHandle.toULong()).toLong()

    actual fun pcAddIceCandidate(
        pcHandle: Long,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean = webrtc_pc_add_ice_candidate(pcHandle.toULong(), sdpMid, sdpMlineIndex, candidate) != 0

    actual fun pcRestartIce(pcHandle: Long) {
        webrtc_pc_restart_ice(pcHandle.toULong())
    }

    actual fun pcGetStatsJson(pcHandle: Long): String = webrtc_pc_get_stats_json(pcHandle.toULong())?.toKString() ?: "[]"

    actual fun createDataChannel(
        pcHandle: Long,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Long =
        webrtc_create_data_channel(
            pcHandle.toULong(),
            label,
            id,
            if (ordered) 1 else 0,
            if (negotiated) 1 else 0,
        ).toLong()

    actual fun dcState(dcHandle: Long): Int = webrtc_dc_state(dcHandle.toULong())

    actual fun dcBufferedAmount(dcHandle: Long): Long = webrtc_dc_buffered_amount(dcHandle.toULong()).toLong()

    actual fun dcSend(
        dcHandle: Long,
        data: ByteArray,
        binary: Boolean,
    ): Boolean =
        data.withUBytePtr { ptr ->
            webrtc_dc_send(dcHandle.toULong(), ptr, data.size, if (binary) 1 else 0) != 0
        }

    actual fun dcClose(dcHandle: Long) {
        webrtc_dc_close(dcHandle.toULong())
    }

    actual fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Long =
        webrtc_create_audio_source(
            sampleRate,
            numChannels,
            if (echoCancellation) 1 else 0,
            if (noiseSuppression) 1 else 0,
            if (autoGainControl) 1 else 0,
        ).toLong()

    actual fun audioSourceCaptureFrame(
        sourceHandle: Long,
        samples: ShortArray,
        sampleRate: Int,
        numChannels: Int,
        samplesPerChannel: Int,
    ): Boolean =
        samples.withShortPtr { ptr ->
            webrtc_audio_source_capture_frame(
                sourceHandle.toULong(),
                ptr,
                samples.size,
                sampleRate,
                numChannels,
                samplesPerChannel,
            ) != 0
        }

    actual fun createVideoSource(
        width: Int,
        height: Int,
        isScreencast: Boolean,
    ): Long = webrtc_create_video_source(width, height, if (isScreencast) 1 else 0).toLong()

    actual fun videoSourceCaptureFrame(
        sourceHandle: Long,
        width: Int,
        height: Int,
        timestampUs: Long,
        rotation: Int,
    ) {
        webrtc_video_source_capture_frame(sourceHandle.toULong(), width, height, timestampUs, rotation)
    }

    actual fun createAudioTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long = webrtc_create_audio_track(factoryHandle.toULong(), id, sourceHandle.toULong()).toLong()

    actual fun createVideoTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long = webrtc_create_video_track(factoryHandle.toULong(), id, sourceHandle.toULong()).toLong()

    actual fun trackEnabled(trackHandle: Long): Boolean = webrtc_track_enabled(trackHandle.toULong()) != 0

    actual fun trackSetEnabled(
        trackHandle: Long,
        enabled: Boolean,
    ): Boolean = webrtc_track_set_enabled(trackHandle.toULong(), if (enabled) 1 else 0) != 0

    actual fun trackState(trackHandle: Long): Int = webrtc_track_state(trackHandle.toULong())

    actual fun trackSetState(
        trackHandle: Long,
        state: Int,
    ) {
        webrtc_track_set_state(trackHandle.toULong(), state)
    }

    actual fun createRtpSender(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = webrtc_create_rtp_sender(pcHandle.toULong(), trackHandle.toULong()).toLong()

    actual fun rtpSenderGetStatsJson(senderHandle: Long): String =
        webrtc_rtp_sender_get_stats_json(senderHandle.toULong())?.toKString() ?: "[]"

    actual fun createRtpReceiver(
        pcHandle: Long,
        trackHandle: Long,
    ): Long = webrtc_create_rtp_receiver(pcHandle.toULong(), trackHandle.toULong()).toLong()

    actual fun rtpReceiverGetStatsJson(receiverHandle: Long): String =
        webrtc_rtp_receiver_get_stats_json(receiverHandle.toULong())?.toKString() ?: "[]"

    actual fun createRtpTransceiver(
        pcHandle: Long,
        mediaType: Int,
        direction: Int,
    ): Long = webrtc_create_rtp_transceiver(pcHandle.toULong(), mediaType, direction).toLong()

    actual fun rtpTransceiverDirection(transceiverHandle: Long): Int =
        webrtc_rtp_transceiver_direction(transceiverHandle.toULong())

    actual fun rtpTransceiverSetDirection(
        transceiverHandle: Long,
        direction: Int,
    ) {
        webrtc_rtp_transceiver_set_direction(transceiverHandle.toULong(), direction)
    }

    actual fun rtpTransceiverStop(transceiverHandle: Long) {
        webrtc_rtp_transceiver_stop(transceiverHandle.toULong())
    }

    actual fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Long =
        webrtc_apm_create(
            if (echoCanceller) 1 else 0,
            if (gainController) 1 else 0,
            if (highPassFilter) 1 else 0,
            if (noiseSuppression) 1 else 0,
        ).toLong()

    actual fun apmProcessStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int =
        data.withShortPtr { ptr ->
            webrtc_apm_process_stream(apmHandle.toULong(), ptr, data.size, sampleRate, numChannels)
        }

    actual fun apmProcessReverseStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int =
        data.withShortPtr { ptr ->
            webrtc_apm_process_reverse_stream(apmHandle.toULong(), ptr, data.size, sampleRate, numChannels)
        }

    actual fun apmSetStreamDelayMs(
        apmHandle: Long,
        delayMs: Int,
    ): Int = webrtc_apm_set_stream_delay_ms(apmHandle.toULong(), delayMs)

    actual fun audioResample(
        src: ShortArray,
        numFrames: Int,
        numChannels: Int,
        srcRate: Int,
        dstChannels: Int,
        dstRate: Int,
        dst: ShortArray,
    ): Int =
        src.withShortPtr { srcPtr ->
            dst.withShortPtr { dstPtr ->
                webrtc_audio_resample(
                    srcPtr,
                    numFrames,
                    numChannels,
                    srcRate,
                    dstChannels,
                    dstRate,
                    dstPtr,
                    dst.size,
                )
            }
        }

    actual fun audioMixFrames(
        src: ShortArray,
        dstAccum: ShortArray,
        totalSamples: Int,
    ) {
        src.withShortPtr { srcPtr ->
            dstAccum.withShortPtr { dstPtr ->
                webrtc_audio_mix_frames(srcPtr, dstPtr, totalSamples)
            }
        }
    }

    actual fun cryptorTransform(
        algorithm: Int,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray {
        val output = ByteArray(input.size)
        val written =
            key.withUBytePtr { keyPtr ->
                iv.withUBytePtr { ivPtr ->
                    input.withUBytePtr { inPtr ->
                        output.withUBytePtr { outPtr ->
                            webrtc_cryptor_transform(
                                algorithm,
                                keyPtr,
                                key.size,
                                ivPtr,
                                iv.size,
                                inPtr,
                                input.size,
                                outPtr,
                            )
                        }
                    }
                }
            }
        return if (written <= 0) ByteArray(0) else output
    }

    actual fun ratchetKey(
        currentKey: ByteArray,
        salt: ByteArray,
    ): ByteArray {
        val nextKey = ByteArray(currentKey.size)
        currentKey.withUBytePtr { keyPtr ->
            salt.withUBytePtr { saltPtr ->
                nextKey.withUBytePtr { nextPtr ->
                    webrtc_ratchet_key(keyPtr, currentKey.size, saltPtr, salt.size, nextPtr)
                }
            }
        }
        return nextKey
    }

    actual fun desktopCapturerSourceCount(sourceType: Int): Int = webrtc_desktop_capturer_source_count(sourceType)

    actual fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Long = webrtc_desktop_capturer_source_id(sourceType, index).toLong()

    actual fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String = webrtc_desktop_capturer_source_title(sourceType, index)?.toKString() ?: ""

    actual fun desktopCapturerCaptureRgba(
        sourceId: Long,
        width: Int,
        height: Int,
    ): ByteArray {
        val len = width * height * 4
        if (len <= 0) return ByteArray(0)
        val out = ByteArray(len)
        val written =
            out.withUBytePtr { outPtr ->
                webrtc_desktop_capturer_capture_rgba(sourceId.toULong(), width, height, outPtr, len)
            }
        return if (written <= 0) ByteArray(0) else out
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
        srcY.withUBytePtr { yPtr ->
            srcU.withUBytePtr { uPtr ->
                srcV.withUBytePtr { vPtr ->
                    dstArgb.withUBytePtr { dstPtr ->
                        webrtc_yuv_i420_to_argb(
                            yPtr,
                            srcStrideY,
                            uPtr,
                            srcStrideU,
                            vPtr,
                            srcStrideV,
                            dstPtr,
                            dstStrideArgb,
                            width,
                            height,
                        )
                    }
                }
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
        srcY.withUBytePtr { yPtr ->
            srcU.withUBytePtr { uPtr ->
                srcV.withUBytePtr { vPtr ->
                    dstBgra.withUBytePtr { dstPtr ->
                        webrtc_yuv_i420_to_bgra(
                            yPtr,
                            srcStrideY,
                            uPtr,
                            srcStrideU,
                            vPtr,
                            srcStrideV,
                            dstPtr,
                            dstStrideBgra,
                            width,
                            height,
                        )
                    }
                }
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
        srcY.withUBytePtr { yPtr ->
            srcU.withUBytePtr { uPtr ->
                srcV.withUBytePtr { vPtr ->
                    dstAbgr.withUBytePtr { dstPtr ->
                        webrtc_yuv_i420_to_abgr(
                            yPtr,
                            srcStrideY,
                            uPtr,
                            srcStrideU,
                            vPtr,
                            srcStrideV,
                            dstPtr,
                            dstStrideAbgr,
                            width,
                            height,
                        )
                    }
                }
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
        srcY.withUBytePtr { yPtr ->
            srcU.withUBytePtr { uPtr ->
                srcV.withUBytePtr { vPtr ->
                    dstRgba.withUBytePtr { dstPtr ->
                        webrtc_yuv_i420_to_rgba(
                            yPtr,
                            srcStrideY,
                            uPtr,
                            srcStrideU,
                            vPtr,
                            srcStrideV,
                            dstPtr,
                            dstStrideRgba,
                            width,
                            height,
                        )
                    }
                }
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
        srcArgb.withUBytePtr { srcPtr ->
            dstY.withUBytePtr { yPtr ->
                dstU.withUBytePtr { uPtr ->
                    dstV.withUBytePtr { vPtr ->
                        webrtc_yuv_argb_to_i420(
                            srcPtr,
                            srcStrideArgb,
                            yPtr,
                            dstStrideY,
                            uPtr,
                            dstStrideU,
                            vPtr,
                            dstStrideV,
                            width,
                            height,
                        )
                    }
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
        srcAbgr.withUBytePtr { srcPtr ->
            dstY.withUBytePtr { yPtr ->
                dstU.withUBytePtr { uPtr ->
                    dstV.withUBytePtr { vPtr ->
                        webrtc_yuv_abgr_to_i420(
                            srcPtr,
                            srcStrideAbgr,
                            yPtr,
                            dstStrideY,
                            uPtr,
                            dstStrideU,
                            vPtr,
                            dstStrideV,
                            width,
                            height,
                        )
                    }
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
        srcArgb.withUBytePtr { srcPtr ->
            dstRgb24.withUBytePtr { dstPtr ->
                webrtc_yuv_argb_to_rgb24(srcPtr, srcStrideArgb, dstPtr, dstStrideRgb24, width, height)
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
        srcY.withUBytePtr { syPtr ->
            srcU.withUBytePtr { suPtr ->
                srcV.withUBytePtr { svPtr ->
                    dstY.withUBytePtr { dyPtr ->
                        dstUv.withUBytePtr { duvPtr ->
                            webrtc_yuv_i420_to_nv12(
                                syPtr,
                                srcStrideY,
                                suPtr,
                                srcStrideU,
                                svPtr,
                                srcStrideV,
                                dyPtr,
                                dstStrideY,
                                duvPtr,
                                dstStrideUv,
                                width,
                                height,
                            )
                        }
                    }
                }
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
        srcY.withUBytePtr { syPtr ->
            srcUv.withUBytePtr { suvPtr ->
                dstY.withUBytePtr { dyPtr ->
                    dstU.withUBytePtr { duPtr ->
                        dstV.withUBytePtr { dvPtr ->
                            webrtc_yuv_nv12_to_i420(
                                syPtr,
                                srcStrideY,
                                suvPtr,
                                srcStrideUv,
                                dyPtr,
                                dstStrideY,
                                duPtr,
                                dstStrideU,
                                dvPtr,
                                dstStrideV,
                                width,
                                height,
                            )
                        }
                    }
                }
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
        srcY.withUBytePtr { syPtr ->
            srcUv.withUBytePtr { suvPtr ->
                dstArgb.withUBytePtr { dstPtr ->
                    webrtc_yuv_nv12_to_argb(
                        syPtr,
                        srcStrideY,
                        suvPtr,
                        srcStrideUv,
                        dstPtr,
                        dstStrideArgb,
                        width,
                        height,
                    )
                }
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
        srcY.withUBytePtr { syPtr ->
            srcUv.withUBytePtr { suvPtr ->
                dstAbgr.withUBytePtr { dstPtr ->
                    webrtc_yuv_nv12_to_abgr(
                        syPtr,
                        srcStrideY,
                        suvPtr,
                        srcStrideUv,
                        dstPtr,
                        dstStrideAbgr,
                        width,
                        height,
                    )
                }
            }
        }
    }
}
