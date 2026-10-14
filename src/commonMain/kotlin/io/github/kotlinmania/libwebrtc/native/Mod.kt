// Copyright 2025 LiveKit, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// port-lint: source native/mod.rs

package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.MediaType
import io.github.kotlinmania.libwebrtc.RtcError
import io.github.kotlinmania.libwebrtc.RtcErrorType

// Tracking file for upstream native/mod.rs.
//
// Upstream module declarations:
//   Android -> native/Android.kt
//   Apm -> native/Apm.kt
//   AudioMixer -> native/AudioMixer.kt
//   AudioResampler -> native/AudioResampler.kt
//   AudioSource -> native/AudioSource.kt
//   AudioStream -> native/AudioStream.kt
//   AudioTrack -> native/AudioTrack.kt
//   DataChannel -> native/DataChannel.kt
//   DesktopCapturer -> native/DesktopCapturer.kt
//   FrameCryptor -> native/FrameCryptor.kt
//   IceCandidate -> native/IceCandidate.kt
//   MediaStream -> native/MediaStream.kt
//   MediaStreamTrack -> native/MediaStreamTrack.kt
//   PeerConnection -> native/PeerConnection.kt
//   PeerConnectionFactory -> native/PeerConnectionFactory.kt
//   RtpParameters -> native/RtpParameters.kt
//   RtpReceiver -> native/RtpReceiver.kt
//   RtpSender -> native/RtpSender.kt
//   RtpTransceiver -> native/RtpTransceiver.kt
//   SessionDescription -> native/SessionDescription.kt
//   VideoFrame -> native/VideoFrame.kt
//   VideoSource -> native/VideoSource.kt
//   VideoStream -> native/VideoStream.kt
//   VideoTrack -> native/VideoTrack.kt
//   YuvHelper -> native/YuvHelper.kt
//
// Callers migrated:
//   (none)

internal expect val isWebPlatform: Boolean

internal expect val isNativePlatform: Boolean

internal expect val isAndroidPlatform: Boolean

