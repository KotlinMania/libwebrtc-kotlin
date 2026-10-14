// port-lint: source stats.rs
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

/**
 * Values from https://www.w3.org/TR/webrtc-stats/ (NOTE: Some of the structs are not in the SPEC
 * but inside libwebrtc).
 * Serde will handle the magic of correctly deserializing the json into our structs.
 * The enum values are encapsulated inside option because we are not sure about their
 * default values (So we default to null instead of an arbitrary value).
 */
public enum class QualityLimitationReason {
    None,
    Cpu,
    Bandwidth,
    Other,
}

public enum class DtlsTransportState {
    New,
    Connecting,
    Connected,
    Closed,
    Failed,
}

public enum class IceTransportState {
    New,
    Checking,
    Connected,
    Completed,
    Disconnected,
    Failed,
    Closed,
}

public enum class DtlsRole {
    Client,
    Server,
    Unknown,
}

public enum class IceRole {
    Unknown,
    Controlling,
    Controlled,
}

public enum class IceCandidatePairState {
    Frozen,
    Waiting,
    InProgress,
    Failed,
    Succeeded,
}

public enum class IceCandidateType {
    Host,
    Srflx,
    Prflx,
    Relay,
}

public enum class IceServerTransportProtocol {
    Udp,
    Tcp,
    Tls,
}

public enum class IceTcpCandidateType {
    Active,
    Passive,
    So,
}

public data class RtcStatsDict(
    public val id: String = "",
    public val timestamp: Long = 0L,
)

public data class CodecStats(
    public val payloadType: UInt = 0u,
    public val transportId: String = "",
    public val mimeType: String = "",
    public val clockRate: UInt = 0u,
    public val channels: UInt = 0u,
    public val sdpFmtpLine: String = "",
)

public data class RtpStreamStats(
    public val ssrc: UInt = 0u,
    public val kind: String = "",
    public val transportId: String = "",
    public val codecId: String = "",
)

public data class ReceivedRtpStreamStats(
    public val packetsReceived: ULong = 0uL,
    public val packetsLost: Long = 0L,
    public val jitter: Double = 0.0,
)

public data class InboundRtpStreamStats(
    public val trackIdentifier: String = "",
    public val mid: String = "",
    public val remoteId: String = "",
    public val framesDecoded: UInt = 0u,
    public val keyFramesDecoded: UInt = 0u,
    public val framesRendered: UInt = 0u,
    public val framesDropped: UInt = 0u,
    public val frameWidth: UInt = 0u,
    public val frameHeight: UInt = 0u,
    public val framesPerSecond: Double = 0.0,
    public val qpSum: ULong = 0uL,
    public val totalDecodeTime: Double = 0.0,
    public val totalInterFrameDelay: Double = 0.0,
    public val totalSquaredInterFrameDelay: Double = 0.0,
    public val pauseCount: UInt = 0u,
    public val totalPausesDuration: Double = 0.0,
    public val freezeCount: UInt = 0u,
    public val totalFreezesDuration: Double = 0.0,
    public val lastPacketReceivedTimestamp: Double = 0.0,
    public val headerBytesReceived: ULong = 0uL,
    public val packetsDiscarded: ULong = 0uL,
    public val fecBytesReceived: ULong = 0uL,
    public val fecPacketsReceived: ULong = 0uL,
    public val fecPacketsDiscarded: ULong = 0uL,
    public val bytesReceived: ULong = 0uL,
    public val nackCount: UInt = 0u,
    public val firCount: UInt = 0u,
    public val pliCount: UInt = 0u,
    public val totalProcessingDelay: Double = 0.0,
    public val estimatedPlayoutTimestamp: Double = 0.0,
    public val jitterBufferDelay: Double = 0.0,
    public val jitterBufferTargetDelay: Double = 0.0,
    public val jitterBufferEmittedCount: ULong = 0uL,
    public val jitterBufferMinimumDelay: Double = 0.0,
    public val totalSamplesReceived: ULong = 0uL,
    public val concealedSamples: ULong = 0uL,
    public val silentConcealedSamples: ULong = 0uL,
    public val concealmentEvents: ULong = 0uL,
    public val insertedSamplesForDeceleration: ULong = 0uL,
    public val removedSamplesForAcceleration: ULong = 0uL,
    public val audioLevel: Double = 0.0,
    public val totalAudioEnergy: Double = 0.0,
    public val totalSamplesDuration: Double = 0.0,
    public val framesReceived: UInt = 0u,
    public val decoderImplementation: String = "",
    public val playoutId: String = "",
    public val powerEfficientDecoder: Boolean = false,
    public val framesAssembledFromMultiplePackets: UInt = 0u,
    public val totalAssemblyTime: Double = 0.0,
    public val retransmittedPacketsReceived: ULong = 0uL,
    public val retransmittedBytesReceived: ULong = 0uL,
    public val rtxSsrc: UInt = 0u,
    public val fecSsrc: UInt = 0u,
)

