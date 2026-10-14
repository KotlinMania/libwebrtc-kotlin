#include <napi.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <vector>

#include "webrtc_wrapper.h"

namespace {

int hexNibble(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return c - 'a' + 10;
    if (c >= 'A' && c <= 'F') return c - 'A' + 10;
    return 0;
}

std::vector<uint8_t> decodeHexBytes(const std::string& hex) {
    size_t count = hex.size() / 2;
    std::vector<uint8_t> out(count);
    for (size_t i = 0; i < count; ++i) {
        int hi = hexNibble(hex[i * 2]);
        int lo = hexNibble(hex[i * 2 + 1]);
        out[i] = static_cast<uint8_t>((hi << 4) | lo);
    }
    return out;
}

std::string encodeHexBytes(const uint8_t* data, size_t len) {
    static const char kHex[] = "0123456789abcdef";
    std::string out(len * 2, '0');
    for (size_t i = 0; i < len; ++i) {
        uint8_t b = data[i];
        out[i * 2] = kHex[(b >> 4) & 0x0F];
        out[i * 2 + 1] = kHex[b & 0x0F];
    }
    return out;
}

std::vector<int16_t> decodeHexShorts(const std::string& hex) {
    size_t count = hex.size() / 4;
    std::vector<int16_t> out(count);
    for (size_t i = 0; i < count; ++i) {
        int n0 = hexNibble(hex[i * 4]);
        int n1 = hexNibble(hex[i * 4 + 1]);
        int n2 = hexNibble(hex[i * 4 + 2]);
        int n3 = hexNibble(hex[i * 4 + 3]);
        uint16_t u = static_cast<uint16_t>((n0 << 12) | (n1 << 8) | (n2 << 4) | n3);
        out[i] = static_cast<int16_t>(u);
    }
    return out;
}

std::string encodeHexShorts(const int16_t* data, size_t count) {
    static const char kHex[] = "0123456789abcdef";
    std::string out(count * 4, '0');
    for (size_t i = 0; i < count; ++i) {
        uint16_t u = static_cast<uint16_t>(data[i]);
        out[i * 4] = kHex[(u >> 12) & 0x0F];
        out[i * 4 + 1] = kHex[(u >> 8) & 0x0F];
        out[i * 4 + 2] = kHex[(u >> 4) & 0x0F];
        out[i * 4 + 3] = kHex[u & 0x0F];
    }
    return out;
}

Napi::Value InitAndroid(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    webrtc_init_android(h);
    return info.Env().Undefined();
}

Napi::Value IsAndroidInitialized(const Napi::CallbackInfo& info) {
    return Napi::Boolean::New(info.Env(), webrtc_is_android_initialized() != 0);
}

Napi::Value CreatePeerConnectionFactory(const Napi::CallbackInfo& info) {
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_create_peer_connection_factory()));
}

Napi::Value ReleasePeerConnectionFactory(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    webrtc_release_peer_connection_factory(h);
    return info.Env().Undefined();
}

Napi::Value CreatePeerConnection(const Napi::CallbackInfo& info) {
    uint64_t factory = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t iceType = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    int32_t policy = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 0;
    int32_t servers = info.Length() > 3 ? info[3].As<Napi::Number>().Int32Value() : 0;
    uint64_t h = webrtc_create_peer_connection(factory, iceType, policy, servers);
    return Napi::Number::New(info.Env(), static_cast<double>(h));
}

Napi::Value ClosePeerConnection(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    webrtc_close_peer_connection(h);
    return info.Env().Undefined();
}

Napi::Value PcConnectionState(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_pc_connection_state(h)));
}

Napi::Value PcIceConnectionState(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_pc_ice_connection_state(h)));
}

Napi::Value PcIceGatheringState(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_pc_ice_gathering_state(h)));
}

Napi::Value PcSignalingState(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_pc_signaling_state(h)));
}

Napi::Value PcCreateOfferSdp(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t iceRestart = (info.Length() > 1 && info[1].ToBoolean().Value()) ? 1 : 0;
    int32_t offerAudio = (info.Length() > 2 && info[2].ToBoolean().Value()) ? 1 : 0;
    int32_t offerVideo = (info.Length() > 3 && info[3].ToBoolean().Value()) ? 1 : 0;
    const char* s = webrtc_pc_create_offer_sdp(h, iceRestart, offerAudio, offerVideo);
    return Napi::String::New(info.Env(), s != nullptr ? s : "");
}

Napi::Value PcCreateAnswerSdp(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t iceRestart = (info.Length() > 1 && info[1].ToBoolean().Value()) ? 1 : 0;
    int32_t offerAudio = (info.Length() > 2 && info[2].ToBoolean().Value()) ? 1 : 0;
    int32_t offerVideo = (info.Length() > 3 && info[3].ToBoolean().Value()) ? 1 : 0;
    const char* s = webrtc_pc_create_answer_sdp(h, iceRestart, offerAudio, offerVideo);
    return Napi::String::New(info.Env(), s != nullptr ? s : "");
}

