// port-lint: source libwebrtc/libwebrtc/src/session_description.rs
package io.github.kotlinmania.libwebrtc

public enum class SdpType(
    public val value: String,
) {
    Offer("offer"),
    PrAnswer("pranswer"),
    Answer("answer"),
    Rollback("rollback"),
    ;

    public companion object {
        public fun fromString(sdpType: String): SdpType =
            when (sdpType.lowercase()) {
                "offer" -> Offer
                "pranswer" -> PrAnswer
                "answer" -> Answer
                "rollback" -> Rollback
                else -> throw IllegalArgumentException("invalid SdpType: $sdpType")
            }
    }
}

public class SdpParseError(
    public val line: String,
    public val errorDescription: String,
) : RuntimeException("Failed to parse sdp: $line - $errorDescription")

public class SessionDescription(
    public val sdp: String,
    public val sdpType: SdpType,
) {
    override fun toString(): String = sdp

    public companion object {
        public fun parse(sdp: String, sdpType: SdpType): SessionDescription {
            if (sdp.isBlank()) {
                throw SdpParseError("", "SDP string cannot be blank")
            }
            return SessionDescription(sdp, sdpType)
        }
    }
}
