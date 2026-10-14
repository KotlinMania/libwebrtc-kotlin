#ifndef WEBRTC_WRAPPER_H
#define WEBRTC_WRAPPER_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

/* ========================================================================== */
/* Android VM initialization (webrtc-sys/src/android.rs)                      */
/* ========================================================================== */

void webrtc_init_android(uint64_t jvm_handle);
int32_t webrtc_is_android_initialized(void);

/* ========================================================================== */
/* PeerConnectionFactory & PeerConnection (webrtc-sys)                        */
/* ========================================================================== */

uint64_t webrtc_create_peer_connection_factory(void);
void webrtc_release_peer_connection_factory(uint64_t factory_handle);

uint64_t webrtc_create_peer_connection(
    uint64_t factory_handle,
    int32_t ice_transport_type,
    int32_t continual_gathering_policy,
    int32_t ice_servers_count
);
void webrtc_close_peer_connection(uint64_t pc_handle);
int32_t webrtc_pc_connection_state(uint64_t pc_handle);
int32_t webrtc_pc_ice_connection_state(uint64_t pc_handle);
int32_t webrtc_pc_ice_gathering_state(uint64_t pc_handle);
int32_t webrtc_pc_signaling_state(uint64_t pc_handle);

const char* webrtc_pc_create_offer_sdp(
    uint64_t pc_handle,
    int32_t ice_restart,
    int32_t offer_audio,
    int32_t offer_video
);
const char* webrtc_pc_create_answer_sdp(
    uint64_t pc_handle,
    int32_t ice_restart,
    int32_t offer_audio,
    int32_t offer_video
);
int32_t webrtc_pc_set_local_description(uint64_t pc_handle, int32_t sdp_type, const char* sdp);
int32_t webrtc_pc_set_remote_description(uint64_t pc_handle, int32_t sdp_type, const char* sdp);
uint64_t webrtc_pc_remote_peer_handle(uint64_t pc_handle);
int32_t webrtc_pc_add_ice_candidate(
    uint64_t pc_handle,
    const char* sdp_mid,
    int32_t sdp_mline_index,
    const char* candidate
);
void webrtc_pc_restart_ice(uint64_t pc_handle);
const char* webrtc_pc_get_stats_json(uint64_t pc_handle);

/* ========================================================================== */
/* DataChannel (webrtc-sys/src/data_channel.rs)                               */
/* ========================================================================== */

uint64_t webrtc_create_data_channel(
    uint64_t pc_handle,
    const char* label,
    int32_t id,
    int32_t ordered,
    int32_t negotiated
);
int32_t webrtc_dc_state(uint64_t dc_handle);
uint64_t webrtc_dc_buffered_amount(uint64_t dc_handle);
int32_t webrtc_dc_send(uint64_t dc_handle, const uint8_t* data, int32_t len, int32_t binary);
void webrtc_dc_close(uint64_t dc_handle);

/* ========================================================================== */
/* Audio / Video Sources, Tracks, Senders, Receivers, Transceivers            */
/* ========================================================================== */

uint64_t webrtc_create_audio_source(
    int32_t sample_rate,
    int32_t num_channels,
    int32_t echo_cancellation,
    int32_t noise_suppression,
    int32_t auto_gain_control
);
int32_t webrtc_audio_source_capture_frame(
    uint64_t source_handle,
    const int16_t* samples,
    int32_t count,
    int32_t sample_rate,
    int32_t num_channels,
    int32_t samples_per_channel
);

uint64_t webrtc_create_video_source(int32_t width, int32_t height, int32_t is_screencast);
void webrtc_video_source_capture_frame(
    uint64_t source_handle,
    int32_t width,
    int32_t height,
    int64_t timestamp_us,
    int32_t rotation
);

uint64_t webrtc_create_audio_track(uint64_t factory_handle, const char* id, uint64_t source_handle);
uint64_t webrtc_create_video_track(uint64_t factory_handle, const char* id, uint64_t source_handle);
int32_t webrtc_track_enabled(uint64_t track_handle);
int32_t webrtc_track_set_enabled(uint64_t track_handle, int32_t enabled);
int32_t webrtc_track_state(uint64_t track_handle);
void webrtc_track_set_state(uint64_t track_handle, int32_t state);

uint64_t webrtc_create_rtp_sender(uint64_t pc_handle, uint64_t track_handle);
const char* webrtc_rtp_sender_get_stats_json(uint64_t sender_handle);

uint64_t webrtc_create_rtp_receiver(uint64_t pc_handle, uint64_t track_handle);
const char* webrtc_rtp_receiver_get_stats_json(uint64_t receiver_handle);

