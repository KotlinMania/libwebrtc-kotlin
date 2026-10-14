// port-lint: source rtp_sender.rs
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

import io.github.kotlinmania.libwebrtc.native.RtpSender as ImpRtpSender

public class RtpSender internal constructor(
    internal val handle: ImpRtpSender,
) {
    public constructor(
        currentTrack: MediaStreamTrack? = null,
        currentParameters: RtpParameters = RtpParameters(),
    ) : this(
        handle =
            ImpRtpSender(
                trackHandle = currentTrack?.sysHandle(),
                currentParameters = currentParameters,
            ),
    )

    public fun track(): MediaStreamTrack? = handle.track()

    public fun getStats(): List<RtcStats> = handle.getStats()

    public fun setTrack(track: MediaStreamTrack?) {
        handle.setTrack(track)
    }

    public fun parameters(): RtpParameters = handle.parameters()

    public fun setParameters(parameters: RtpParameters) {
        handle.setParameters(parameters)
    }

    override fun toString(): String =
        "RtpReceiver(cname=${parameters().rtcp.cname})"
}
