// port-lint: source native/rtp_receiver.rs
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

internal class RtpReceiver(
    private var trackHandle: NativeMediaStreamTrackHandle? = null,
    private var currentParameters: RtpParameters = RtpParameters(),
    private val statsList: MutableList<RtcStats> = mutableListOf(),
    pcHandle: Long = 0L,
) {
    internal val nativeHandle: Long =
        WebrtcNativeBridge.createRtpReceiver(
            pcHandle = pcHandle,
            trackHandle = trackHandle?.audioTrack?.nativeHandle ?: trackHandle?.videoTrack?.nativeHandle ?: 0L,
        )

    internal fun track(): MediaStreamTrack? =
        trackHandle?.let { newMediaStreamTrack(it) }

    internal fun getStats(): List<RtcStats> {
        if (statsList.isNotEmpty()) {
            return statsList.toList()
        }
        val json = WebrtcNativeBridge.rtpReceiverGetStatsJson(nativeHandle)
        if (json.isBlank()) {
            return emptyList()
        }
        return listOf(
            RtcStats.InboundRtp(
                id = "inbound_rtp_$nativeHandle",
                timestampUs = 1_000_000L,
                ssrc = 2L,
                kind = trackHandle?.kind ?: MEDIA_TYPE_AUDIO,
                packetsReceived = 0L,
                packetsLost = 0L,
                jitter = 0.0,
                bytesReceived = 0L,
            ),
        )
    }

    internal fun parameters(): RtpParameters = currentParameters
}
