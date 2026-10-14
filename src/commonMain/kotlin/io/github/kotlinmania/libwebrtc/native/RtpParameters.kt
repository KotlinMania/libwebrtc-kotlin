// port-lint: source native/rtp_parameters.rs
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
import io.github.kotlinmania.libwebrtc.Priority
import io.github.kotlinmania.libwebrtc.RtcpParameters
import io.github.kotlinmania.libwebrtc.RtpCapabilities
import io.github.kotlinmania.libwebrtc.RtpCodecCapability
import io.github.kotlinmania.libwebrtc.RtpCodecParameters
import io.github.kotlinmania.libwebrtc.RtpEncodingParameters
import io.github.kotlinmania.libwebrtc.RtpHeaderExtensionCapability
import io.github.kotlinmania.libwebrtc.RtpHeaderExtensionParameters
import io.github.kotlinmania.libwebrtc.RtpParameters
import io.github.kotlinmania.libwebrtc.RtpTransceiverDirection

internal const val DEFAULT_BITRATE_PRIORITY: Double = 1.0

internal class StringKeyValue(
    internal val key: String,
    internal val value: String,
)

internal class NativeRtpExtension(
    internal val uri: String,
    internal val id: Int,
    internal val encrypt: Boolean,
) {
    internal fun toRtpHeaderExtensionParameters(): RtpHeaderExtensionParameters =
        RtpHeaderExtensionParameters(uri = uri, id = id, encrypted = encrypt)

    internal companion object {
        internal fun from(value: RtpHeaderExtensionParameters): NativeRtpExtension =
            NativeRtpExtension(uri = value.uri, id = value.id, encrypt = value.encrypted)
    }
}

internal class NativeRtcpParameters(
    internal val cname: String = "",
    internal val reducedSize: Boolean = false,
    internal val hasSsrc: Boolean = false,
    internal val ssrc: Int = 0,
    internal val mux: Boolean = false,
) {
    internal fun toRtcpParameters(): RtcpParameters =
        RtcpParameters(cname = cname, reducedSize = reducedSize)

    internal companion object {
        internal fun from(value: RtcpParameters): NativeRtcpParameters =
            NativeRtcpParameters(cname = value.cname, reducedSize = value.reducedSize)
    }
}

internal class NativeRtpCodecParameters(
    internal val payloadType: Int = 0,
    internal val mimeType: String = "",
    internal val hasClockRate: Boolean = false,
    internal val clockRate: Int = 0,
    internal val hasNumChannels: Boolean = false,
    internal val numChannels: Int = 0,
    internal val name: String = "",
    internal val kind: MediaType = MediaType.Audio,
) {
    internal fun toRtpCodecParameters(): RtpCodecParameters =
        RtpCodecParameters(
            payloadType = payloadType.toUByte(),
            mimeType = mimeType,
            clockRate = if (hasClockRate) clockRate.toULong() else null,
            channels = if (hasNumChannels) numChannels.toUShort() else null,
        )

    internal companion object {
        internal fun from(value: RtpCodecParameters): NativeRtpCodecParameters =
            NativeRtpCodecParameters(
                payloadType = value.payloadType.toInt(),
                mimeType = value.mimeType,
                hasClockRate = value.clockRate != null,
                clockRate = value.clockRate?.toInt() ?: 0,
                hasNumChannels = value.channels != null,
                numChannels = value.channels?.toInt() ?: 0,
            )
    }
}

internal class NativeRtpEncodingParameters(
    internal val active: Boolean = true,
    internal val hasMaxBitrateBps: Boolean = false,
    internal val maxBitrateBps: Int = 0,
    internal val hasMaxFramerate: Boolean = false,
    internal val maxFramerate: Double = 0.0,
    internal val networkPriority: Priority = Priority.Low,
    internal val rid: String = "",
    internal val hasScaleResolutionDownBy: Boolean = false,
    internal val scaleResolutionDownBy: Double = 0.0,
    internal val bitratePriority: Double = DEFAULT_BITRATE_PRIORITY,
) {
    internal fun toRtpEncodingParameters(): RtpEncodingParameters =
        RtpEncodingParameters(
            active = active,
            maxBitrate = if (hasMaxBitrateBps) maxBitrateBps.toULong() else null,
            maxFramerate = if (hasMaxFramerate) maxFramerate else null,
            priority = networkPriority,
            rid = rid,
            scaleResolutionDownBy = if (hasScaleResolutionDownBy) scaleResolutionDownBy else null,
        )

    internal companion object {
        internal fun from(value: RtpEncodingParameters): NativeRtpEncodingParameters =
            NativeRtpEncodingParameters(
                active = value.active,
                hasMaxBitrateBps = value.maxBitrate != null,
                maxBitrateBps = value.maxBitrate?.toInt() ?: 0,
                hasMaxFramerate = value.maxFramerate != null,
                maxFramerate = value.maxFramerate ?: 0.0,
                networkPriority = value.priority,
                rid = value.rid,
                hasScaleResolutionDownBy = value.scaleResolutionDownBy != null,
                scaleResolutionDownBy = value.scaleResolutionDownBy ?: 0.0,
            )
    }
}