internal expect object WebrtcNativeBridge {
    fun initAndroid(jvmHandle: Long)

    fun isAndroidInitialized(): Boolean

    fun createPeerConnectionFactory(): Long

    fun releasePeerConnectionFactory(factoryHandle: Long)

    fun createPeerConnection(
        factoryHandle: Long,
        iceTransportType: Int,
        continualGatheringPolicy: Int,
        iceServersCount: Int,
    ): Long

    fun closePeerConnection(pcHandle: Long)

    fun pcConnectionState(pcHandle: Long): Int

    fun pcIceConnectionState(pcHandle: Long): Int

    fun pcIceGatheringState(pcHandle: Long): Int

    fun pcSignalingState(pcHandle: Long): Int

    fun pcCreateOfferSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String

    fun pcCreateAnswerSdp(
        pcHandle: Long,
        iceRestart: Boolean,
        offerAudio: Boolean,
        offerVideo: Boolean,
    ): String

    fun pcSetLocalDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean

    fun pcSetRemoteDescription(
        pcHandle: Long,
        sdpType: Int,
        sdp: String,
    ): Boolean

    fun pcRemotePeerHandle(pcHandle: Long): Long

    fun pcAddIceCandidate(
        pcHandle: Long,
        sdpMid: String,
        sdpMlineIndex: Int,
        candidate: String,
    ): Boolean

    fun pcRestartIce(pcHandle: Long)

    fun pcGetStatsJson(pcHandle: Long): String

    fun createDataChannel(
        pcHandle: Long,
        label: String,
        id: Int,
        ordered: Boolean,
        negotiated: Boolean,
    ): Long

    fun dcState(dcHandle: Long): Int

    fun dcBufferedAmount(dcHandle: Long): Long

    fun dcSend(
        dcHandle: Long,
        data: ByteArray,
        binary: Boolean,
    ): Boolean

    fun dcClose(dcHandle: Long)

    fun createAudioSource(
        sampleRate: Int,
        numChannels: Int,
        echoCancellation: Boolean,
        noiseSuppression: Boolean,
        autoGainControl: Boolean,
    ): Long

    fun audioSourceCaptureFrame(
        sourceHandle: Long,
        samples: ShortArray,
        sampleRate: Int,
        numChannels: Int,
        samplesPerChannel: Int,
    ): Boolean

    fun createVideoSource(
        width: Int,
        height: Int,
        isScreencast: Boolean,
    ): Long

    fun videoSourceCaptureFrame(
        sourceHandle: Long,
        width: Int,
        height: Int,
        timestampUs: Long,
        rotation: Int,
    )

    fun createAudioTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long

    fun createVideoTrack(
        factoryHandle: Long,
        id: String,
        sourceHandle: Long,
    ): Long

    fun trackEnabled(trackHandle: Long): Boolean

    fun trackSetEnabled(
        trackHandle: Long,
        enabled: Boolean,
    ): Boolean

    fun trackState(trackHandle: Long): Int

    fun trackSetState(
        trackHandle: Long,
        state: Int,
    )

    fun createRtpSender(
        pcHandle: Long,
        trackHandle: Long,
    ): Long

    fun rtpSenderGetStatsJson(senderHandle: Long): String

    fun createRtpReceiver(
        pcHandle: Long,
        trackHandle: Long,
    ): Long

    fun rtpReceiverGetStatsJson(receiverHandle: Long): String

    fun createRtpTransceiver(
        pcHandle: Long,
        mediaType: Int,
        direction: Int,
    ): Long

    fun rtpTransceiverDirection(transceiverHandle: Long): Int

    fun rtpTransceiverSetDirection(
        transceiverHandle: Long,
        direction: Int,
    )

    fun rtpTransceiverStop(transceiverHandle: Long)

    fun apmCreate(
        echoCanceller: Boolean,
        gainController: Boolean,
        highPassFilter: Boolean,
        noiseSuppression: Boolean,
    ): Long

    fun apmProcessStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int

    fun apmProcessReverseStream(
        apmHandle: Long,
        data: ShortArray,
        sampleRate: Int,
        numChannels: Int,
    ): Int

    fun apmSetStreamDelayMs(
        apmHandle: Long,
        delayMs: Int,
    ): Int

    fun audioResample(
        src: ShortArray,
        numFrames: Int,
        numChannels: Int,
        srcRate: Int,
        dstChannels: Int,
        dstRate: Int,
        dst: ShortArray,
    ): Int

    fun audioMixFrames(
        src: ShortArray,
        dstAccum: ShortArray,
        totalSamples: Int,
    )

    fun cryptorTransform(
        algorithm: Int,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray

    fun ratchetKey(
        currentKey: ByteArray,
        salt: ByteArray,
    ): ByteArray

    fun desktopCapturerSourceCount(sourceType: Int): Int

    fun desktopCapturerSourceId(
        sourceType: Int,
        index: Int,
    ): Long

    fun desktopCapturerSourceTitle(
        sourceType: Int,
        index: Int,
    ): String

    fun desktopCapturerCaptureRgba(
        sourceId: Long,
        width: Int,
        height: Int,
    ): ByteArray

    fun yuvI420ToArgb(
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

    fun yuvI420ToBgra(
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

    fun yuvI420ToAbgr(
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

    fun yuvI420ToRgba(
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

    fun yuvArgbToI420(
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

    fun yuvAbgrToI420(
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

    fun yuvArgbToRgb24(
        srcArgb: ByteArray,
        srcStrideArgb: Int,
        dstRgb24: ByteArray,
        dstStrideRgb24: Int,
        width: Int,
        height: Int,
    )

    fun yuvI420ToNv12(
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

    fun yuvNv12ToI420(
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

    fun yuvNv12ToArgb(
        srcY: ByteArray,
        srcStrideY: Int,
        srcUv: ByteArray,
        srcStrideUv: Int,
        dstArgb: ByteArray,
        dstStrideArgb: Int,
        width: Int,
        height: Int,
    )

    fun yuvNv12ToAbgr(
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

internal enum class NativeRtcErrorType {
    Internal,
    InvalidSdp,
    InvalidState,
    ;

    internal companion object {
        internal fun from(value: NativeRtcErrorType): RtcErrorType =
            when (value) {
                InvalidState -> RtcErrorType.InvalidState
                else -> RtcErrorType.Internal
            }
    }
}

internal class NativeRtcError(
    internal val errorType: NativeRtcErrorType,
    internal val message: String,
) {
    internal companion object {
        internal fun from(value: NativeRtcError): RtcError =
            RtcError(
                errorType = NativeRtcErrorType.from(value.errorType),
                rtcMessage = value.message,
            )
    }
}

internal enum class NativeMediaType {
    Audio,
    Video,
    Data,
    Unsupported,
    ;

    internal companion object {
        internal fun from(value: MediaType): NativeMediaType =
            when (value) {
                MediaType.Audio -> Audio
                MediaType.Video -> Video
                MediaType.Data -> Data
                MediaType.Unsupported -> Unsupported
            }
    }
}
