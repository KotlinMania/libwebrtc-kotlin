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

public class RtpTransceiver(
    private val senderInstance: RtpSender = RtpSender(),
    private val receiverInstance: RtpReceiver = RtpReceiver(),
    private var transceiverDirection: RtpTransceiverDirection = RtpTransceiverDirection.SendRecv,
    private var transceiverMid: String? = null,
) {
    public fun mid(): String? = transceiverMid
    public fun direction(): RtpTransceiverDirection = transceiverDirection
    public fun currentDirection(): RtpTransceiverDirection? = transceiverDirection
    public fun sender(): RtpSender = senderInstance
    public fun receiver(): RtpReceiver = receiverInstance

    public fun stop() {
        transceiverDirection = RtpTransceiverDirection.Stopped
    }
}