Napi::Value PcSetLocalDescription(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t sdpType = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    std::string sdp = (info.Length() > 2 && info[2].IsString()) ? info[2].As<Napi::String>().Utf8Value() : "";
    int32_t ok = webrtc_pc_set_local_description(h, sdpType, sdp.c_str());
    return Napi::Boolean::New(info.Env(), ok != 0);
}

Napi::Value PcSetRemoteDescription(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t sdpType = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    std::string sdp = (info.Length() > 2 && info[2].IsString()) ? info[2].As<Napi::String>().Utf8Value() : "";
    int32_t ok = webrtc_pc_set_remote_description(h, sdpType, sdp.c_str());
    return Napi::Boolean::New(info.Env(), ok != 0);
}

Napi::Value PcRemotePeerHandle(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_pc_remote_peer_handle(h)));
}

Napi::Value PcAddIceCandidate(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string mid = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    int32_t mline = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 0;
    std::string cand = (info.Length() > 3 && info[3].IsString()) ? info[3].As<Napi::String>().Utf8Value() : "";
    int32_t ok = webrtc_pc_add_ice_candidate(h, mid.c_str(), mline, cand.c_str());
    return Napi::Boolean::New(info.Env(), ok != 0);
}

Napi::Value PcRestartIce(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    webrtc_pc_restart_ice(h);
    return info.Env().Undefined();
}

Napi::Value PcGetStatsJson(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    const char* s = webrtc_pc_get_stats_json(h);
    return Napi::String::New(info.Env(), s != nullptr ? s : "[]");
}

Napi::Value CreateDataChannel(const Napi::CallbackInfo& info) {
    uint64_t pc = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string label = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    int32_t id = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 0;
    int32_t ordered = (info.Length() > 3 && info[3].ToBoolean().Value()) ? 1 : 0;
    int32_t negotiated = (info.Length() > 4 && info[4].ToBoolean().Value()) ? 1 : 0;
    uint64_t h = webrtc_create_data_channel(pc, label.c_str(), id, ordered, negotiated);
    return Napi::Number::New(info.Env(), static_cast<double>(h));
}

Napi::Value DcState(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_dc_state(h)));
}

Napi::Value DcBufferedAmount(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_dc_buffered_amount(h)));
}

Napi::Value DcSendHex(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string hex = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    int32_t binary = (info.Length() > 2 && info[2].ToBoolean().Value()) ? 1 : 0;
    std::vector<uint8_t> bytes = decodeHexBytes(hex);
    int32_t ok = webrtc_dc_send(h, bytes.empty() ? nullptr : bytes.data(), static_cast<int32_t>(bytes.size()), binary);
    return Napi::Boolean::New(info.Env(), ok != 0);
}

Napi::Value DcClose(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    webrtc_dc_close(h);
    return info.Env().Undefined();
}

Napi::Value CreateAudioSource(const Napi::CallbackInfo& info) {
    int32_t rate = info.Length() > 0 ? info[0].As<Napi::Number>().Int32Value() : 48000;
    int32_t ch = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 1;
    int32_t ec = (info.Length() > 2 && info[2].ToBoolean().Value()) ? 1 : 0;
    int32_t ns = (info.Length() > 3 && info[3].ToBoolean().Value()) ? 1 : 0;
    int32_t agc = (info.Length() > 4 && info[4].ToBoolean().Value()) ? 1 : 0;
    uint64_t h = webrtc_create_audio_source(rate, ch, ec, ns, agc);
    return Napi::Number::New(info.Env(), static_cast<double>(h));
}

Napi::Value AudioSourceCaptureFrameHex(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string hex = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    int32_t rate = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 48000;
    int32_t ch = info.Length() > 3 ? info[3].As<Napi::Number>().Int32Value() : 1;
    int32_t spc = info.Length() > 4 ? info[4].As<Napi::Number>().Int32Value() : 0;
    std::vector<int16_t> samples = decodeHexShorts(hex);
    int32_t ok = webrtc_audio_source_capture_frame(
        h,
        samples.empty() ? nullptr : samples.data(),
        static_cast<int32_t>(samples.size()),
        rate,
        ch,
        spc
    );
    return Napi::Boolean::New(info.Env(), ok != 0);
}

Napi::Value CreateVideoSource(const Napi::CallbackInfo& info) {
    int32_t w = info.Length() > 0 ? info[0].As<Napi::Number>().Int32Value() : 0;
    int32_t h = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    int32_t sc = (info.Length() > 2 && info[2].ToBoolean().Value()) ? 1 : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_create_video_source(w, h, sc)));
}

