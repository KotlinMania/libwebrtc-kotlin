// port-lint: source libwebrtc/libwebrtc/src/audio_track.rs
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

public class RtcAudioTrack(
    override val id: String,
    private var isTrackEnabled: Boolean = true,
    override var state: RtcTrackState = RtcTrackState.Live,
) : MediaStreamTrack {
    override val kind: String get() = "audio"
    override val enabled: Boolean get() = isTrackEnabled

    override fun setEnabled(enabled: Boolean): Boolean {
        isTrackEnabled = enabled
        return isTrackEnabled
    }
}
