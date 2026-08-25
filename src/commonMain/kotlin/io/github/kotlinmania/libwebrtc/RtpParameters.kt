// port-lint: source rtp_parameters.rs
package io.github.kotlinmania.libwebrtc

public enum class Priority {
    VeryLow,
    Low,
    Medium,
    High,
}

public class RtpHeaderExtensionParameters(
    public val uri: String,
    public val id: Int,
    public val encrypted: Boolean,
)

public class RtcpParameters(
    public val cname: String = "",
    public val reducedSize: Boolean = false,
)

public class RtpCodecParameters(
    public val payloadType: UByte = 0u,
    public val mimeType: String = "",
    public val clockRate: ULong? = null,
    public val channels: UShort? = null,
)

public class RtpEncodingParameters(
    public val active: Boolean = true,
    public val maxBitrate: ULong? = null,
    public val maxFramerate: Double? = null,
    public val priority: Priority = Priority.Low,
    public val rid: String = "",
    public val scaleResolutionDownBy: Double? = null,
)

public class RtpCodecCapability(
    public val mimeType: String,
    public val clockRate: ULong? = null,
    public val channels: UShort? = null,
    public val sdpFmtpLine: String? = null,
)

public class RtpHeaderExtensionCapability(
    public val uri: String,
    public val direction: RtpTransceiverDirection,
)

public class RtpCapabilities(
    public val codecs: List<RtpCodecCapability> = emptyList(),
    public val headerExtensions: List<RtpHeaderExtensionCapability> = emptyList(),
)

public class RtpParameters(
    public val codecs: List<RtpCodecParameters> = emptyList(),
    public val headerExtensions: List<RtpHeaderExtensionParameters> = emptyList(),
    public val rtcp: RtcpParameters = RtcpParameters(),
)