Napi::Value VideoSourceCaptureFrame(const Napi::CallbackInfo& info) {
    uint64_t src = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t w = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    int32_t h = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 0;
    int64_t ts = info.Length() > 3 ? info[3].As<Napi::Number>().Int64Value() : 0;
    int32_t rot = info.Length() > 4 ? info[4].As<Napi::Number>().Int32Value() : 0;
    webrtc_video_source_capture_frame(src, w, h, ts, rot);
    return info.Env().Undefined();
}

Napi::Value CreateAudioTrack(const Napi::CallbackInfo& info) {
    uint64_t factory = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string id = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    uint64_t src = info.Length() > 2 ? static_cast<uint64_t>(info[2].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_create_audio_track(factory, id.c_str(), src)));
}

Napi::Value CreateVideoTrack(const Napi::CallbackInfo& info) {
    uint64_t factory = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string id = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    uint64_t src = info.Length() > 2 ? static_cast<uint64_t>(info[2].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_create_video_track(factory, id.c_str(), src)));
}

Napi::Value TrackEnabled(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Boolean::New(info.Env(), webrtc_track_enabled(h) != 0);
}

Napi::Value TrackSetEnabled(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t en = (info.Length() > 1 && info[1].ToBoolean().Value()) ? 1 : 0;
    return Napi::Boolean::New(info.Env(), webrtc_track_set_enabled(h, en) != 0);
}

Napi::Value TrackState(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_track_state(h)));
}

Napi::Value TrackSetState(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t st = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    webrtc_track_set_state(h, st);
    return info.Env().Undefined();
}

Napi::Value CreateRtpSender(const Napi::CallbackInfo& info) {
    uint64_t pc = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    uint64_t tr = info.Length() > 1 ? static_cast<uint64_t>(info[1].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_create_rtp_sender(pc, tr)));
}

Napi::Value RtpSenderGetStatsJson(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    const char* s = webrtc_rtp_sender_get_stats_json(h);
    return Napi::String::New(info.Env(), s != nullptr ? s : "[]");
}

Napi::Value CreateRtpReceiver(const Napi::CallbackInfo& info) {
    uint64_t pc = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    uint64_t tr = info.Length() > 1 ? static_cast<uint64_t>(info[1].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_create_rtp_receiver(pc, tr)));
}

Napi::Value RtpReceiverGetStatsJson(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    const char* s = webrtc_rtp_receiver_get_stats_json(h);
    return Napi::String::New(info.Env(), s != nullptr ? s : "[]");
}

Napi::Value CreateRtpTransceiver(const Napi::CallbackInfo& info) {
    uint64_t pc = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t mt = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    int32_t dir = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_create_rtp_transceiver(pc, mt, dir)));
}

Napi::Value RtpTransceiverDirection(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_rtp_transceiver_direction(h)));
}

Napi::Value RtpTransceiverSetDirection(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t dir = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    webrtc_rtp_transceiver_set_direction(h, dir);
    return info.Env().Undefined();
}

Napi::Value RtpTransceiverStop(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    webrtc_rtp_transceiver_stop(h);
    return info.Env().Undefined();
}

Napi::Value ApmCreate(const Napi::CallbackInfo& info) {
    int32_t ec = (info.Length() > 0 && info[0].ToBoolean().Value()) ? 1 : 0;
    int32_t gc = (info.Length() > 1 && info[1].ToBoolean().Value()) ? 1 : 0;
    int32_t hpf = (info.Length() > 2 && info[2].ToBoolean().Value()) ? 1 : 0;
    int32_t ns = (info.Length() > 3 && info[3].ToBoolean().Value()) ? 1 : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_apm_create(ec, gc, hpf, ns)));
}

Napi::Value ApmProcessStreamHex(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string hex = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    int32_t rate = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 48000;
    int32_t ch = info.Length() > 3 ? info[3].As<Napi::Number>().Int32Value() : 1;
    std::vector<int16_t> samples = decodeHexShorts(hex);
    int32_t res = webrtc_apm_process_stream(
        h,
        samples.empty() ? nullptr : samples.data(),
        static_cast<int32_t>(samples.size()),
        rate,
        ch
    );
    std::string out = std::to_string(res) + ":" + encodeHexShorts(samples.data(), samples.size());
    return Napi::String::New(info.Env(), out);
}

Napi::Value ApmProcessReverseStreamHex(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    std::string hex = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    int32_t rate = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 48000;
    int32_t ch = info.Length() > 3 ? info[3].As<Napi::Number>().Int32Value() : 1;
    std::vector<int16_t> samples = decodeHexShorts(hex);
    int32_t res = webrtc_apm_process_reverse_stream(
        h,
        samples.empty() ? nullptr : samples.data(),
        static_cast<int32_t>(samples.size()),
        rate,
        ch
    );
    std::string out = std::to_string(res) + ":" + encodeHexShorts(samples.data(), samples.size());
    return Napi::String::New(info.Env(), out);
}