public data class RemoteInboundRtpStreamStats(
    public val localId: String = "",
    public val roundTripTime: Double = 0.0,
    public val totalRoundTripTime: Double = 0.0,
    public val fractionLost: Double = 0.0,
    public val roundTripTimeMeasurements: ULong = 0uL,
)

public data class SentRtpStreamStats(
    public val packetsSent: ULong = 0uL,
    public val bytesSent: ULong = 0uL,
)

public data class QualityLimitationDurations(
    public val none: Double = 0.0,
    public val cpu: Double = 0.0,
    public val bandwidth: Double = 0.0,
    public val other: Double = 0.0,
)

public data class OutboundRtpStreamStats(
    public val mid: String = "",
    public val mediaSourceId: String = "",
    public val remoteId: String = "",
    public val rid: String = "",
    public val headerBytesSent: ULong = 0uL,
    public val retransmittedPacketsSent: ULong = 0uL,
    public val retransmittedBytesSent: ULong = 0uL,
    public val rtxSsrc: UInt = 0u,
    public val targetBitrate: Double = 0.0,
    public val totalEncodedBytesTarget: ULong = 0uL,
    public val frameWidth: UInt = 0u,
    public val frameHeight: UInt = 0u,
    public val framesPerSecond: Double = 0.0,
    public val framesSent: UInt = 0u,
    public val hugeFramesSent: UInt = 0u,
    public val framesEncoded: UInt = 0u,
    public val keyFramesEncoded: UInt = 0u,
    public val qpSum: ULong = 0uL,
    public val totalEncodeTime: Double = 0.0,
    public val totalPacketSendDelay: Double = 0.0,
    public val qualityLimitationReason: QualityLimitationReason = QualityLimitationReason.None,
    public val qualityLimitationDurations: QualityLimitationDurations = QualityLimitationDurations(),
    public val qualityLimitationResolutionChanges: UInt = 0u,
    public val nackCount: UInt = 0u,
    public val firCount: UInt = 0u,
    public val pliCount: UInt = 0u,
    public val encoderImplementation: String = "",
    public val powerEfficientEncoder: Boolean = false,
    public val active: Boolean = false,
    public val scalabilityMode: String = "",
)

public data class RemoteOutboundRtpStreamStats(
    public val localId: String = "",
    public val remoteTimestamp: Double = 0.0,
    public val reportsSent: ULong = 0uL,
    public val roundTripTime: Double = 0.0,
    public val totalRoundTripTime: Double = 0.0,
    public val roundTripTimeMeasurements: ULong = 0uL,
)

public data class MediaSourceStats(
    public val trackIdentifier: String = "",
    public val kind: String = "",
)

