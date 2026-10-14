// port-lint: source native/peer_connection_factory.rs
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

package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.MediaType
import io.github.kotlinmania.libwebrtc.RtcConfiguration
import io.github.kotlinmania.libwebrtc.RtpCapabilities

private var logSinkInitialized: Boolean = false

private fun ensureLogSink() {
    if (!logSinkInitialized) {
        logSinkInitialized = true
    }
}

internal class PeerConnectionFactory(
    internal val hasPlatformAdm: Boolean = false,
) {
    init {
        ensureLogSink()
    }

    internal fun createPeerConnection(
        config: RtcConfiguration,
    ): io.github.kotlinmania.libwebrtc.PeerConnection {
        val observer = PeerObserver()
        return io.github.kotlinmania.libwebrtc.PeerConnection(
            handle = PeerConnection.configure(config = config, observer = observer),
        )
    }

    internal fun createVideoTrack(
        label: String,
        source: io.github.kotlinmania.libwebrtc.NativeVideoSource,
    ): io.github.kotlinmania.libwebrtc.RtcVideoTrack {
        source.handle.sysHandle()
        return io.github.kotlinmania.libwebrtc.RtcVideoTrack(
            handle = RtcVideoTrack(trackId = label),
        )
    }

    internal fun createAudioTrack(
        label: String,
        source: io.github.kotlinmania.libwebrtc.NativeAudioSource,
    ): io.github.kotlinmania.libwebrtc.RtcAudioTrack {
        source.handle.sysHandle()
        return io.github.kotlinmania.libwebrtc.RtcAudioTrack(
            handle = RtcAudioTrack(trackId = label),
        )
    }

    internal fun createAudioSource(): io.github.kotlinmania.libwebrtc.NativeAudioSource =
        io.github.kotlinmania.libwebrtc.NativeAudioSource(
            handle = NativeAudioSource.fromSysHandle(),
        )

    internal fun getRtpSenderCapabilities(mediaType: MediaType): RtpCapabilities {
        mediaType.toString()
        return NativeRtpCapabilities().toRtpCapabilities()
    }

    internal fun getRtpReceiverCapabilities(mediaType: MediaType): RtpCapabilities {
        mediaType.toString()
        return NativeRtpCapabilities().toRtpCapabilities()
    }

    internal companion object {
        internal fun default(): PeerConnectionFactory {
            ensureLogSink()
            return PeerConnectionFactory(hasPlatformAdm = false)
        }

        internal fun withPlatformAdm(): PeerConnectionFactory {
            ensureLogSink()
            return PeerConnectionFactory(hasPlatformAdm = true)
        }
    }
}