Napi::Value ApmSetStreamDelayMs(const Napi::CallbackInfo& info) {
    uint64_t h = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t delay = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_apm_set_stream_delay_ms(h, delay)));
}

Napi::Value AudioResampleHex(const Napi::CallbackInfo& info) {
    std::string srcHex = (info.Length() > 0 && info[0].IsString()) ? info[0].As<Napi::String>().Utf8Value() : "";
    int32_t numFrames = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    int32_t numChannels = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 1;
    int32_t srcRate = info.Length() > 3 ? info[3].As<Napi::Number>().Int32Value() : 48000;
    int32_t dstChannels = info.Length() > 4 ? info[4].As<Napi::Number>().Int32Value() : 1;
    int32_t dstRate = info.Length() > 5 ? info[5].As<Napi::Number>().Int32Value() : 48000;
    int32_t dstCap = info.Length() > 6 ? info[6].As<Napi::Number>().Int32Value() : 0;
    std::vector<int16_t> src = decodeHexShorts(srcHex);
    std::vector<int16_t> dst(static_cast<size_t>(dstCap > 0 ? dstCap : 1), 0);
    int32_t produced = webrtc_audio_resample(
        src.empty() ? nullptr : src.data(),
        numFrames,
        numChannels,
        srcRate,
        dstChannels,
        dstRate,
        dst.data(),
        dstCap
    );
    size_t outCount = produced > 0 ? static_cast<size_t>(produced * dstChannels) : 0;
    if (outCount > dst.size()) outCount = dst.size();
    std::string out = std::to_string(produced) + ":" + encodeHexShorts(dst.data(), outCount);
    return Napi::String::New(info.Env(), out);
}

Napi::Value AudioMixFramesHex(const Napi::CallbackInfo& info) {
    std::string srcHex = (info.Length() > 0 && info[0].IsString()) ? info[0].As<Napi::String>().Utf8Value() : "";
    std::string dstHex = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    int32_t total = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 0;
    std::vector<int16_t> src = decodeHexShorts(srcHex);
    std::vector<int16_t> dst = decodeHexShorts(dstHex);
    webrtc_audio_mix_frames(
        src.empty() ? nullptr : src.data(),
        dst.empty() ? nullptr : dst.data(),
        total
    );
    return Napi::String::New(info.Env(), encodeHexShorts(dst.data(), dst.size()));
}

Napi::Value CryptorTransformHex(const Napi::CallbackInfo& info) {
    int32_t alg = info.Length() > 0 ? info[0].As<Napi::Number>().Int32Value() : 0;
    std::string keyHex = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    std::string ivHex = (info.Length() > 2 && info[2].IsString()) ? info[2].As<Napi::String>().Utf8Value() : "";
    std::string inHex = (info.Length() > 3 && info[3].IsString()) ? info[3].As<Napi::String>().Utf8Value() : "";
    std::vector<uint8_t> key = decodeHexBytes(keyHex);
    std::vector<uint8_t> iv = decodeHexBytes(ivHex);
    std::vector<uint8_t> inBuf = decodeHexBytes(inHex);
    std::vector<uint8_t> outBuf(inBuf.empty() ? 1 : inBuf.size(), 0);
    int32_t outLen = webrtc_cryptor_transform(
        alg,
        key.empty() ? nullptr : key.data(),
        static_cast<int32_t>(key.size()),
        iv.empty() ? nullptr : iv.data(),
        static_cast<int32_t>(iv.size()),
        inBuf.empty() ? nullptr : inBuf.data(),
        static_cast<int32_t>(inBuf.size()),
        outBuf.data()
    );
    if (outLen <= 0) return Napi::String::New(info.Env(), "");
    return Napi::String::New(info.Env(), encodeHexBytes(outBuf.data(), static_cast<size_t>(outLen)));
}

Napi::Value RatchetKeyHex(const Napi::CallbackInfo& info) {
    std::string keyHex = (info.Length() > 0 && info[0].IsString()) ? info[0].As<Napi::String>().Utf8Value() : "";
    std::string saltHex = (info.Length() > 1 && info[1].IsString()) ? info[1].As<Napi::String>().Utf8Value() : "";
    std::vector<uint8_t> key = decodeHexBytes(keyHex);
    std::vector<uint8_t> salt = decodeHexBytes(saltHex);
    std::vector<uint8_t> nextKey(key.empty() ? 1 : key.size(), 0);
    webrtc_ratchet_key(
        key.empty() ? nullptr : key.data(),
        static_cast<int32_t>(key.size()),
        salt.empty() ? nullptr : salt.data(),
        static_cast<int32_t>(salt.size()),
        nextKey.data()
    );
    return Napi::String::New(info.Env(), encodeHexBytes(nextKey.data(), key.size()));
}

