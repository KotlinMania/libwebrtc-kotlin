// port-lint: source rtp_receiver.rs
package io.github.kotlinmania.libwebrtc

public class RtpReceiver(
    private val currentTrack: MediaStreamTrack? = null,
    private val currentParameters: RtpParameters = RtpParameters(),
) {
    public fun track(): MediaStreamTrack? = currentTrack

    public fun parameters(): RtpParameters = currentParameters
}
