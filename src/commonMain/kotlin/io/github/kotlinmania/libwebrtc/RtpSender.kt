// port-lint: source libwebrtc/libwebrtc/src/rtp_sender.rs
package io.github.kotlinmania.libwebrtc

public class RtpSender(
    private var currentTrack: MediaStreamTrack? = null,
    private var currentParameters: RtpParameters = RtpParameters(),
) {
    public fun track(): MediaStreamTrack? = currentTrack

    public fun setTrack(track: MediaStreamTrack?) {
        currentTrack = track
    }

    public fun parameters(): RtpParameters = currentParameters

    public fun setParameters(parameters: RtpParameters) {
        currentParameters = parameters
    }
}