Napi::Value DesktopCapturerSourceCount(const Napi::CallbackInfo& info) {
    int32_t st = info.Length() > 0 ? info[0].As<Napi::Number>().Int32Value() : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_desktop_capturer_source_count(st)));
}

Napi::Value DesktopCapturerSourceId(const Napi::CallbackInfo& info) {
    int32_t st = info.Length() > 0 ? info[0].As<Napi::Number>().Int32Value() : 0;
    int32_t idx = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    return Napi::Number::New(info.Env(), static_cast<double>(webrtc_desktop_capturer_source_id(st, idx)));
}

Napi::Value DesktopCapturerSourceTitle(const Napi::CallbackInfo& info) {
    int32_t st = info.Length() > 0 ? info[0].As<Napi::Number>().Int32Value() : 0;
    int32_t idx = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    const char* s = webrtc_desktop_capturer_source_title(st, idx);
    return Napi::String::New(info.Env(), s != nullptr ? s : "");
}

Napi::Value DesktopCapturerCaptureRgbaHex(const Napi::CallbackInfo& info) {
    uint64_t id = info.Length() > 0 ? static_cast<uint64_t>(info[0].As<Napi::Number>().Int64Value()) : 0;
    int32_t w = info.Length() > 1 ? info[1].As<Napi::Number>().Int32Value() : 0;
    int32_t h = info.Length() > 2 ? info[2].As<Napi::Number>().Int32Value() : 0;
    int32_t len = w * h * 4;
    if (len <= 0) return Napi::String::New(info.Env(), "");
    std::vector<uint8_t> buf(static_cast<size_t>(len), 0);
    int32_t written = webrtc_desktop_capturer_capture_rgba(id, w, h, buf.data(), len);
    if (written <= 0) return Napi::String::New(info.Env(), "");
    return Napi::String::New(info.Env(), encodeHexBytes(buf.data(), static_cast<size_t>(written)));
}

#define DEFINE_NAPI_YUV_I420_TO_PACKED(fnName, cFunc) \
Napi::Value fnName(const Napi::CallbackInfo& info) { \
    std::vector<uint8_t> sy = decodeHexBytes(info[0].As<Napi::String>().Utf8Value()); \
    int32_t ssy = info[1].As<Napi::Number>().Int32Value(); \
    std::vector<uint8_t> su = decodeHexBytes(info[2].As<Napi::String>().Utf8Value()); \
    int32_t ssu = info[3].As<Napi::Number>().Int32Value(); \
    std::vector<uint8_t> sv = decodeHexBytes(info[4].As<Napi::String>().Utf8Value()); \
    int32_t ssv = info[5].As<Napi::Number>().Int32Value(); \
    int32_t dstLen = info[6].As<Napi::Number>().Int32Value(); \
    int32_t dstStride = info[7].As<Napi::Number>().Int32Value(); \
    int32_t w = info[8].As<Napi::Number>().Int32Value(); \
    int32_t h = info[9].As<Napi::Number>().Int32Value(); \
    std::vector<uint8_t> dst(static_cast<size_t>(dstLen > 0 ? dstLen : 1), 0); \
    cFunc(sy.data(), ssy, su.data(), ssu, sv.data(), ssv, dst.data(), dstStride, w, h); \
    return Napi::String::New(info.Env(), encodeHexBytes(dst.data(), static_cast<size_t>(dstLen))); \
}

DEFINE_NAPI_YUV_I420_TO_PACKED(YuvI420ToArgbHex, webrtc_yuv_i420_to_argb)
DEFINE_NAPI_YUV_I420_TO_PACKED(YuvI420ToBgraHex, webrtc_yuv_i420_to_bgra)
DEFINE_NAPI_YUV_I420_TO_PACKED(YuvI420ToAbgrHex, webrtc_yuv_i420_to_abgr)
DEFINE_NAPI_YUV_I420_TO_PACKED(YuvI420ToRgbaHex, webrtc_yuv_i420_to_rgba)