public data class AudioSourceStats(
    public val audioLevel: Double = 0.0,
    public val totalAudioEnergy: Double = 0.0,
    public val totalSamplesDuration: Double = 0.0,
    public val echoReturnLoss: Double = 0.0,
    public val echoReturnLossEnhancement: Double = 0.0,
    public val droppedSamplesDuration: Double = 0.0,
    public val droppedSamplesEvents: UInt = 0u,
    public val totalCaptureDelay: Double = 0.0,
    public val totalSamplesCaptured: ULong = 0uL,
)

public data class VideoSourceStats(
    public val width: UInt = 0u,
    public val height: UInt = 0u,
    public val frames: UInt = 0u,
    public val framesPerSecond: Double = 0.0,
)

public data class AudioPlayoutStats(
    public val kind: String = "",
    public val synthesizedSamplesDuration: Double = 0.0,
    public val synthesizedSamplesEvents: UInt = 0u,
    public val totalSamplesDuration: Double = 0.0,
    public val totalPlayoutDelay: Double = 0.0,
    public val totalSamplesCount: ULong = 0uL,
)

public data class PeerConnectionStats(
    public val dataChannelsOpened: UInt = 0u,
    public val dataChannelsClosed: UInt = 0u,
)

public data class DataChannelStats(
    public val label: String = "",
    public val protocol: String = "",
    public val dataChannelIdentifier: Int = 0,
    public val state: DataChannelState? = null,
    public val messagesSent: ULong = 0uL,
    public val bytesSent: ULong = 0uL,
    public val messagesReceived: ULong = 0uL,
    public val bytesReceived: ULong = 0uL,
)

public data class TransportStats(
    public val packetsSent: ULong = 0uL,
    public val packetsReceived: ULong = 0uL,
    public val bytesSent: ULong = 0uL,
    public val bytesReceived: ULong = 0uL,
    public val iceRole: IceRole = IceRole.Unknown,
    public val iceLocalUsernameFragment: String = "",
    public val dtlsState: DtlsTransportState? = null,
    public val iceState: IceTransportState? = null,
    public val selectedCandidatePairId: String = "",
    public val localCertificateId: String = "",
    public val remoteCertificateId: String = "",
    public val tlsVersion: String = "",
    public val dtlsCipher: String = "",
    public val dtlsRole: DtlsRole = DtlsRole.Unknown,
    public val srtpCipher: String = "",
    public val selectedCandidatePairChanges: UInt = 0u,
)

public data class CandidatePairStats(
    public val transportId: String = "",
    public val localCandidateId: String = "",
    public val remoteCandidateId: String = "",
    public val state: IceCandidatePairState? = null,
    public val nominated: Boolean = false,
    public val packetsSent: ULong = 0uL,
    public val packetsReceived: ULong = 0uL,
    public val bytesSent: ULong = 0uL,
    public val bytesReceived: ULong = 0uL,
    public val lastPacketSentTimestamp: Double = 0.0,
    public val lastPacketReceivedTimestamp: Double = 0.0,
    public val totalRoundTripTime: Double = 0.0,
    public val currentRoundTripTime: Double = 0.0,
    public val availableOutgoingBitrate: Double = 0.0,
    public val availableIncomingBitrate: Double = 0.0,
    public val requestsReceived: ULong = 0uL,
    public val requestsSent: ULong = 0uL,
    public val responsesReceived: ULong = 0uL,
    public val responsesSent: ULong = 0uL,
    public val consentRequestsSent: ULong = 0uL,
    public val packetsDiscardedOnSend: UInt = 0u,
    public val bytesDiscardedOnSend: ULong = 0uL,
)

public data class IceCandidateStats(
    public val transportId: String = "",
    public val address: String = "",
    public val port: Int = 0,
    public val protocol: String = "",
    public val candidateType: String = "",
    public val priority: Int = 0,
    public val url: String = "",
    public val relayProtocol: String = "",
    public val foundation: String = "",
    public val relatedAddress: String = "",
    public val relatedPort: Int = 0,
    public val usernameFragment: String = "",
    public val tcpType: String = "",
)

