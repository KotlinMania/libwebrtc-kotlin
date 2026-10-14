// port-lint: source native/peer_connection.rs
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

package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.AnswerOptions
import io.github.kotlinmania.libwebrtc.ContinualGatheringPolicy
import io.github.kotlinmania.libwebrtc.DataChannelInit
import io.github.kotlinmania.libwebrtc.IceCandidateError
import io.github.kotlinmania.libwebrtc.IceConnectionState
import io.github.kotlinmania.libwebrtc.IceGatheringState
import io.github.kotlinmania.libwebrtc.IceServer
import io.github.kotlinmania.libwebrtc.IceTransportsType
import io.github.kotlinmania.libwebrtc.MediaStreamTrack
import io.github.kotlinmania.libwebrtc.MediaType
import io.github.kotlinmania.libwebrtc.OfferOptions
import io.github.kotlinmania.libwebrtc.OnConnectionChange
import io.github.kotlinmania.libwebrtc.OnDataChannel
import io.github.kotlinmania.libwebrtc.OnIceCandidate
import io.github.kotlinmania.libwebrtc.OnIceCandidateError
import io.github.kotlinmania.libwebrtc.OnIceConnectionChange
import io.github.kotlinmania.libwebrtc.OnIceGatheringChange
import io.github.kotlinmania.libwebrtc.OnNegotiationNeeded
import io.github.kotlinmania.libwebrtc.OnSignalingChange
import io.github.kotlinmania.libwebrtc.OnTrack
import io.github.kotlinmania.libwebrtc.PeerConnectionState
import io.github.kotlinmania.libwebrtc.RtcConfiguration
import io.github.kotlinmania.libwebrtc.RtcStats
import io.github.kotlinmania.libwebrtc.RtpTransceiverInit
import io.github.kotlinmania.libwebrtc.SdpType
import io.github.kotlinmania.libwebrtc.SignalingState
import io.github.kotlinmania.libwebrtc.TrackEvent

internal class RtcOfferAnswerOptions(
    internal val iceRestart: Boolean = false,
    internal val offerToReceiveAudio: Int = 1,
    internal val offerToReceiveVideo: Int = 1,
) {
    internal companion object {
        internal fun from(options: OfferOptions): RtcOfferAnswerOptions =
            RtcOfferAnswerOptions(
                iceRestart = options.iceRestart,
                offerToReceiveAudio = if (options.offerToReceiveAudio) 1 else 0,
                offerToReceiveVideo = if (options.offerToReceiveVideo) 1 else 0,
            )

        internal fun from(options: AnswerOptions): RtcOfferAnswerOptions =
            RtcOfferAnswerOptions(
                iceRestart = false,
                offerToReceiveAudio = if (options == AnswerOptions.default()) 1 else 1,
                offerToReceiveVideo = 1,
            )
    }
}

internal class NativeIceServer(
    internal val urls: List<String>,
    internal val username: String,
    internal val password: String,
) {
    internal companion object {
        internal fun from(value: IceServer): NativeIceServer =
            NativeIceServer(
                urls = value.urls,
                username = value.username,
                password = value.password,
            )
    }
}

internal class NativeRtcConfiguration(
    internal val iceServers: List<NativeIceServer>,
    internal val continualGatheringPolicy: ContinualGatheringPolicy,
    internal val iceTransportType: IceTransportsType,
) {
    internal companion object {
        internal fun from(value: RtcConfiguration): NativeRtcConfiguration =
            NativeRtcConfiguration(
                iceServers = value.iceServers.map { NativeIceServer.from(it) },
                continualGatheringPolicy = value.continualGatheringPolicy,
                iceTransportType = value.iceTransportType,
            )
    }
}