#define DEFINE_NAPI_YUV_PACKED_TO_I420(fnName, cFunc) \
Napi::Value fnName(const Napi::CallbackInfo& info) { \
    std::vector<uint8_t> src = decodeHexBytes(info[0].As<Napi::String>().Utf8Value()); \
    int32_t srcStride = info[1].As<Napi::Number>().Int32Value(); \
    int32_t yLen = info[2].As<Napi::Number>().Int32Value(); \
    int32_t dsy = info[3].As<Napi::Number>().Int32Value(); \
    int32_t uLen = info[4].As<Napi::Number>().Int32Value(); \
    int32_t dsu = info[5].As<Napi::Number>().Int32Value(); \
    int32_t vLen = info[6].As<Napi::Number>().Int32Value(); \
    int32_t dsv = info[7].As<Napi::Number>().Int32Value(); \
    int32_t w = info[8].As<Napi::Number>().Int32Value(); \
    int32_t h = info[9].As<Napi::Number>().Int32Value(); \
    std::vector<uint8_t> dy(static_cast<size_t>(yLen > 0 ? yLen : 1), 0); \
    std::vector<uint8_t> du(static_cast<size_t>(uLen > 0 ? uLen : 1), 0); \
    std::vector<uint8_t> dv(static_cast<size_t>(vLen > 0 ? vLen : 1), 0); \
    cFunc(src.data(), srcStride, dy.data(), dsy, du.data(), dsu, dv.data(), dsv, w, h); \
    std::string out = encodeHexBytes(dy.data(), static_cast<size_t>(yLen)) + ":" + \
                      encodeHexBytes(du.data(), static_cast<size_t>(uLen)) + ":" + \
                      encodeHexBytes(dv.data(), static_cast<size_t>(vLen)); \
    return Napi::String::New(info.Env(), out); \
}

DEFINE_NAPI_YUV_PACKED_TO_I420(YuvArgbToI420Hex, webrtc_yuv_argb_to_i420)
DEFINE_NAPI_YUV_PACKED_TO_I420(YuvAbgrToI420Hex, webrtc_yuv_abgr_to_i420)

Napi::Value YuvArgbToRgb24Hex(const Napi::CallbackInfo& info) {
    std::vector<uint8_t> src = decodeHexBytes(info[0].As<Napi::String>().Utf8Value());
    int32_t srcStride = info[1].As<Napi::Number>().Int32Value();
    int32_t dstLen = info[2].As<Napi::Number>().Int32Value();
    int32_t dstStride = info[3].As<Napi::Number>().Int32Value();
    int32_t w = info[4].As<Napi::Number>().Int32Value();
    int32_t h = info[5].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> dst(static_cast<size_t>(dstLen > 0 ? dstLen : 1), 0);
    webrtc_yuv_argb_to_rgb24(src.data(), srcStride, dst.data(), dstStride, w, h);
    return Napi::String::New(info.Env(), encodeHexBytes(dst.data(), static_cast<size_t>(dstLen)));
}

Napi::Value YuvI420ToNv12Hex(const Napi::CallbackInfo& info) {
    std::vector<uint8_t> sy = decodeHexBytes(info[0].As<Napi::String>().Utf8Value());
    int32_t ssy = info[1].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> su = decodeHexBytes(info[2].As<Napi::String>().Utf8Value());
    int32_t ssu = info[3].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> sv = decodeHexBytes(info[4].As<Napi::String>().Utf8Value());
    int32_t ssv = info[5].As<Napi::Number>().Int32Value();
    int32_t yLen = info[6].As<Napi::Number>().Int32Value();
    int32_t dsy = info[7].As<Napi::Number>().Int32Value();
    int32_t uvLen = info[8].As<Napi::Number>().Int32Value();
    int32_t dsuv = info[9].As<Napi::Number>().Int32Value();
    int32_t w = info[10].As<Napi::Number>().Int32Value();
    int32_t h = info[11].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> dy(static_cast<size_t>(yLen > 0 ? yLen : 1), 0);
    std::vector<uint8_t> duv(static_cast<size_t>(uvLen > 0 ? uvLen : 1), 0);
    webrtc_yuv_i420_to_nv12(sy.data(), ssy, su.data(), ssu, sv.data(), ssv, dy.data(), dsy, duv.data(), dsuv, w, h);
    std::string out = encodeHexBytes(dy.data(), static_cast<size_t>(yLen)) + ":" +
                      encodeHexBytes(duv.data(), static_cast<size_t>(uvLen));
    return Napi::String::New(info.Env(), out);
}

Napi::Value YuvNv12ToI420Hex(const Napi::CallbackInfo& info) {
    std::vector<uint8_t> sy = decodeHexBytes(info[0].As<Napi::String>().Utf8Value());
    int32_t ssy = info[1].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> suv = decodeHexBytes(info[2].As<Napi::String>().Utf8Value());
    int32_t ssuv = info[3].As<Napi::Number>().Int32Value();
    int32_t yLen = info[4].As<Napi::Number>().Int32Value();
    int32_t dsy = info[5].As<Napi::Number>().Int32Value();
    int32_t uLen = info[6].As<Napi::Number>().Int32Value();
    int32_t dsu = info[7].As<Napi::Number>().Int32Value();
    int32_t vLen = info[8].As<Napi::Number>().Int32Value();
    int32_t dsv = info[9].As<Napi::Number>().Int32Value();
    int32_t w = info[10].As<Napi::Number>().Int32Value();
    int32_t h = info[11].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> dy(static_cast<size_t>(yLen > 0 ? yLen : 1), 0);
    std::vector<uint8_t> du(static_cast<size_t>(uLen > 0 ? uLen : 1), 0);
    std::vector<uint8_t> dv(static_cast<size_t>(vLen > 0 ? vLen : 1), 0);
    webrtc_yuv_nv12_to_i420(sy.data(), ssy, suv.data(), ssuv, dy.data(), dsy, du.data(), dsu, dv.data(), dsv, w, h);
    std::string out = encodeHexBytes(dy.data(), static_cast<size_t>(yLen)) + ":" +
                      encodeHexBytes(du.data(), static_cast<size_t>(uLen)) + ":" +
                      encodeHexBytes(dv.data(), static_cast<size_t>(vLen));
    return Napi::String::New(info.Env(), out);
}