public data class CertificateStats(
    public val fingerprint: String = "",
    public val fingerprintAlgorithm: String = "",
    public val base64Certificate: String = "",
    public val issuerCertificateId: String = "",
)

public data class StreamStats(
    public val id: String = "",
    public val streamIdentifier: String = "",
)

public sealed class RtcStats {
    public abstract val id: String
    public abstract val timestampUs: Long

    public data class Codec(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val payloadType: Int = 0,
        public val transportId: String? = null,
        public val mimeType: String = "",
        public val clockRate: Long? = null,
        public val channels: Int? = null,
        public val sdpFmtpLine: String? = null,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val codec: CodecStats =
            CodecStats(
                payloadType = payloadType.toUInt(),
                transportId = transportId ?: "",
                mimeType = mimeType,
                clockRate = (clockRate ?: 0L).toUInt(),
                channels = (channels ?: 0).toUInt(),
                sdpFmtpLine = sdpFmtpLine ?: "",
            ),
    ) : RtcStats()

    public data class InboundRtp(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val ssrc: Long = 0L,
        public val kind: String = "",
        public val transportId: String? = null,
        public val codecId: String? = null,
        public val packetsReceived: Long? = null,
        public val packetsLost: Long? = null,
        public val jitter: Double? = null,
        public val bytesReceived: Long? = null,
        public val framesDecoded: Long? = null,
        public val keyFramesDecoded: Long? = null,
        public val frameWidth: Long? = null,
        public val frameHeight: Long? = null,
        public val framesPerSecond: Double? = null,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val stream: RtpStreamStats =
            RtpStreamStats(
                ssrc = ssrc.toUInt(),
                kind = kind,
                transportId = transportId ?: "",
                codecId = codecId ?: "",
            ),
        public val received: ReceivedRtpStreamStats =
            ReceivedRtpStreamStats(
                packetsReceived = (packetsReceived ?: 0L).toULong(),
                packetsLost = packetsLost ?: 0L,
                jitter = jitter ?: 0.0,
            ),
        public val inbound: InboundRtpStreamStats =
            InboundRtpStreamStats(
                bytesReceived = (bytesReceived ?: 0L).toULong(),
                framesDecoded = (framesDecoded ?: 0L).toUInt(),
                keyFramesDecoded = (keyFramesDecoded ?: 0L).toUInt(),
                frameWidth = (frameWidth ?: 0L).toUInt(),
                frameHeight = (frameHeight ?: 0L).toUInt(),
                framesPerSecond = framesPerSecond ?: 0.0,
            ),
    ) : RtcStats()

    public data class OutboundRtp(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val ssrc: Long = 0L,
        public val kind: String = "",
        public val transportId: String? = null,
        public val codecId: String? = null,
        public val packetsSent: Long? = null,
        public val bytesSent: Long? = null,
        public val framesEncoded: Long? = null,
        public val keyFramesEncoded: Long? = null,
        public val frameWidth: Long? = null,
        public val frameHeight: Long? = null,
        public val framesPerSecond: Double? = null,
        public val qualityLimitationReason: QualityLimitationReason? = null,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val stream: RtpStreamStats =
            RtpStreamStats(
                ssrc = ssrc.toUInt(),
                kind = kind,
                transportId = transportId ?: "",
                codecId = codecId ?: "",
            ),
        public val sent: SentRtpStreamStats =
            SentRtpStreamStats(
                packetsSent = (packetsSent ?: 0L).toULong(),
                bytesSent = (bytesSent ?: 0L).toULong(),
            ),
        public val outbound: OutboundRtpStreamStats =
            OutboundRtpStreamStats(
                framesEncoded = (framesEncoded ?: 0L).toUInt(),
                keyFramesEncoded = (keyFramesEncoded ?: 0L).toUInt(),
                frameWidth = (frameWidth ?: 0L).toUInt(),
                frameHeight = (frameHeight ?: 0L).toUInt(),
                framesPerSecond = framesPerSecond ?: 0.0,
                qualityLimitationReason = qualityLimitationReason ?: QualityLimitationReason.None,
            ),
    ) : RtcStats()

