// port-lint: source libwebrtc/libwebrtc/src/peer_connection_factory.rs
package io.github.kotlinmania.libwebrtc

public data class IceServer(
    val urls: List<String> = emptyList(),
    val username: String = "",
    val credential: String = "",
)

public enum class ContinualGatheringPolicy {
    GatherOnce,
    GatherContinually,
}

public enum class IceTransportsType {
    Relay,
    NoHost,
    All,
}

public data class RtcConfiguration(
    val iceServers: List<IceServer> = emptyList(),
    val continualGatheringPolicy: ContinualGatheringPolicy = ContinualGatheringPolicy.GatherContinually,
    val iceTransportsType: IceTransportsType = IceTransportsType.All,
)

public class PeerConnectionFactory {
    public fun createPeerConnection(
        config: RtcConfiguration = RtcConfiguration(),
        observer: PeerConnectionObserver? = null,
    ): PeerConnection {
        return PeerConnection(config, observer)
    }

    public fun createAudioTrack(id: String): RtcAudioTrack {
        return RtcAudioTrack(id)
    }

    public fun createVideoTrack(id: String): RtcVideoTrack {
        return RtcVideoTrack(id)
    }

    public companion object {
        public fun withPlatformAdm(): PeerConnectionFactory = PeerConnectionFactory()
    }
}