Napi::Value YuvNv12ToArgbHex(const Napi::CallbackInfo& info) {
    std::vector<uint8_t> sy = decodeHexBytes(info[0].As<Napi::String>().Utf8Value());
    int32_t ssy = info[1].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> suv = decodeHexBytes(info[2].As<Napi::String>().Utf8Value());
    int32_t ssuv = info[3].As<Napi::Number>().Int32Value();
    int32_t dstLen = info[4].As<Napi::Number>().Int32Value();
    int32_t dstStride = info[5].As<Napi::Number>().Int32Value();
    int32_t w = info[6].As<Napi::Number>().Int32Value();
    int32_t h = info[7].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> dst(static_cast<size_t>(dstLen > 0 ? dstLen : 1), 0);
    webrtc_yuv_nv12_to_argb(sy.data(), ssy, suv.data(), ssuv, dst.data(), dstStride, w, h);
    return Napi::String::New(info.Env(), encodeHexBytes(dst.data(), static_cast<size_t>(dstLen)));
}

Napi::Value YuvNv12ToAbgrHex(const Napi::CallbackInfo& info) {
    std::vector<uint8_t> sy = decodeHexBytes(info[0].As<Napi::String>().Utf8Value());
    int32_t ssy = info[1].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> suv = decodeHexBytes(info[2].As<Napi::String>().Utf8Value());
    int32_t ssuv = info[3].As<Napi::Number>().Int32Value();
    int32_t dstLen = info[4].As<Napi::Number>().Int32Value();
    int32_t dstStride = info[5].As<Napi::Number>().Int32Value();
    int32_t w = info[6].As<Napi::Number>().Int32Value();
    int32_t h = info[7].As<Napi::Number>().Int32Value();
    std::vector<uint8_t> dst(static_cast<size_t>(dstLen > 0 ? dstLen : 1), 0);
    webrtc_yuv_nv12_to_abgr(sy.data(), ssy, suv.data(), ssuv, dst.data(), dstStride, w, h);
    return Napi::String::New(info.Env(), encodeHexBytes(dst.data(), static_cast<size_t>(dstLen)));
}

}  // namespace

