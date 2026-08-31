// port-lint: source libwebrtc/libwebrtc/src/ice_candidate.rs
package io.github.kotlinmania.libwebrtc

public class IceCandidate(
    public val sdpMid: String,
    public val sdpMLineIndex: Int,
    public val candidate: String,
) {
    override fun toString(): String = candidate

    public companion object {
        public fun parse(sdpMid: String, sdpMLineIndex: Int, sdp: String): IceCandidate {
            if (sdp.isBlank()) {
                throw SdpParseError("", "Candidate SDP string cannot be blank")
            }
            return IceCandidate(sdpMid, sdpMLineIndex, sdp)
        }
    }
}