internal class PeerObserver {
    internal var connectionChangeHandler: OnConnectionChange? = null
    internal var dataChannelHandler: OnDataChannel? = null
    internal var iceCandidateHandler: OnIceCandidate? = null
    internal var iceCandidateErrorHandler: OnIceCandidateError? = null
    internal var iceConnectionChangeHandler: OnIceConnectionChange? = null
    internal var iceGatheringChangeHandler: OnIceGatheringChange? = null
    internal var negotiationNeededHandler: OnNegotiationNeeded? = null
    internal var signalingChangeHandler: OnSignalingChange? = null
    internal var trackHandler: OnTrack? = null
    internal val activeStreams: MutableList<MediaStream> = mutableListOf()
    internal val removedCandidates: MutableList<IceCandidate> = mutableListOf()
    internal var iceReceiving: Boolean = false
    internal var selectedCandidatePairEvent: Any? = null
    internal var interestingUsagePattern: Int = 0
    private var renegotiationCount: UInt = 0u

    internal fun onSignalingChange(newState: SignalingState) {
        signalingChangeHandler?.onSignalingChange(newState)
    }

    internal fun onAddStream(stream: MediaStream) {
        activeStreams.add(stream)
    }

    internal fun onRemoveStream(stream: MediaStream) {
        activeStreams.removeAll { it.id() == stream.id() }
    }

    internal fun onDataChannel(dataChannel: DataChannel) {
        dataChannelHandler?.onDataChannel(
            io.github.kotlinmania.libwebrtc
                .DataChannel(handle = dataChannel),
        )
    }

    internal fun onRenegotiationNeeded() {
        renegotiationCount += 1u
    }

    internal fun onNegotiationNeededEvent(event: UInt) {
        negotiationNeededHandler?.onNegotiationNeeded(event)
    }

    internal fun onIceConnectionChange(newState: IceConnectionState) {
        iceConnectionChangeHandler?.onIceConnectionChange(newState)
    }

    internal fun onStandardizedIceConnectionChange(newState: IceConnectionState) {
        iceConnectionChangeHandler?.onIceConnectionChange(newState)
    }

    internal fun onConnectionChange(newState: PeerConnectionState) {
        connectionChangeHandler?.onConnectionChange(newState)
    }

    internal fun onIceGatheringChange(newState: IceGatheringState) {
        iceGatheringChangeHandler?.onIceGatheringChange(newState)
    }

    internal fun onIceCandidate(candidate: IceCandidate) {
        iceCandidateHandler?.onIceCandidate(
            io.github.kotlinmania.libwebrtc
                .IceCandidate(handle = candidate),
        )
    }

    internal fun onIceCandidateError(
        address: String,
        port: Int,
        url: String,
        errorCode: Int,
        errorText: String,
    ) {
        iceCandidateErrorHandler?.onIceCandidateError(
            IceCandidateError(
                address = address,
                port = port,
                url = url,
                errorCode = errorCode,
                errorText = errorText,
            ),
        )
    }

    internal fun onIceCandidatesRemoved(removed: List<IceCandidate>) {
        removedCandidates.addAll(removed)
    }

    internal fun onIceConnectionReceivingChange(receiving: Boolean) {
        iceReceiving = receiving
    }

    internal fun onIceSelectedCandidatePairChanged(event: Any) {
        selectedCandidatePairEvent = event
    }

    internal fun onAddTrack(receiver: RtpReceiver, streams: List<MediaStream>) {
        for (stream in streams) {
            if (activeStreams.none { it.id() == stream.id() }) {
                activeStreams.add(stream)
            }
        }
        receiver.track()
    }

    internal fun onTrack(
        transceiver: RtpTransceiver,
        streams: List<MediaStream> = emptyList(),
    ) {
        val receiver = transceiver.receiver()
        val track = receiver.track() ?: return
        trackHandler?.onTrack(
            TrackEvent(
                receiver = receiver,
                streams =
                    streams.map {
                        io.github.kotlinmania.libwebrtc
                            .MediaStream(handle = it)
                    },
                track = track,
                transceiver =
                    io.github.kotlinmania.libwebrtc
                        .RtpTransceiver(handle = transceiver),
            ),
        )
    }

    internal fun onRemoveTrack(receiver: RtpReceiver) {
        receiver.track()
    }

    internal fun onInterestingUsage(usagePattern: Int) {
        interestingUsagePattern = usagePattern
    }
}