Napi::Object Init(Napi::Env env, Napi::Object exports) {
    exports.Set("initAndroid", Napi::Function::New(env, InitAndroid));
    exports.Set("isAndroidInitialized", Napi::Function::New(env, IsAndroidInitialized));
    exports.Set("createPeerConnectionFactory", Napi::Function::New(env, CreatePeerConnectionFactory));
    exports.Set("releasePeerConnectionFactory", Napi::Function::New(env, ReleasePeerConnectionFactory));
    exports.Set("createPeerConnection", Napi::Function::New(env, CreatePeerConnection));
    exports.Set("closePeerConnection", Napi::Function::New(env, ClosePeerConnection));
    exports.Set("pcConnectionState", Napi::Function::New(env, PcConnectionState));
    exports.Set("pcIceConnectionState", Napi::Function::New(env, PcIceConnectionState));
    exports.Set("pcIceGatheringState", Napi::Function::New(env, PcIceGatheringState));
    exports.Set("pcSignalingState", Napi::Function::New(env, PcSignalingState));
    exports.Set("pcCreateOfferSdp", Napi::Function::New(env, PcCreateOfferSdp));
    exports.Set("pcCreateAnswerSdp", Napi::Function::New(env, PcCreateAnswerSdp));
    exports.Set("pcSetLocalDescription", Napi::Function::New(env, PcSetLocalDescription));
    exports.Set("pcSetRemoteDescription", Napi::Function::New(env, PcSetRemoteDescription));
    exports.Set("pcRemotePeerHandle", Napi::Function::New(env, PcRemotePeerHandle));
    exports.Set("pcAddIceCandidate", Napi::Function::New(env, PcAddIceCandidate));
    exports.Set("pcRestartIce", Napi::Function::New(env, PcRestartIce));
    exports.Set("pcGetStatsJson", Napi::Function::New(env, PcGetStatsJson));
    exports.Set("createDataChannel", Napi::Function::New(env, CreateDataChannel));
    exports.Set("dcState", Napi::Function::New(env, DcState));
    exports.Set("dcBufferedAmount", Napi::Function::New(env, DcBufferedAmount));
    exports.Set("dcSendHex", Napi::Function::New(env, DcSendHex));
    exports.Set("dcClose", Napi::Function::New(env, DcClose));
    exports.Set("createAudioSource", Napi::Function::New(env, CreateAudioSource));
    exports.Set("audioSourceCaptureFrameHex", Napi::Function::New(env, AudioSourceCaptureFrameHex));
    exports.Set("createVideoSource", Napi::Function::New(env, CreateVideoSource));
    exports.Set("videoSourceCaptureFrame", Napi::Function::New(env, VideoSourceCaptureFrame));
    exports.Set("createAudioTrack", Napi::Function::New(env, CreateAudioTrack));
    exports.Set("createVideoTrack", Napi::Function::New(env, CreateVideoTrack));
    exports.Set("trackEnabled", Napi::Function::New(env, TrackEnabled));
    exports.Set("trackSetEnabled", Napi::Function::New(env, TrackSetEnabled));
    exports.Set("trackState", Napi::Function::New(env, TrackState));
    exports.Set("trackSetState", Napi::Function::New(env, TrackSetState));
    exports.Set("createRtpSender", Napi::Function::New(env, CreateRtpSender));
    exports.Set("rtpSenderGetStatsJson", Napi::Function::New(env, RtpSenderGetStatsJson));
    exports.Set("createRtpReceiver", Napi::Function::New(env, CreateRtpReceiver));
    exports.Set("rtpReceiverGetStatsJson", Napi::Function::New(env, RtpReceiverGetStatsJson));
    exports.Set("createRtpTransceiver", Napi::Function::New(env, CreateRtpTransceiver));
    exports.Set("rtpTransceiverDirection", Napi::Function::New(env, RtpTransceiverDirection));
    exports.Set("rtpTransceiverSetDirection", Napi::Function::New(env, RtpTransceiverSetDirection));
    exports.Set("rtpTransceiverStop", Napi::Function::New(env, RtpTransceiverStop));
    exports.Set("apmCreate", Napi::Function::New(env, ApmCreate));
    exports.Set("apmProcessStreamHex", Napi::Function::New(env, ApmProcessStreamHex));
    exports.Set("apmProcessReverseStreamHex", Napi::Function::New(env, ApmProcessReverseStreamHex));
    exports.Set("apmSetStreamDelayMs", Napi::Function::New(env, ApmSetStreamDelayMs));
    exports.Set("audioResampleHex", Napi::Function::New(env, AudioResampleHex));
    exports.Set("audioMixFramesHex", Napi::Function::New(env, AudioMixFramesHex));
    exports.Set("cryptorTransformHex", Napi::Function::New(env, CryptorTransformHex));
    exports.Set("ratchetKeyHex", Napi::Function::New(env, RatchetKeyHex));
    exports.Set("desktopCapturerSourceCount", Napi::Function::New(env, DesktopCapturerSourceCount));
    exports.Set("desktopCapturerSourceId", Napi::Function::New(env, DesktopCapturerSourceId));
    exports.Set("desktopCapturerSourceTitle", Napi::Function::New(env, DesktopCapturerSourceTitle));
    exports.Set("desktopCapturerCaptureRgbaHex", Napi::Function::New(env, DesktopCapturerCaptureRgbaHex));
    exports.Set("yuvI420ToArgbHex", Napi::Function::New(env, YuvI420ToArgbHex));
    exports.Set("yuvI420ToBgraHex", Napi::Function::New(env, YuvI420ToBgraHex));
    exports.Set("yuvI420ToAbgrHex", Napi::Function::New(env, YuvI420ToAbgrHex));
    exports.Set("yuvI420ToRgbaHex", Napi::Function::New(env, YuvI420ToRgbaHex));
    exports.Set("yuvArgbToI420Hex", Napi::Function::New(env, YuvArgbToI420Hex));
    exports.Set("yuvAbgrToI420Hex", Napi::Function::New(env, YuvAbgrToI420Hex));
    exports.Set("yuvArgbToRgb24Hex", Napi::Function::New(env, YuvArgbToRgb24Hex));
    exports.Set("yuvI420ToNv12Hex", Napi::Function::New(env, YuvI420ToNv12Hex));
    exports.Set("yuvNv12ToI420Hex", Napi::Function::New(env, YuvNv12ToI420Hex));
    exports.Set("yuvNv12ToArgbHex", Napi::Function::New(env, YuvNv12ToArgbHex));
    exports.Set("yuvNv12ToAbgrHex", Napi::Function::New(env, YuvNv12ToAbgrHex));
    return exports;
}

NODE_API_MODULE(webrtc_native, Init)
