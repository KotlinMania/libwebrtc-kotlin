// port-lint: source peer_connection.rs
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

package io.github.kotlinmania.libwebrtc

import io.github.kotlinmania.libwebrtc.native.PeerConnection as ImpPeerConnection
import io.github.kotlinmania.libwebrtc.native.PeerObserver as ImpPeerObserver

public fun interface OnConnectionChange {
    public fun onConnectionChange(state: PeerConnectionState)
}

public fun interface OnDataChannel {
    public fun onDataChannel(dataChannel: DataChannel)
}

public fun interface OnIceCandidate {
    public fun onIceCandidate(candidate: IceCandidate)
}

public fun interface OnIceCandidateError {
    public fun onIceCandidateError(error: IceCandidateError)
}

public fun interface OnIceConnectionChange {
    public fun onIceConnectionChange(state: IceConnectionState)
}

public fun interface OnIceGatheringChange {
    public fun onIceGatheringChange(state: IceGatheringState)
}

public fun interface OnNegotiationNeeded {
    public fun onNegotiationNeeded(event: UInt)
}

public fun interface OnSignalingChange {
    public fun onSignalingChange(state: SignalingState)
}

public fun interface OnTrack {
    public fun onTrack(event: TrackEvent)
}

public enum class PeerConnectionState {
    New,
    Connecting,
    Connected,
    Disconnected,
    Failed,
    Closed,
}

public enum class IceConnectionState {
    New,
    Checking,
    Connected,
    Completed,
    Failed,
    Disconnected,
    Closed,
    Max,
}

public enum class IceGatheringState {
    New,
    Gathering,
    Complete,
}

public enum class SignalingState {
    Stable,
    HaveLocalOffer,
    HaveRemoteOffer,
    HaveLocalPrAnswer,
    HaveRemotePrAnswer,
    Closed,
}

public class OfferOptions(
    public val iceRestart: Boolean = false,
    public val offerToReceiveAudio: Boolean = true,
    public val offerToReceiveVideo: Boolean = true,
) {
    public companion object {
        public fun default(): OfferOptions = OfferOptions()
    }
}

public class AnswerOptions {
    override fun equals(other: Any?): Boolean = other is AnswerOptions

    override fun hashCode(): Int = 0

    override fun toString(): String = "AnswerOptions"

    public companion object {
        public fun default(): AnswerOptions = AnswerOptions()
    }
}

public class IceCandidateError(
    public val address: String,
    public val port: Int,
    public val url: String,
    public val errorCode: Int,
    public val errorText: String,
)

public class TrackEvent(
    public val receiver: RtpReceiver,
    public val streams: List<MediaStream>,
    public val track: MediaStreamTrack,
    public val transceiver: RtpTransceiver,
)

public class PeerConnection internal constructor(
    internal val handle: ImpPeerConnection,
) {
    public constructor(config: RtcConfiguration = RtcConfiguration()) : this(
        handle = ImpPeerConnection.configure(config = config, observer = ImpPeerObserver()),
    )

    public fun setConfiguration(config: RtcConfiguration) {
        handle.setConfiguration(config)
    }

    public fun createOffer(options: OfferOptions = OfferOptions()): SessionDescription =
        handle.createOffer(options)

    public fun createAnswer(options: AnswerOptions = AnswerOptions()): SessionDescription =
        handle.createAnswer(options)

    public fun setLocalDescription(desc: SessionDescription) {
        handle.setLocalDescription(desc)
    }

    public fun setRemoteDescription(desc: SessionDescription) {
        handle.setRemoteDescription(desc)
    }

    public fun addIceCandidate(candidate: IceCandidate) {
        handle.addIceCandidate(candidate)
    }

    public fun createDataChannel(
        label: String,
        init: DataChannelInit = DataChannelInit(),
    ): DataChannel = handle.createDataChannel(label, init)

    public fun addTrack(
        track: MediaStreamTrack,
        streamIds: List<String> = emptyList(),
    ): RtpSender = handle.addTrack(track, streamIds)

    public fun addTrack(
        track: RtcAudioTrack,
        streamIds: List<String> = emptyList(),
    ): RtpSender = handle.addTrack(MediaStreamTrack.Audio(track), streamIds)

    public fun addTrack(
        track: RtcVideoTrack,
        streamIds: List<String> = emptyList(),
    ): RtpSender = handle.addTrack(MediaStreamTrack.Video(track), streamIds)

    public fun addTransceiver(
        track: MediaStreamTrack,
        init: RtpTransceiverInit = RtpTransceiverInit(),
    ): RtpTransceiver = handle.addTransceiver(track, init)

    public fun addTransceiver(
        track: RtcAudioTrack,
        init: RtpTransceiverInit = RtpTransceiverInit(),
    ): RtpTransceiver = handle.addTransceiver(MediaStreamTrack.Audio(track), init)

    public fun addTransceiver(
        track: RtcVideoTrack,
        init: RtpTransceiverInit = RtpTransceiverInit(),
    ): RtpTransceiver = handle.addTransceiver(MediaStreamTrack.Video(track), init)

    public fun addTransceiverForMedia(
        mediaType: MediaType,
        init: RtpTransceiverInit = RtpTransceiverInit(),
    ): RtpTransceiver = handle.addTransceiverForMedia(mediaType, init)

    public fun restartIce() {
        handle.restartIce()
    }

    public fun close() {
        handle.close()
    }

    public fun connectionState(): PeerConnectionState = handle.connectionState()

    public fun iceConnectionState(): IceConnectionState = handle.iceConnectionState()

    public fun iceGatheringState(): IceGatheringState = handle.iceGatheringState()

    public fun signalingState(): SignalingState = handle.signalingState()

    public fun currentLocalDescription(): SessionDescription? = handle.currentLocalDescription()

    public fun currentRemoteDescription(): SessionDescription? = handle.currentRemoteDescription()

    public fun removeTrack(sender: RtpSender) {
        handle.removeTrack(sender)
    }

    public fun getStats(): List<RtcStats> = handle.getStats()

    public fun senders(): List<RtpSender> = handle.senders()

    public fun receivers(): List<RtpReceiver> = handle.receivers()

    public fun transceivers(): List<RtpTransceiver> = handle.transceivers()

    public fun onConnectionStateChange(handler: OnConnectionChange?) {
        handle.onConnectionStateChange(handler)
    }

    public fun onDataChannel(handler: OnDataChannel?) {
        handle.onDataChannel(handler)
    }

    public fun onIceCandidate(handler: OnIceCandidate?) {
        handle.onIceCandidate(handler)
    }

    public fun onIceCandidateError(handler: OnIceCandidateError?) {
        handle.onIceCandidateError(handler)
    }

    public fun onIceConnectionStateChange(handler: OnIceConnectionChange?) {
        handle.onIceConnectionStateChange(handler)
    }

    public fun onIceGatheringStateChange(handler: OnIceGatheringChange?) {
        handle.onIceGatheringStateChange(handler)
    }

    public fun onNegotiationNeeded(handler: OnNegotiationNeeded?) {
        handle.onNegotiationNeeded(handler)
    }

    public fun onSignalingStateChange(handler: OnSignalingChange?) {
        handle.onSignalingStateChange(handler)
    }

    public fun onTrack(handler: OnTrack?) {
        handle.onTrack(handler)
    }

    override fun toString(): String =
        "PeerConnection(connection_state=${connectionState()}, ice_connection_state=${iceConnectionState()}, ice_gathering_state=${iceGatheringState()}, signaling_state=${signalingState()})"
}
