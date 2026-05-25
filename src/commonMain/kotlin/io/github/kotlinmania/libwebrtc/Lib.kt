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

// Ledger for upstream lib.rs. The file in upstream Rust carries real
// declarations alongside a long list of module lines and target-gated
// re-export blocks; per workspace policy a Kotlin source file may not bridge
// re-exports via central typealiases, and a module root that mixes real code with
// module declarations is parceled into per-symbol Kotlin files. This file
// records the upstream order and the migration ledger; the implementation
// lives in the parceled files below.
//
// Parceled per-symbol files in this package share the lib.rs provenance:
//
//   MediaType     -> MediaType.kt
//   RtcErrorType  -> RtcErrorType.kt
//   RtcError      -> RtcError.kt
//
// Upstream selects the backing implementation by platform: WebAssembly uses
// the web implementation module, and all other targets use the native
// implementation module.
// In Kotlin Multiplatform this split is expressed as expect/actual per
// source set, not as a single inner module alias.
//
// Upstream submodule declarations follow in source order. Each will be ported
// as its own Kotlin file in this package, so the per-module declarations do
// not require a Kotlin counterpart of their own.
//   AudioFrame
//   AudioSource
//   AudioStream
//   AudioTrack
//   DataChannel
//   DesktopCapturer (macOS, Windows, and Linux)
//   IceCandidate
//   MediaStream
//   MediaStreamTrack
//   PeerConnection
//   PeerConnectionFactory
//   Prelude
//   RtpParameters
//   RtpReceiver
//   RtpSender
//   RtpTransceiver
//   SessionDescription
//   Stats
//   VideoFrame
//   VideoSource
//   VideoStream
//   VideoTrack
//
// Upstream non-wasm re-exports that surface native-only symbols:
//   NativeCreateRandomUuid
//   NativeApm
//   NativeAudioMixer
//   NativeAudioResampler
//   NativeFrameCryptor
//   NativeYuvHelper
// Per the libwebrtc-kotlin re-export workflow, these will be migrated to
// direct references at the call site (no central typealias). This file
// remains the ledger when callers begin to land.
//
// Upstream Android-only re-exports:
//   android implementation symbols
// Same migration rule: callers reference the original symbol; no central
// typealias.
//
// Callers migrated:
//   (none yet)
