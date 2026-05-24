// port-lint: source src/lib.rs
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

// Upstream selects the backing implementation by target arch:
//   #[cfg_attr(target_arch = "wasm32", path = "web/mod.rs")]
//   #[cfg_attr(not(target_arch = "wasm32"), path = "native/mod.rs")]
//   mod imp;
// In Kotlin Multiplatform this split is expressed as expect/actual per
// source set, not as a single inner module alias.

public enum class MediaType {
    Audio,
    Video,
    Data,
    Unsupported,
}

public enum class RtcErrorType {
    Internal,
    InvalidSdp,
    InvalidState,
}

// Upstream derives thiserror's #[error("an RtcError occured: {error_type:?} - {message}")].
// In Kotlin the formatted message is composed eagerly at construction time.
public class RtcError(
    public val errorType: RtcErrorType,
    public val rtcMessage: String,
) : RuntimeException("an RtcError occured: $errorType - $rtcMessage") {
    // Upstream Rust field is named `message`; Kotlin's Throwable already owns
    // a nullable `message`, so the upstream field is exposed under
    // `rtcMessage` to avoid shadowing the standard exception accessor while
    // still surfacing the raw, unformatted text.
}

// Upstream submodule declarations follow. Each will be ported as its own Kotlin file
// in this package, so the per-module `pub mod` lines below do not require a Kotlin
// counterpart.
//   pub mod audio_frame;
//   pub mod audio_source;
//   pub mod audio_stream;
//   pub mod audio_track;
//   pub mod data_channel;
//   #[cfg(any(target_os = "macos", target_os = "windows", target_os = "linux"))]
//   pub mod desktop_capturer;
//   pub mod ice_candidate;
//   pub mod media_stream;
//   pub mod media_stream_track;
//   pub mod peer_connection;
//   pub mod peer_connection_factory;
//   pub mod prelude;
//   pub mod rtp_parameters;
//   pub mod rtp_receiver;
//   pub mod rtp_sender;
//   pub mod rtp_transceiver;
//   pub mod session_description;
//   pub mod stats;
//   pub mod video_frame;
//   pub mod video_source;
//   pub mod video_stream;
//   pub mod video_track;

// Upstream non-wasm re-exports that surface native-only symbols:
//   #[cfg(not(target_arch = "wasm32"))]
//   pub mod native {
//       pub use webrtc_sys::webrtc::ffi::create_random_uuid;
//       pub use crate::imp::{apm, audio_mixer, audio_resampler, frame_cryptor, yuv_helper};
//   }
// Per the libwebrtc-kotlin re-export workflow, these will be migrated to direct
// references at the call site (no central typealias). This file remains the
// ledger when callers begin to land.

// Upstream Android-only re-exports:
//   #[cfg(target_os = "android")]
//   pub mod android {
//       pub use crate::imp::android::*;
//   }
// Same migration rule: callers reference the original symbol; no central typealias.