    public data class RemoteInboundRtp(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val stream: RtpStreamStats = RtpStreamStats(),
        public val received: ReceivedRtpStreamStats = ReceivedRtpStreamStats(),
        public val remoteInbound: RemoteInboundRtpStreamStats = RemoteInboundRtpStreamStats(),
    ) : RtcStats()

    public data class RemoteOutboundRtp(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val stream: RtpStreamStats = RtpStreamStats(),
        public val sent: SentRtpStreamStats = SentRtpStreamStats(),
        public val remoteOutbound: RemoteOutboundRtpStreamStats = RemoteOutboundRtpStreamStats(),
    ) : RtcStats()

    public data class MediaSource(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val source: MediaSourceStats = MediaSourceStats(),
        public val audio: AudioSourceStats = AudioSourceStats(),
        public val video: VideoSourceStats = VideoSourceStats(),
    ) : RtcStats()

    public data class MediaPlayout(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val audioPlayout: AudioPlayoutStats = AudioPlayoutStats(),
    ) : RtcStats()

    public data class PeerConnection(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val peerConnection: PeerConnectionStats = PeerConnectionStats(),
    ) : RtcStats()

    public data class DataChannel(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val label: String? = null,
        public val protocol: String? = null,
        public val dataChannelIdentifier: Int? = null,
        public val state: DataChannelState? = null,
        public val messagesSent: Long? = null,
        public val bytesSent: Long? = null,
        public val messagesReceived: Long? = null,
        public val bytesReceived: Long? = null,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val dataChannel: DataChannelStats =
            DataChannelStats(
                label = label ?: "",
                protocol = protocol ?: "",
                dataChannelIdentifier = dataChannelIdentifier ?: 0,
                state = state,
                messagesSent = (messagesSent ?: 0L).toULong(),
                bytesSent = (bytesSent ?: 0L).toULong(),
                messagesReceived = (messagesReceived ?: 0L).toULong(),
                bytesReceived = (bytesReceived ?: 0L).toULong(),
            ),
    ) : RtcStats()

    public data class Transport(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val bytesSent: Long? = null,
        public val bytesReceived: Long? = null,
        public val packetsSent: Long? = null,
        public val packetsReceived: Long? = null,
        public val dtlsState: DtlsTransportState? = null,
        public val iceState: IceTransportState? = null,
        public val dtlsRole: DtlsRole? = null,
        public val iceRole: IceRole? = null,
        public val selectedCandidatePairId: String? = null,
        public val localCertificateId: String? = null,
        public val remoteCertificateId: String? = null,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val transport: TransportStats =
            TransportStats(
                packetsSent = (packetsSent ?: 0L).toULong(),
                packetsReceived = (packetsReceived ?: 0L).toULong(),
                bytesSent = (bytesSent ?: 0L).toULong(),
                bytesReceived = (bytesReceived ?: 0L).toULong(),
                iceRole = iceRole ?: IceRole.Unknown,
                dtlsState = dtlsState,
                iceState = iceState,
                selectedCandidatePairId = selectedCandidatePairId ?: "",
                localCertificateId = localCertificateId ?: "",
                remoteCertificateId = remoteCertificateId ?: "",
                dtlsRole = dtlsRole ?: DtlsRole.Unknown,
            ),
    ) : RtcStats()

