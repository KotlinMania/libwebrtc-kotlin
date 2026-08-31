// port-lint: source peer_connection.rs
package io.github.kotlinmania.libwebrtc

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
    HaveLocalPrAnswer,
    HaveRemoteOffer,
    HaveRemotePrAnswer,
    Closed,
}

public data class OfferOptions(
    val iceRestart: Boolean = false,
    val offerToReceiveAudio: Boolean = true,
    val offerToReceiveVideo: Boolean = true,
)

public class AnswerOptions

public data class IceCandidateError(
    val address: String,
    val port: Int,
    val url: String,
    val errorCode: Int,
    val errorText: String,
)

public data class TrackEvent(
    val receiver: RtpReceiver,
    val streams: List<MediaStream>,
    val track: MediaStreamTrack,
    val transceiver: RtpTransceiver,
)

public interface PeerConnectionObserver {
    public fun onConnectionChange(state: PeerConnectionState) {
        state.hashCode()
    }

    public fun onDataChannel(dataChannel: DataChannel) {
        dataChannel.hashCode()
    }

    public fun onIceCandidate(candidate: IceCandidate) {
        candidate.hashCode()
    }

    public fun onIceCandidateError(error: IceCandidateError) {
        error.hashCode()
    }

    public fun onIceConnectionChange(state: IceConnectionState) {
        state.hashCode()
    }

    public fun onIceGatheringChange(state: IceGatheringState) {
        state.hashCode()
    }

    public fun onNegotiationNeeded() {}

    public fun onSignalingChange(state: SignalingState) {
        state.hashCode()
    }

    public fun onTrack(event: TrackEvent) {
        event.hashCode()
    }
}

public class PeerConnection(
    public val configuration: RtcConfiguration = RtcConfiguration(),
    public var observer: PeerConnectionObserver? = null,
) {
    private var connectionState: PeerConnectionState = PeerConnectionState.New
    private var iceState: IceConnectionState = IceConnectionState.New
    private var gatheringState: IceGatheringState = IceGatheringState.New
    private var signalState: SignalingState = SignalingState.Stable
    private var localDesc: SessionDescription? = null
    private var remoteDesc: SessionDescription? = null

    private val sendersList = mutableListOf<RtpSender>()
    private val receiversList = mutableListOf<RtpReceiver>()
    private val transceiversList = mutableListOf<RtpTransceiver>()

    public fun connectionState(): PeerConnectionState = connectionState

    public fun iceConnectionState(): IceConnectionState = iceState

    public fun iceGatheringState(): IceGatheringState = gatheringState

    public fun signalingState(): SignalingState = signalState

    public fun localDescription(): SessionDescription? = localDesc

    public fun remoteDescription(): SessionDescription? = remoteDesc

    public fun setLocalDescription(desc: SessionDescription) {
        localDesc = desc
        signalState =
            when (desc.sdpType) {
                SdpType.Offer -> SignalingState.HaveLocalOffer
                SdpType.PrAnswer -> SignalingState.HaveLocalPrAnswer
                SdpType.Answer -> SignalingState.Stable
                SdpType.Rollback -> SignalingState.Stable
            }
        observer?.onSignalingChange(signalState)
    }

    public fun setRemoteDescription(desc: SessionDescription) {
        remoteDesc = desc
        signalState =
            when (desc.sdpType) {
                SdpType.Offer -> SignalingState.HaveRemoteOffer
                SdpType.PrAnswer -> SignalingState.HaveRemotePrAnswer
                SdpType.Answer -> SignalingState.Stable
                SdpType.Rollback -> SignalingState.Stable
            }
        observer?.onSignalingChange(signalState)
    }

    public fun addIceCandidate(candidate: IceCandidate) {
        observer?.onIceCandidate(candidate)
    }

    public fun addTrack(track: MediaStreamTrack, streamIds: List<String> = emptyList()): RtpSender {
        val sender = RtpSender(track)
        sendersList.add(sender)
        return sender
    }

    public fun removeTrack(sender: RtpSender) {
        sendersList.remove(sender)
    }

    public fun senders(): List<RtpSender> = sendersList.toList()

    public fun receivers(): List<RtpReceiver> = receiversList.toList()

    public fun transceivers(): List<RtpTransceiver> = transceiversList.toList()

    public fun createDataChannel(label: String, init: DataChannelInit = DataChannelInit()): DataChannel = DataChannel(label, init)

    public fun close() {
        connectionState = PeerConnectionState.Closed
        iceState = IceConnectionState.Closed
        signalState = SignalingState.Closed
        observer?.onConnectionChange(connectionState)
    }
}