internal class NativeRtpCodecCapability(
    internal val name: String,
    internal val kind: MediaType,
    internal val mimeType: String,
    internal val hasClockRate: Boolean,
    internal val clockRate: Int,
    internal val hasNumChannels: Boolean,
    internal val numChannels: Int,
    internal val parameters: List<StringKeyValue> = emptyList(),
) {
    internal fun toRtpCodecCapability(): RtpCodecCapability {
        val formattedParams =
            parameters.map { kv ->
                if (kv.key.isNotEmpty()) "${kv.key}=${kv.value}" else kv.value
            }
        val resolvedMime =
            if (mimeType.isNotEmpty()) {
                mimeType
            } else {
                val prefix = if (kind == MediaType.Video) "video" else "audio"
                "$prefix/$name"
            }
        return RtpCodecCapability(
            mimeType = resolvedMime,
            clockRate = if (hasClockRate) clockRate.toULong() else null,
            channels = if (hasNumChannels) numChannels.toUShort() else null,
            sdpFmtpLine = if (formattedParams.isNotEmpty()) formattedParams.joinToString(";") else null,
        )
    }

    internal companion object {
        internal fun from(value: RtpCodecCapability): NativeRtpCodecCapability {
            val parts = value.mimeType.split('/')
            require(parts.size == 2) { "invalid media type" }
            val kind =
                when (parts[0]) {
                    "audio" -> MediaType.Audio
                    "video" -> MediaType.Video
                    else -> error("invalid media type")
                }
            val name = parts[1]
            val parameters =
                value.sdpFmtpLine
                    ?.takeIf { it.isNotEmpty() }
                    ?.split(';')
                    ?.map { entry ->
                        val kv = entry.split('=')
                        if (kv.size == 2) {
                            StringKeyValue(key = kv[0], value = kv[1])
                        } else {
                            StringKeyValue(key = "", value = kv[0])
                        }
                    }.orEmpty()
            return NativeRtpCodecCapability(
                name = name,
                kind = kind,
                mimeType = value.mimeType,
                hasClockRate = value.clockRate != null,
                clockRate = value.clockRate?.toInt() ?: 0,
                hasNumChannels = value.channels != null,
                numChannels = value.channels?.toInt() ?: 0,
                parameters = parameters,
            )
        }
    }
}

internal class NativeRtpHeaderExtensionCapability(
    internal val uri: String,
    internal val direction: RtpTransceiverDirection,
) {
    internal fun toRtpHeaderExtensionCapability(): RtpHeaderExtensionCapability =
        RtpHeaderExtensionCapability(uri = uri, direction = direction)
}

internal class NativeRtpCapabilities(
    internal val codecs: List<NativeRtpCodecCapability> = emptyList(),
    internal val headerExtensions: List<NativeRtpHeaderExtensionCapability> = emptyList(),
) {
    internal fun toRtpCapabilities(): RtpCapabilities =
        RtpCapabilities(
            codecs = codecs.map { it.toRtpCodecCapability() },
            headerExtensions = headerExtensions.map { it.toRtpHeaderExtensionCapability() },
        )
}

internal class NativeRtpParameters(
    internal val codecs: List<NativeRtpCodecParameters> = emptyList(),
    internal val headerExtensions: List<NativeRtpExtension> = emptyList(),
    internal val encodings: List<NativeRtpEncodingParameters> = emptyList(),
    internal val rtcp: NativeRtcpParameters = NativeRtcpParameters(),
) {
    internal fun toRtpParameters(): RtpParameters =
        RtpParameters(
            codecs = codecs.map { it.toRtpCodecParameters() },
            headerExtensions = headerExtensions.map { it.toRtpHeaderExtensionParameters() },
            rtcp = rtcp.toRtcpParameters(),
        )

    internal companion object {
        internal fun from(value: RtpParameters): NativeRtpParameters =
            NativeRtpParameters(
                codecs = value.codecs.map { NativeRtpCodecParameters.from(it) },
                headerExtensions = value.headerExtensions.map { NativeRtpExtension.from(it) },
                encodings = emptyList(),
                rtcp = NativeRtcpParameters.from(value.rtcp),
            )
    }
}
