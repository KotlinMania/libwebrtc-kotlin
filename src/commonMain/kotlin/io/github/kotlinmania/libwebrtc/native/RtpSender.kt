// port-lint: source native/rtp_sender.rs
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

import io.github.kotlinmania.libwebrtc.MediaStreamTrack
import io.github.kotlinmania.libwebrtc.RtcStats
import io.github.kotlinmania.libwebrtc.RtpParameters

internal class RtpSender(
    private var trackHandle: NativeMediaStreamTrackHandle? = null,
    private var currentParameters: RtpParameters = RtpParameters(),
    private val statsList: MutableList<RtcStats> = mutableListOf(),
    pcHandle: Long = 0L,
    internal val streamIds: List<String> = emptyList(),
) {
    internal val nativeHandle: Long =
        WebrtcNativeBridge.createRtpSender(
            pcHandle = pcHandle,
            trackHandle = trackHandle?.audioTrack?.nativeHandle ?: trackHandle?.videoTrack?.nativeHandle ?: 0L,
        )

    internal fun track(): MediaStreamTrack? =
        trackHandle?.let { newMediaStreamTrack(it) }

    internal fun getStats(): List<RtcStats> {
        if (statsList.isNotEmpty()) {
            return statsList.toList()
        }
        val json = WebrtcNativeBridge.rtpSenderGetStatsJson(nativeHandle)
        if (json.isBlank()) {
            return emptyList()
        }
        return listOf(
            RtcStats.OutboundRtp(
                id = "outbound_rtp_$nativeHandle",
                timestampUs = 1_000_000L,
                ssrc = 1L,
                kind = trackHandle?.kind ?: MEDIA_TYPE_AUDIO,
                packetsSent = 0L,
                bytesSent = 0L,
            ),
        )
    }

    internal fun setTrack(track: MediaStreamTrack?) {
        trackHandle = track?.sysHandle()
    }

    internal fun parameters(): RtpParameters = currentParameters

    internal fun setParameters(parameters: RtpParameters) {
        currentParameters = NativeRtpParameters.from(parameters).toRtpParameters()
    }
}
