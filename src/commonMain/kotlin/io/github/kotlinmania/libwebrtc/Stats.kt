// port-lint: source libwebrtc/libwebrtc/src/stats.rs
package io.github.kotlinmania.libwebrtc

public enum class QualityLimitationReason {
    None,
    Cpu,
    Bandwidth,
    Other,
}

public enum class IceRole {
    Unknown,
    Controlling,
    Controlled,
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

public sealed interface RtcStats {
    public val id: String
    public val timestampUs: Long

    public data class Codec(
        override val id: String,
        override val timestampUs: Long,
        val payloadType: Int,
        val mimeType: String,
        val clockRate: Long? = null,
        val channels: Int? = null,
        val sdpFmtpLine: String? = null,
        val transportId: String? = null,
    ) : RtcStats

    public data class InboundRtp(
        override val id: String,
        override val timestampUs: Long,
        val ssrc: Long,
        val kind: String,
        val transportId: String? = null,
        val codecId: String? = null,
        val packetsReceived: Long? = null,
        val packetsLost: Long? = null,
        val jitter: Double? = null,
        val bytesReceived: Long? = null,
        val headerBytesReceived: Long? = null,
        val packetsDiscarded: Long? = null,
        val nackCount: Long? = null,
        val firCount: Long? = null,
        val pliCount: Long? = null,
        val totalAssemblyTime: Double? = null,
        val framesReceived: Long? = null,
        val framesDecoded: Long? = null,
        val keyFramesDecoded: Long? = null,
        val framesDropped: Long? = null,
        val totalDecodeTime: Double? = null,
        val totalProcessingDelay: Double? = null,
    ) : RtcStats

    public data class OutboundRtp(
        override val id: String,
        override val timestampUs: Long,
        val ssrc: Long,
        val kind: String,
        val transportId: String? = null,
        val codecId: String? = null,
        val packetsSent: Long? = null,
        val bytesSent: Long? = null,
        val headerBytesSent: Long? = null,
        val retransmittedPacketsSent: Long? = null,
        val retransmittedBytesSent: Long? = null,
        val nackCount: Long? = null,
        val firCount: Long? = null,
        val pliCount: Long? = null,
        val framesEncoded: Long? = null,
        val keyFramesEncoded: Long? = null,
        val totalEncodeTime: Double? = null,
        val totalEncodedBytesTarget: Long? = null,
        val framesSent: Long? = null,
        val qualityLimitationReason: QualityLimitationReason? = null,
    ) : RtcStats

    public data class DataChannel(
        override val id: String,
        override val timestampUs: Long,
        val label: String,
        val protocol: String,
        val dataChannelIdentifier: Int,
        val state: DataChannelState,
        val messagesSent: Long,
        val bytesSent: Long,
        val messagesReceived: Long,
        val bytesReceived: Long,
    ) : RtcStats

    public data class Transport(
        override val id: String,
        override val timestampUs: Long,
        val bytesSent: Long? = null,
        val bytesReceived: Long? = null,
        val dtlsState: DtlsTransportState? = null,
        val selectedCandidatePairId: String? = null,
        val localCertificateId: String? = null,
        val remoteCertificateId: String? = null,
        val iceRole: IceRole? = null,
        val iceLocalUsernameFragment: String? = null,
        val dtlsRole: DtlsRole? = null,
        val iceState: IceTransportState? = null,
    ) : RtcStats

    public data class CandidatePair(
        override val id: String,
        override val timestampUs: Long,
        val transportId: String,
        val localCandidateId: String,
        val remoteCandidateId: String,
        val state: String? = null,
        val nominated: Boolean? = null,
        val packetsSent: Long? = null,
        val packetsReceived: Long? = null,
        val bytesSent: Long? = null,
        val bytesReceived: Long? = null,
        val currentRoundTripTime: Double? = null,
        val totalRoundTripTime: Double? = null,
        val availableOutgoingBitrate: Double? = null,
        val availableIncomingBitrate: Double? = null,
        val requestsReceived: Long? = null,
        val requestsSent: Long? = null,
        val responsesReceived: Long? = null,
        val responsesSent: Long? = null,
    ) : RtcStats
}
