// port-lint: source prelude.rs
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

// Tracking file for upstream prelude.rs.
//
// Upstream re-exports:
//   AudioFrame -> AudioFrame.kt
//   AudioSourceOptions, RtcAudioSource -> AudioSource.kt
//   RtcAudioTrack -> AudioTrack.kt
//   DataBuffer, DataChannel, DataChannelError, DataChannelInit, DataChannelState -> DataChannel.kt
//   IceCandidate -> IceCandidate.kt
//   MediaStream -> MediaStream.kt
//   MediaStreamTrack, RtcTrackState -> MediaStreamTrack.kt
//   AnswerOptions, IceConnectionState, IceGatheringState, OfferOptions, PeerConnection,
//     PeerConnectionState, SignalingState -> PeerConnection.kt
//   ContinualGatheringPolicy, IceServer, IceTransportsType, PeerConnectionFactory,
//     RtcConfiguration -> PeerConnectionFactory.kt
//   RtpParameters, RtpEncodingParameters, RtpCodecParameters, RtpHeaderExtensionParameters,
//     RtcpParameters, Priority, DegradationPreference -> RtpParameters.kt
//   RtpReceiver -> RtpReceiver.kt
//   RtpSender -> RtpSender.kt
//   RtpTransceiver, RtpTransceiverDirection, RtpTransceiverInit -> RtpTransceiver.kt
//   SdpType, SessionDescription -> SessionDescription.kt
//   BoxVideoBuffer, BoxVideoFrame, I010Buffer, I420ABuffer, I420Buffer, I422Buffer, I444Buffer,
//     NV12Buffer, VideoBuffer, VideoBufferType, VideoFormatType, VideoFrame, VideoRotation -> VideoFrame.kt
//   RtcVideoSource, VideoResolution -> VideoSource.kt
//   RtcVideoTrack -> VideoTrack.kt
//   MediaType -> MediaType.kt
//   RtcError -> RtcError.kt
//   RtcErrorType -> RtcErrorType.kt
//
// Callers migrated:
//   (none)
