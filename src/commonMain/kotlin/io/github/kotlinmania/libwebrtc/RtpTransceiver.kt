// port-lint: source rtp_transceiver.rs
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

import io.github.kotlinmania.libwebrtc.native.RtpTransceiver as ImpRtpTransceiver

public class RtpTransceiverInit(
    public val direction: RtpTransceiverDirection = RtpTransceiverDirection.SendRecv,
    public val streamIds: List<String> = emptyList(),
    public val sendEncodings: List<RtpEncodingParameters> = emptyList(),
)

public enum class RtpTransceiverDirection {
    SendRecv,
    SendOnly,
    RecvOnly,
    Inactive,
    Stopped,
}

public class RtpTransceiver internal constructor(
    internal val handle: ImpRtpTransceiver,
) {
    public constructor(
        senderInstance: RtpSender = RtpSender(),
        receiverInstance: RtpReceiver = RtpReceiver(),
        transceiverDirection: RtpTransceiverDirection = RtpTransceiverDirection.SendRecv,
        transceiverMid: String? = null,
    ) : this(
        handle =
            ImpRtpTransceiver(
                senderHandle = senderInstance.handle,
                receiverHandle = receiverInstance.handle,
                transceiverDirection = transceiverDirection,
                transceiverMid = transceiverMid,
            ),
    )

    public fun mid(): String? = handle.mid()

    public fun currentDirection(): RtpTransceiverDirection? = handle.currentDirection()

    public fun direction(): RtpTransceiverDirection = handle.direction()

    public fun sender(): RtpSender = handle.sender()

    public fun receiver(): RtpReceiver = handle.receiver()

    public fun setCodecPreferences(codecs: List<RtpCodecCapability>) {
        handle.setCodecPreferences(codecs)
    }

    public fun stop() {
        handle.stop()
    }

    override fun toString(): String =
        "RtpTransceiver(mid=${mid()}, direction=${direction()}, sender=${sender()}, receiver=${receiver()})"
}
