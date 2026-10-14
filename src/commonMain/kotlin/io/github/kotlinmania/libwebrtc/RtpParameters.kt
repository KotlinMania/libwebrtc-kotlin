// port-lint: source rtp_parameters.rs
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

public enum class Priority {
    VeryLow,
    Low,
    Medium,
    High,
}

public class RtpHeaderExtensionParameters(
    public val uri: String,
    public val id: Int,
    public val encrypted: Boolean,
)

public class RtpParameters(
    public val codecs: List<RtpCodecParameters> = emptyList(),
    public val headerExtensions: List<RtpHeaderExtensionParameters> = emptyList(),
    public val rtcp: RtcpParameters = RtcpParameters(),
) {
    public companion object {
        public fun default(): RtpParameters = RtpParameters()
    }
}

public class RtpCodecParameters(
    public val payloadType: UByte = 0u,
    public val mimeType: String = "",
    public val clockRate: ULong? = null,
    public val channels: UShort? = null,
) {
    public companion object {
        public fun default(): RtpCodecParameters = RtpCodecParameters()
    }
}

public class RtcpParameters(
    public val cname: String = "",
    public val reducedSize: Boolean = false,
) {
    public companion object {
        public fun default(): RtcpParameters = RtcpParameters()
    }
}

public class RtpEncodingParameters(
    public val active: Boolean = true,
    public val maxBitrate: ULong? = null,
    public val maxFramerate: Double? = null,
    public val priority: Priority = Priority.Low,
    public val rid: String = "",
    public val scaleResolutionDownBy: Double? = null,
) {
    public companion object {
        public fun default(): RtpEncodingParameters =
            RtpEncodingParameters(
                active = true,
                maxBitrate = null,
                maxFramerate = null,
                priority = Priority.Low,
                rid = "",
                scaleResolutionDownBy = null,
            )
    }
}

public class RtpCodecCapability(
    public val mimeType: String,
    public val clockRate: ULong? = null,
    public val channels: UShort? = null,
    public val sdpFmtpLine: String? = null,
)

public class RtpHeaderExtensionCapability(
    public val uri: String,
    public val direction: RtpTransceiverDirection,
)

public class RtpCapabilities(
    public val codecs: List<RtpCodecCapability> = emptyList(),
    public val headerExtensions: List<RtpHeaderExtensionCapability> = emptyList(),
)
