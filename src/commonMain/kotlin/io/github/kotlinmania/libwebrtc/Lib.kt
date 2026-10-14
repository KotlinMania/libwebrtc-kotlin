// port-lint: source lib.rs
package io.github.kotlinmania.libwebrtc

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

// Tracking file for upstream lib.rs. The file in upstream Rust carries real
// declarations alongside module declarations and target-gated re-export blocks;
// per workspace policy a Kotlin source file may not bridge re-exports via
// central typealiases, and a module root that mixes real code with module
// declarations is parceled into per-symbol Kotlin files. This file records
// the upstream order and the migration ledger; the implementation lives in
// the parceled files below.
//
// Parceled per-symbol files in this package share the lib.rs provenance:
//   MediaType     -> MediaType.kt
//   RtcErrorType  -> RtcErrorType.kt
//   RtcError      -> RtcError.kt
//
// Upstream submodule declarations in source order:
//   AudioFrame            -> AudioFrame.kt
//   AudioSource           -> AudioSource.kt
//   AudioStream           -> AudioStream.kt
//   AudioTrack            -> AudioTrack.kt
//   DataChannel           -> DataChannel.kt
//   DesktopCapturer       -> DesktopCapturer.kt
//   IceCandidate          -> IceCandidate.kt
//   MediaStream           -> MediaStream.kt
//   MediaStreamTrack      -> MediaStreamTrack.kt
//   PeerConnection        -> PeerConnection.kt
//   PeerConnectionFactory -> PeerConnectionFactory.kt
//   Prelude               -> Prelude.kt
//   RtpParameters         -> RtpParameters.kt
//   RtpReceiver           -> RtpReceiver.kt
//   RtpSender             -> RtpSender.kt
//   RtpTransceiver        -> RtpTransceiver.kt
//   SessionDescription    -> SessionDescription.kt
//   Stats                 -> Stats.kt
//   VideoFrame            -> VideoFrame.kt
//   VideoSource           -> VideoSource.kt
//   VideoStream           -> VideoStream.kt
//   VideoTrack            -> VideoTrack.kt
//
// Upstream native module re-exports:
//   native/Apm.kt
//   native/AudioMixer.kt
//   native/AudioResampler.kt
//   native/FrameCryptor.kt
//   native/YuvHelper.kt
//   native/Android.kt
//
// Callers migrated:
//   (none)
