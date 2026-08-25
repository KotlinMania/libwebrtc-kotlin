// port-lint: source rtp_transceiver.rs
package io.github.kotlinmania.libwebrtc

public enum class RtpTransceiverDirection {
    SendRecv,
    SendOnly,
    RecvOnly,
    Inactive,
    Stopped,
}

public class RtpTransceiverInit(
    public val direction: RtpTransceiverDirection = RtpTransceiverDirection.SendRecv,
    public val streamIds: List<String> = emptyList(),
    public val sendEncodings: List<RtpEncodingParameters> = emptyList(),
)