uint64_t webrtc_create_rtp_transceiver(uint64_t pc_handle, int32_t media_type, int32_t direction);
int32_t webrtc_rtp_transceiver_direction(uint64_t transceiver_handle);
void webrtc_rtp_transceiver_set_direction(uint64_t transceiver_handle, int32_t direction);
void webrtc_rtp_transceiver_stop(uint64_t transceiver_handle);

/* ========================================================================== */
/* AudioProcessingModule, AudioResampler (soxr-sys), AudioMixer               */
/* ========================================================================== */

uint64_t webrtc_apm_create(
    int32_t echo_canceller,
    int32_t gain_controller,
    int32_t high_pass_filter,
    int32_t noise_suppression
);
int32_t webrtc_apm_process_stream(
    uint64_t apm_handle,
    int16_t* data,
    int32_t len,
    int32_t sample_rate,
    int32_t num_channels
);
int32_t webrtc_apm_process_reverse_stream(
    uint64_t apm_handle,
    int16_t* data,
    int32_t len,
    int32_t sample_rate,
    int32_t num_channels
);
int32_t webrtc_apm_set_stream_delay_ms(uint64_t apm_handle, int32_t delay_ms);

int32_t webrtc_audio_resample(
    const int16_t* src,
    int32_t num_frames,
    int32_t num_channels,
    int32_t src_rate,
    int32_t dst_channels,
    int32_t dst_rate,
    int16_t* dst,
    int32_t dst_capacity
);

void webrtc_audio_mix_frames(
    const int16_t* src,
    int16_t* dst_accum,
    int32_t total_samples
);

/* ========================================================================== */
/* FrameCryptor / KeyProvider / DataPacketCryptor (frame_cryptor.rs)          */
/* ========================================================================== */

int32_t webrtc_cryptor_transform(
    int32_t algorithm,
    const uint8_t* key,
    int32_t key_len,
    const uint8_t* iv,
    int32_t iv_len,
    const uint8_t* input,
    int32_t input_len,
    uint8_t* output
);

void webrtc_ratchet_key(
    const uint8_t* current_key,
    int32_t key_len,
    const uint8_t* salt,
    int32_t salt_len,
    uint8_t* next_key
);

/* ========================================================================== */
/* DesktopCapturer (desktop_capturer.rs)                                      */
/* ========================================================================== */

int32_t webrtc_desktop_capturer_source_count(int32_t source_type);
uint64_t webrtc_desktop_capturer_source_id(int32_t source_type, int32_t index);
const char* webrtc_desktop_capturer_source_title(int32_t source_type, int32_t index);
int32_t webrtc_desktop_capturer_capture_rgba(
    uint64_t source_id,
    int32_t width,
    int32_t height,
    uint8_t* dst_rgba,
    int32_t dst_len
);

/* ========================================================================== */
/* YuvHelper (yuv-sys / libyuv)                                               */
/* ========================================================================== */

int32_t webrtc_yuv_i420_to_argb(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_argb, int32_t dst_stride_argb,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_i420_to_bgra(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_bgra, int32_t dst_stride_bgra,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_i420_to_abgr(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_abgr, int32_t dst_stride_abgr,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_i420_to_rgba(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_rgba, int32_t dst_stride_rgba,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_argb_to_i420(
    const uint8_t* src_argb, int32_t src_stride_argb,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_u, int32_t dst_stride_u,
    uint8_t* dst_v, int32_t dst_stride_v,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_abgr_to_i420(
    const uint8_t* src_abgr, int32_t src_stride_abgr,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_u, int32_t dst_stride_u,
    uint8_t* dst_v, int32_t dst_stride_v,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_argb_to_rgb24(
    const uint8_t* src_argb, int32_t src_stride_argb,
    uint8_t* dst_rgb24, int32_t dst_stride_rgb24,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_i420_to_nv12(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_uv, int32_t dst_stride_uv,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_nv12_to_i420(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_uv, int32_t src_stride_uv,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_u, int32_t dst_stride_u,
    uint8_t* dst_v, int32_t dst_stride_v,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_nv12_to_argb(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_uv, int32_t src_stride_uv,
    uint8_t* dst_argb, int32_t dst_stride_argb,
    int32_t width, int32_t height
);
int32_t webrtc_yuv_nv12_to_abgr(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_uv, int32_t src_stride_uv,
    uint8_t* dst_abgr, int32_t dst_stride_abgr,
    int32_t width, int32_t height
);

#ifdef __cplusplus
}
#endif

#endif /* WEBRTC_WRAPPER_H */
