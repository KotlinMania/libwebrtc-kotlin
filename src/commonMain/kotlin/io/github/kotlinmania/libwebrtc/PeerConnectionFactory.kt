// port-lint: source peer_connection_factory.rs
// Copyright 2025 LiveKit, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.github.kotlinmania.libwebrtc

import io.github.kotlinmania.libwebrtc.native.PeerConnectionFactory as ImpPeerConnectionFactory

public class IceServer(
    public val urls: List<String> = emptyList(),
    public val username: String = "",
    public val password: String = "",
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

public class RtcConfiguration(
    public val iceServers: List<IceServer> = emptyList(),
    public val continualGatheringPolicy: ContinualGatheringPolicy = ContinualGatheringPolicy.GatherContinually,
    public val iceTransportType: IceTransportsType = IceTransportsType.All,
) {
    public companion object {
        public fun default(): RtcConfiguration = RtcConfiguration()
    }
}

public interface PeerConnectionFactoryExt {
    public fun createVideoTrack(label: String, source: NativeVideoSource): RtcVideoTrack

    public fun createAudioTrack(label: String, source: NativeAudioSource): RtcAudioTrack

    public fun createAudioSource(): NativeAudioSource
}

public class PeerConnectionFactory internal constructor(
    internal val handle: ImpPeerConnectionFactory,
) : PeerConnectionFactoryExt {
    public constructor(hasPlatformAdm: Boolean = false) : this(
        handle =
            if (hasPlatformAdm) {
                ImpPeerConnectionFactory.withPlatformAdm()
            } else {
                ImpPeerConnectionFactory.default()
            },
    )

    public val hasPlatformAdm: Boolean
        get() = handle.hasPlatformAdm

    public fun createPeerConnection(config: RtcConfiguration = RtcConfiguration()): PeerConnection =
        handle.createPeerConnection(config)

    override fun createVideoTrack(
        label: String,
        source: NativeVideoSource,
    ): RtcVideoTrack = handle.createVideoTrack(label, source)

    public fun createVideoTrack(
        label: String,
        source: RtcVideoSource = RtcVideoSource.Native(NativeVideoSource.default()),
    ): RtcVideoTrack =
        when (source) {
            is RtcVideoSource.Native -> handle.createVideoTrack(label, source.source)
        }

    override fun createAudioTrack(
        label: String,
        source: NativeAudioSource,
    ): RtcAudioTrack = handle.createAudioTrack(label, source)

    public fun createAudioTrack(
        label: String,
        source: RtcAudioSource = RtcAudioSource.Native(NativeAudioSource.default()),
    ): RtcAudioTrack =
        when (source) {
            is RtcAudioSource.Native -> handle.createAudioTrack(label, source.source)
        }

    override fun createAudioSource(): NativeAudioSource = handle.createAudioSource()

    public fun getRtpSenderCapabilities(mediaType: MediaType): RtpCapabilities =
        handle.getRtpSenderCapabilities(mediaType)

    public fun getRtpReceiverCapabilities(mediaType: MediaType): RtpCapabilities =
        handle.getRtpReceiverCapabilities(mediaType)

    override fun toString(): String = "PeerConnectionFactory"

    public companion object {
        public fun default(): PeerConnectionFactory =
            PeerConnectionFactory(handle = ImpPeerConnectionFactory.default())

        public fun withPlatformAdm(): PeerConnectionFactory =
            PeerConnectionFactory(handle = ImpPeerConnectionFactory.withPlatformAdm())
    }
}
