// port-lint: source media_stream.rs
package io.github.kotlinmania.libwebrtc

public enum class RtcTrackState {
    Live,
    Ended,
}

public interface MediaStreamTrack {
    public val id: String
    public val kind: String
    public val enabled: Boolean
    public val state: RtcTrackState

    public fun setEnabled(enabled: Boolean): Boolean
}

public class RtcAudioTrack(
    override val id: String,
    private var isTrackEnabled: Boolean = true,
    override var state: RtcTrackState = RtcTrackState.Live,
) : MediaStreamTrack {
    override val kind: String get() = "audio"
    override val enabled: Boolean get() = isTrackEnabled

    override fun setEnabled(enabled: Boolean): Boolean {
        isTrackEnabled = enabled
        return isTrackEnabled
    }
}

public class RtcVideoTrack(
    override val id: String,
    private var isTrackEnabled: Boolean = true,
    override var state: RtcTrackState = RtcTrackState.Live,
) : MediaStreamTrack {
    override val kind: String get() = "video"
    override val enabled: Boolean get() = isTrackEnabled

    override fun setEnabled(enabled: Boolean): Boolean {
        isTrackEnabled = enabled
        return isTrackEnabled
    }
}

public class MediaStream(
    public val id: String,
) {
    private val audioTracksList: MutableList<RtcAudioTrack> = mutableListOf()
    private val videoTracksList: MutableList<RtcVideoTrack> = mutableListOf()

    public fun audioTracks(): List<RtcAudioTrack> = audioTracksList.toList()
    public fun videoTracks(): List<RtcVideoTrack> = videoTracksList.toList()

    public fun addTrack(track: RtcAudioTrack) {
        audioTracksList.add(track)
    }

    public fun addTrack(track: RtcVideoTrack) {
        videoTracksList.add(track)
    }

    public fun removeTrack(track: RtcAudioTrack) {
        audioTracksList.remove(track)
    }

    public fun removeTrack(track: RtcVideoTrack) {
        videoTracksList.remove(track)
    }
}