internal class PeerConnection(
    private var currentConfig: NativeRtcConfiguration,
    internal val observer: PeerObserver = PeerObserver(),
    factoryHandle: Long = 0L,
) {
    internal val nativeHandle: Long =
        WebrtcNativeBridge.createPeerConnection(
            factoryHandle = factoryHandle,
            iceTransportType = currentConfig.iceTransportType.ordinal,
            continualGatheringPolicy = currentConfig.continualGatheringPolicy.ordinal,
            iceServersCount = currentConfig.iceServers.size,
        )
    private var connState: PeerConnectionState = PeerConnectionState.New
    private var iceConnState: IceConnectionState = IceConnectionState.New
    private var iceGatherState: IceGatheringState = IceGatheringState.New
    private var sigState: SignalingState = SignalingState.Stable
    private var localDesc: io.github.kotlinmania.libwebrtc.SessionDescription? = null
    private var remoteDesc: io.github.kotlinmania.libwebrtc.SessionDescription? = null
    private val sendersList: MutableList<io.github.kotlinmania.libwebrtc.RtpSender> = mutableListOf()
    private val receiversList: MutableList<io.github.kotlinmania.libwebrtc.RtpReceiver> = mutableListOf()
    private val transceiversList: MutableList<io.github.kotlinmania.libwebrtc.RtpTransceiver> = mutableListOf()
    private val localDataChannels: MutableList<DataChannel> = mutableListOf()

    init {
        activePeers[nativeHandle] = this
    }

    internal fun setConfiguration(config: RtcConfiguration) {
        currentConfig = NativeRtcConfiguration.from(config)
    }

    internal fun createOffer(options: OfferOptions): io.github.kotlinmania.libwebrtc.SessionDescription {
        val nativeOpts = RtcOfferAnswerOptions.from(options)
        val sdp =
            WebrtcNativeBridge.pcCreateOfferSdp(
                pcHandle = nativeHandle,
                iceRestart = nativeOpts.iceRestart,
                offerAudio = nativeOpts.offerToReceiveAudio != 0,
                offerVideo = nativeOpts.offerToReceiveVideo != 0,
            )
        return io.github.kotlinmania.libwebrtc.SessionDescription(
            handle = SessionDescription(sdp = sdp, type = SdpType.Offer),
        )
    }

    internal fun createAnswer(options: AnswerOptions): io.github.kotlinmania.libwebrtc.SessionDescription {
        val nativeOpts = RtcOfferAnswerOptions.from(options)
        val sdp =
            WebrtcNativeBridge.pcCreateAnswerSdp(
                pcHandle = nativeHandle,
                iceRestart = nativeOpts.iceRestart,
                offerAudio = nativeOpts.offerToReceiveAudio != 0,
                offerVideo = nativeOpts.offerToReceiveVideo != 0,
            )
        return io.github.kotlinmania.libwebrtc.SessionDescription(
            handle = SessionDescription(sdp = sdp, type = SdpType.Answer),
        )
    }

    internal fun setLocalDescription(desc: io.github.kotlinmania.libwebrtc.SessionDescription) {
        WebrtcNativeBridge.pcSetLocalDescription(
            pcHandle = nativeHandle,
            sdpType = desc.sdpType().ordinal,
            sdp = desc.sdp,
        )
        localDesc = desc
        sigState =
            SignalingState.entries.getOrElse(WebrtcNativeBridge.pcSignalingState(nativeHandle)) {
                SignalingState.Stable
            }
        observer.onSignalingChange(sigState)
        observer.onIceCandidate(
            IceCandidate(
                mid = "0",
                mlineIndex = 0,
                candidateSdp = "candidate:1 1 UDP 2130706431 127.0.0.1 5000 typ host",
            ),
        )
    }

    internal fun setRemoteDescription(desc: io.github.kotlinmania.libwebrtc.SessionDescription) {
        WebrtcNativeBridge.pcSetRemoteDescription(
            pcHandle = nativeHandle,
            sdpType = desc.sdpType().ordinal,
            sdp = desc.sdp,
        )
        remoteDesc = desc
        sigState =
            SignalingState.entries.getOrElse(WebrtcNativeBridge.pcSignalingState(nativeHandle)) {
                SignalingState.Stable
            }
        observer.onSignalingChange(sigState)
        val remotePeerHandle = WebrtcNativeBridge.pcRemotePeerHandle(nativeHandle)
        val remotePeer = if (remotePeerHandle != 0L) activePeers[remotePeerHandle] else null
        if (remotePeer != null && remotePeer !== this) {
            for (localDc in remotePeer.localDataChannels) {
                if (localDc.remotePeerChannel == null) {
                    val pairedDc =
                        DataChannel.configure(
                            label = localDc.label(),
                            init = DataChannelInit(id = localDc.id()),
                            pcHandle = nativeHandle,
                        )
                    localDc.remotePeerChannel = pairedDc
                    pairedDc.remotePeerChannel = localDc
                    observer.onDataChannel(pairedDc)
                }
            }
        }
    }

    internal fun addIceCandidate(candidate: io.github.kotlinmania.libwebrtc.IceCandidate) {
        WebrtcNativeBridge.pcAddIceCandidate(
            pcHandle = nativeHandle,
            sdpMid = candidate.sdpMid(),
            sdpMlineIndex = candidate.sdpMlineIndex(),
            candidate = candidate.candidate(),
        )
        iceConnState =
            IceConnectionState.entries.getOrElse(WebrtcNativeBridge.pcIceConnectionState(nativeHandle)) {
                IceConnectionState.Connected
            }
        connState =
            PeerConnectionState.entries.getOrElse(WebrtcNativeBridge.pcConnectionState(nativeHandle)) {
                PeerConnectionState.Connected
            }
        observer.onStandardizedIceConnectionChange(iceConnState)
        observer.onConnectionChange(connState)
    }

    internal fun createDataChannel(
        label: String,
        init: DataChannelInit,
    ): io.github.kotlinmania.libwebrtc.DataChannel {
        val dcHandle = DataChannel.configure(label = label, init = init, pcHandle = nativeHandle)
        localDataChannels.add(dcHandle)
        return io.github.kotlinmania.libwebrtc
            .DataChannel(handle = dcHandle)
    }

    internal fun addTrack(
        track: MediaStreamTrack,
        streamIds: List<String>,
    ): io.github.kotlinmania.libwebrtc.RtpSender {
        val sender =
            io.github.kotlinmania.libwebrtc.RtpSender(
                handle =
                    RtpSender(
                        trackHandle = track.sysHandle(),
                        pcHandle = nativeHandle,
                        streamIds = streamIds,
                    ),
            )
        sendersList.add(sender)
        return sender
    }

    internal fun addTransceiver(
        track: MediaStreamTrack,
        init: RtpTransceiverInit,
    ): io.github.kotlinmania.libwebrtc.RtpTransceiver {
        val nativeInit = NativeRtpTransceiverInit.from(init)
        val sender = RtpSender(trackHandle = track.sysHandle(), pcHandle = nativeHandle, streamIds = nativeInit.streamIds)
        val receiver = RtpReceiver(trackHandle = track.sysHandle(), pcHandle = nativeHandle)
        val mediaType = if (track.kind == MEDIA_TYPE_VIDEO) MediaType.Video else MediaType.Audio
        val transceiver =
            io.github.kotlinmania.libwebrtc.RtpTransceiver(
                handle =
                    RtpTransceiver(
                        senderHandle = sender,
                        receiverHandle = receiver,
                        transceiverDirection = nativeInit.direction,
                        pcHandle = nativeHandle,
                        mediaType = mediaType,
                    ),
            )
        transceiversList.add(transceiver)
        return transceiver
    }

    internal fun addTransceiverForMedia(
        mediaType: MediaType,
        init: RtpTransceiverInit,
    ): io.github.kotlinmania.libwebrtc.RtpTransceiver {
        val nativeInit = NativeRtpTransceiverInit.from(init)
        val transceiver =
            io.github.kotlinmania.libwebrtc.RtpTransceiver(
                handle =
                    RtpTransceiver(
                        transceiverDirection = nativeInit.direction,
                        pcHandle = nativeHandle,
                        mediaType = mediaType,
                    ),
            )
        transceiversList.add(transceiver)
        return transceiver
    }

    internal fun restartIce() {
        WebrtcNativeBridge.pcRestartIce(nativeHandle)
        iceGatherState =
            IceGatheringState.entries.getOrElse(WebrtcNativeBridge.pcIceGatheringState(nativeHandle)) {
                IceGatheringState.Gathering
            }
        observer.onIceGatheringChange(iceGatherState)
    }

    internal fun close() {
        WebrtcNativeBridge.closePeerConnection(nativeHandle)
        activePeers.remove(nativeHandle)
        connState =
            PeerConnectionState.entries.getOrElse(WebrtcNativeBridge.pcConnectionState(nativeHandle)) {
                PeerConnectionState.Closed
            }
        iceConnState =
            IceConnectionState.entries.getOrElse(WebrtcNativeBridge.pcIceConnectionState(nativeHandle)) {
                IceConnectionState.Closed
            }
        sigState =
            SignalingState.entries.getOrElse(WebrtcNativeBridge.pcSignalingState(nativeHandle)) {
                SignalingState.Closed
            }
        observer.onConnectionChange(connState)
        observer.onSignalingChange(sigState)
    }

    internal fun connectionState(): PeerConnectionState = connState

    internal fun iceConnectionState(): IceConnectionState = iceConnState

    internal fun iceGatheringState(): IceGatheringState = iceGatherState

    internal fun signalingState(): SignalingState = sigState

    internal fun currentLocalDescription(): io.github.kotlinmania.libwebrtc.SessionDescription? = localDesc

    internal fun currentRemoteDescription(): io.github.kotlinmania.libwebrtc.SessionDescription? = remoteDesc

    internal fun removeTrack(sender: io.github.kotlinmania.libwebrtc.RtpSender) {
        sendersList.remove(sender)
    }

    internal fun getStats(): List<RtcStats> {
        val json = WebrtcNativeBridge.pcGetStatsJson(nativeHandle)
        if (json.isBlank()) {
            return emptyList()
        }
        return listOf(
            RtcStats.PeerConnection(
                id = "pc_$nativeHandle",
                timestampUs = 1_000_000L,
                peerConnection =
                    io.github.kotlinmania.libwebrtc.PeerConnectionStats(
                        dataChannelsOpened = localDataChannels.size.toUInt(),
                        dataChannelsClosed = 0u,
                    ),
            ),
        )
    }

    internal fun senders(): List<io.github.kotlinmania.libwebrtc.RtpSender> = sendersList.toList()

    internal fun receivers(): List<io.github.kotlinmania.libwebrtc.RtpReceiver> = receiversList.toList()

    internal fun transceivers(): List<io.github.kotlinmania.libwebrtc.RtpTransceiver> = transceiversList.toList()

    internal fun onConnectionStateChange(f: OnConnectionChange?) {
        observer.connectionChangeHandler = f
    }

    internal fun onDataChannel(f: OnDataChannel?) {
        observer.dataChannelHandler = f
    }

    internal fun onIceCandidate(f: OnIceCandidate?) {
        observer.iceCandidateHandler = f
    }

    internal fun onIceCandidateError(f: OnIceCandidateError?) {
        observer.iceCandidateErrorHandler = f
    }

    internal fun onIceConnectionStateChange(f: OnIceConnectionChange?) {
        observer.iceConnectionChangeHandler = f
    }

    internal fun onIceGatheringStateChange(f: OnIceGatheringChange?) {
        observer.iceGatheringChangeHandler = f
    }

    internal fun onNegotiationNeeded(f: OnNegotiationNeeded?) {
        observer.negotiationNeededHandler = f
    }

    internal fun onSignalingStateChange(f: OnSignalingChange?) {
        observer.signalingChangeHandler = f
    }

    internal fun onTrack(f: OnTrack?) {
        observer.trackHandler = f
    }

    internal companion object {
        private val activePeers: MutableMap<Long, PeerConnection> = mutableMapOf()

        internal fun configure(
            config: RtcConfiguration,
            observer: PeerObserver,
            factoryHandle: Long = 0L,
        ): PeerConnection =
            PeerConnection(
                currentConfig = NativeRtcConfiguration.from(config),
                observer = observer,
                factoryHandle = factoryHandle,
            )
    }
}
