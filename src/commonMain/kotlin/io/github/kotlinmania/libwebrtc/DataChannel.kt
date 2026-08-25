// port-lint: source data_channel.rs
package io.github.kotlinmania.libwebrtc

public enum class DataChannelState {
    Connecting,
    Open,
    Closing,
    Closed,
}

public class DataChannelInit(
    public val ordered: Boolean = true,
    public val maxRetransmitTime: Int? = null,
    public val maxRetransmits: Int? = null,
    public val protocol: String = "",
    public val negotiated: Boolean = false,
    public val id: Int = -1,
    public val priority: Priority? = null,
)

public class DataBuffer(
    public val data: ByteArray,
    public val binary: Boolean,
)

public sealed class DataChannelException(message: String) : RuntimeException(message) {
    public class SendError(message: String = "failed to send data, dc not open? send buffer is full ?") :
        DataChannelException(message)
    public class Utf8Error(message: String = "only utf8 strings can be sent") :
        DataChannelException(message)
}