    public data class CandidatePair(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val transportId: String = "",
        public val localCandidateId: String = "",
        public val remoteCandidateId: String = "",
        public val state: String? = null,
        public val nominated: Boolean? = null,
        public val packetsSent: Long? = null,
        public val packetsReceived: Long? = null,
        public val bytesSent: Long? = null,
        public val bytesReceived: Long? = null,
        public val currentRoundTripTime: Double? = null,
        public val totalRoundTripTime: Double? = null,
        public val availableOutgoingBitrate: Double? = null,
        public val availableIncomingBitrate: Double? = null,
        public val requestsReceived: Long? = null,
        public val requestsSent: Long? = null,
        public val responsesReceived: Long? = null,
        public val responsesSent: Long? = null,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val candidatePair: CandidatePairStats =
            CandidatePairStats(
                transportId = transportId,
                localCandidateId = localCandidateId,
                remoteCandidateId = remoteCandidateId,
                nominated = nominated ?: false,
                packetsSent = (packetsSent ?: 0L).toULong(),
                packetsReceived = (packetsReceived ?: 0L).toULong(),
                bytesSent = (bytesSent ?: 0L).toULong(),
                bytesReceived = (bytesReceived ?: 0L).toULong(),
                currentRoundTripTime = currentRoundTripTime ?: 0.0,
                totalRoundTripTime = totalRoundTripTime ?: 0.0,
                availableOutgoingBitrate = availableOutgoingBitrate ?: 0.0,
                availableIncomingBitrate = availableIncomingBitrate ?: 0.0,
                requestsReceived = (requestsReceived ?: 0L).toULong(),
                requestsSent = (requestsSent ?: 0L).toULong(),
                responsesReceived = (responsesReceived ?: 0L).toULong(),
                responsesSent = (responsesSent ?: 0L).toULong(),
            ),
    ) : RtcStats()

    public data class LocalCandidate(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val candidate: IceCandidateStats = IceCandidateStats(),
    ) : RtcStats()

    public data class RemoteCandidate(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val candidate: IceCandidateStats = IceCandidateStats(),
    ) : RtcStats()

    public data class Certificate(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val certificate: CertificateStats = CertificateStats(),
    ) : RtcStats()

    public data class Stream(
        override val id: String = "",
        override val timestampUs: Long = 0L,
        public val stats: RtcStatsDict = RtcStatsDict(id = id, timestamp = timestampUs),
        public val stream: StreamStats = StreamStats(),
    ) : RtcStats()

    public data class Track(
        override val id: String = "",
        override val timestampUs: Long = 0L,
    ) : RtcStats()
}

public typealias RtcCodecStats = RtcStats.Codec
public typealias RtcInboundRtpStats = RtcStats.InboundRtp
public typealias RtcOutboundRtpStats = RtcStats.OutboundRtp
public typealias RtcRemoteInboundRtpStats = RtcStats.RemoteInboundRtp
public typealias RtcRemoteOutboundRtpStats = RtcStats.RemoteOutboundRtp
public typealias RtcMediaSourceStats = RtcStats.MediaSource
public typealias RtcMediaPlayoutStats = RtcStats.MediaPlayout
public typealias RtcPeerConnectionStats = RtcStats.PeerConnection
public typealias RtcDataChannelStats = RtcStats.DataChannel
public typealias RtcTransportStats = RtcStats.Transport
public typealias RtcCandidatePairStats = RtcStats.CandidatePair
public typealias RtcLocalCandidateStats = RtcStats.LocalCandidate
public typealias RtcRemoteCandidateStats = RtcStats.RemoteCandidate
public typealias RtcCertificateStats = RtcStats.Certificate
public typealias RtcStreamStats = RtcStats.Stream
public typealias RtcTrackStats = RtcStats.Track

public typealias InboundRtpStats = RtcStats.InboundRtp
public typealias OutboundRtpStats = RtcStats.OutboundRtp
public typealias RemoteInboundRtpStats = RtcStats.RemoteInboundRtp
public typealias RemoteOutboundRtpStats = RtcStats.RemoteOutboundRtp
public typealias MediaPlayoutStats = RtcStats.MediaPlayout
public typealias LocalCandidateStats = RtcStats.LocalCandidate
public typealias RemoteCandidateStats = RtcStats.RemoteCandidate
public typealias TrackStats = RtcStats.Track
