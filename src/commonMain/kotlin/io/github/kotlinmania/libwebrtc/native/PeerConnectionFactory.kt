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
    internal val nativeHandle: Long = WebrtcNativeBridge.createPeerConnectionFactory()

    init {
        ensureLogSink()
    }

    internal fun createPeerConnection(
        config: RtcConfiguration,
    ): io.github.kotlinmania.libwebrtc.PeerConnection {
        val observer = PeerObserver()
        return io.github.kotlinmania.libwebrtc.PeerConnection(
            handle =
                PeerConnection.configure(
                    config = config,
                    observer = observer,
                    factoryHandle = nativeHandle,
                ),
        )
    }

    internal fun createVideoTrack(
        label: String,
        source: io.github.kotlinmania.libwebrtc.NativeVideoSource,
    ): io.github.kotlinmania.libwebrtc.RtcVideoTrack {
        val sysSource = source.handle.sysHandle()
        return io.github.kotlinmania.libwebrtc.RtcVideoTrack(
            handle =
                RtcVideoTrack(
                    trackId = label,
                    factoryHandle = nativeHandle,
                    sourceHandle = sysSource.nativeHandle,
                ),
        )
    }

    internal fun createAudioTrack(
        label: String,
        source: io.github.kotlinmania.libwebrtc.NativeAudioSource,
    ): io.github.kotlinmania.libwebrtc.RtcAudioTrack {
        val sysSource = source.handle.sysHandle()
        return io.github.kotlinmania.libwebrtc.RtcAudioTrack(
            handle =
                RtcAudioTrack(
                    trackId = label,
                    factoryHandle = nativeHandle,
                    sourceHandle = sysSource.nativeHandle,
                ),
        )
    }

    internal fun createAudioSource(): io.github.kotlinmania.libwebrtc.NativeAudioSource =
        io.github.kotlinmania.libwebrtc.NativeAudioSource(
            handle = NativeAudioSource.fromSysHandle(),
        )

    internal fun getRtpSenderCapabilities(mediaType: MediaType): RtpCapabilities {
        val codecs =
            when (mediaType) {
                MediaType.Audio -> {
                    listOf(
                        NativeRtpCodecCapability(
                            name = "opus",
                            kind = MediaType.Audio,
                            mimeType = "audio/opus",
                            hasClockRate = true,
                            clockRate = 48_000,
                            hasNumChannels = true,
                            numChannels = 2,
                        ),
                    )
                }

                MediaType.Video -> {
                    listOf(
                        NativeRtpCodecCapability(
                            name = "VP8",
                            kind = MediaType.Video,
                            mimeType = "video/VP8",
                            hasClockRate = true,
                            clockRate = 90_000,
                            hasNumChannels = false,
                            numChannels = 0,
                        ),
                    )
                }

                MediaType.Data, MediaType.Unsupported -> {
                    emptyList()
                }
            }
        return NativeRtpCapabilities(codecs = codecs).toRtpCapabilities()
    }

    internal fun getRtpReceiverCapabilities(mediaType: MediaType): RtpCapabilities =
        getRtpSenderCapabilities(mediaType)

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
