// port-lint: source native/rtp_transceiver.rs
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

import io.github.kotlinmania.libwebrtc.RtpCodecCapability
import io.github.kotlinmania.libwebrtc.RtpTransceiverDirection
import io.github.kotlinmania.libwebrtc.RtpTransceiverInit

internal class NativeRtpTransceiverInit(
    internal val direction: RtpTransceiverDirection,
    internal val streamIds: List<String>,
    internal val sendEncodings: List<NativeRtpEncodingParameters>,
) {
    internal companion object {
        internal fun from(value: RtpTransceiverInit): NativeRtpTransceiverInit =
            NativeRtpTransceiverInit(
                direction = value.direction,
                streamIds = value.streamIds,
                sendEncodings = value.sendEncodings.map { NativeRtpEncodingParameters.from(it) },
            )
    }
}

internal class RtpTransceiver(
    private val senderHandle: RtpSender = RtpSender(),
    private val receiverHandle: RtpReceiver = RtpReceiver(),
    private var transceiverDirection: RtpTransceiverDirection = RtpTransceiverDirection.SendRecv,
    private var transceiverMid: String? = null,
) {
    private var codecPreferences: List<NativeRtpCodecCapability> = emptyList()

    internal fun mid(): String? = transceiverMid

    internal fun currentDirection(): RtpTransceiverDirection? = transceiverDirection

    internal fun direction(): RtpTransceiverDirection = transceiverDirection

    internal fun sender(): io.github.kotlinmania.libwebrtc.RtpSender =
        io.github.kotlinmania.libwebrtc.RtpSender(handle = senderHandle)

    internal fun receiver(): io.github.kotlinmania.libwebrtc.RtpReceiver =
        io.github.kotlinmania.libwebrtc.RtpReceiver(handle = receiverHandle)

    internal fun setCodecPreferences(codecs: List<RtpCodecCapability>) {
        codecPreferences = codecs.map { NativeRtpCodecCapability.from(it) }
    }

    internal fun stop() {
        transceiverDirection = RtpTransceiverDirection.Stopped
    }
}
