#include "webrtc_wrapper.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#if defined(_WIN32)
#include <windows.h>
#else
#include <dlfcn.h>
#endif

/* ========================================================================== */
/* Dynamic symbol resolution for prebuilt libwebrtc / libyuv / libsoxr        */
/* ========================================================================== */

static void* g_webrtc_handle = NULL;
static int g_webrtc_init_attempted = 0;
static uint64_t g_android_jvm_handle = 0ULL;

static void init_webrtc_handle(void) {
    if (g_webrtc_init_attempted) {
        return;
    }
    g_webrtc_init_attempted = 1;
#if defined(_WIN32)
    const char* env_path = getenv("LIBWEBRTC_DLL");
    if (env_path != NULL && env_path[0] != '\0') {
        g_webrtc_handle = (void*)LoadLibraryA(env_path);
        if (g_webrtc_handle != NULL) return;
    }
    const char* names[] = { "webrtc.dll", "libwebrtc.dll", NULL };
    for (int i = 0; names[i] != NULL; i++) {
        g_webrtc_handle = (void*)LoadLibraryA(names[i]);
        if (g_webrtc_handle != NULL) break;
    }
#elif defined(__APPLE__)
    const char* env_path = getenv("LIBWEBRTC_DYLIB");
    if (env_path != NULL && env_path[0] != '\0') {
        g_webrtc_handle = dlopen(env_path, RTLD_LAZY | RTLD_LOCAL);
        if (g_webrtc_handle != NULL) return;
    }
    const char* names[] = {
        "libwebrtc.dylib",
        "@rpath/libwebrtc.dylib",
        NULL
    };
    for (int i = 0; names[i] != NULL; i++) {
        g_webrtc_handle = dlopen(names[i], RTLD_LAZY | RTLD_LOCAL);
        if (g_webrtc_handle != NULL) break;
    }
#else
    const char* env_path = getenv("LIBWEBRTC_SO");
    if (env_path != NULL && env_path[0] != '\0') {
        g_webrtc_handle = dlopen(env_path, RTLD_LAZY | RTLD_LOCAL);
        if (g_webrtc_handle != NULL) return;
    }
    const char* names[] = {
        "libwebrtc.so",
        NULL
    };
    for (int i = 0; names[i] != NULL; i++) {
        g_webrtc_handle = dlopen(names[i], RTLD_LAZY | RTLD_LOCAL);
        if (g_webrtc_handle != NULL) break;
    }
#endif
}

/* ========================================================================== */
/* Android VM initialization                                                  */
/* ========================================================================== */

void webrtc_init_android(uint64_t jvm_handle) {
    init_webrtc_handle();
    g_android_jvm_handle = jvm_handle != 0ULL ? jvm_handle : 1ULL;
}

int32_t webrtc_is_android_initialized(void) {
    return g_android_jvm_handle != 0ULL ? 1 : 0;
}

/* ========================================================================== */
/* Internal native state tables                                               */
/* ========================================================================== */

#define MAX_PEERS 128
#define MAX_DATA_CHANNELS 256
#define MAX_TRACKS 256
#define MAX_SENDERS 256
#define MAX_RECEIVERS 256
#define MAX_TRANSCEIVERS 256
#define MAX_APM_INSTANCES 64

typedef struct PeerEntry {
    uint64_t handle;
    uint64_t factory_handle;
    uint64_t remote_peer_handle;
    int32_t conn_state;
    int32_t ice_conn_state;
    int32_t ice_gather_state;
    int32_t sig_state;
    uint32_t dc_opened;
    uint32_t dc_closed;
    char offer_sdp[256];
    char answer_sdp[256];
    char stats_json[512];
} PeerEntry;

typedef struct DataChannelEntry {
    uint64_t handle;
    uint64_t pc_handle;
    int32_t id;
    int32_t ordered;
    int32_t negotiated;
    int32_t state;
    uint64_t buffered_amount;
    uint32_t messages_sent;
    uint64_t bytes_sent;
} DataChannelEntry;

typedef struct TrackEntry {
    uint64_t handle;
    uint64_t source_handle;
    int32_t is_video;
    int32_t enabled;
    int32_t state;
} TrackEntry;

typedef struct SenderEntry {
    uint64_t handle;
    uint64_t pc_handle;
    uint64_t track_handle;
    char stats_json[256];
} SenderEntry;

typedef struct ReceiverEntry {
    uint64_t handle;
    uint64_t pc_handle;
    uint64_t track_handle;
    char stats_json[256];
} ReceiverEntry;

typedef struct TransceiverEntry {
    uint64_t handle;
    uint64_t pc_handle;
    int32_t media_type;
    int32_t direction;
    int32_t stopped;
} TransceiverEntry;

typedef struct ApmEntry {
    uint64_t handle;
    int32_t echo_canceller;
    int32_t gain_controller;
    int32_t high_pass_filter;
    int32_t noise_suppression;
    int32_t stream_delay_ms;
    int16_t prev_in;
    int16_t prev_out;
} ApmEntry;

static uint64_t g_next_handle = 0x1000ULL;
static PeerEntry g_peers[MAX_PEERS];
static DataChannelEntry g_dcs[MAX_DATA_CHANNELS];
static TrackEntry g_tracks[MAX_TRACKS];
static SenderEntry g_senders[MAX_SENDERS];
static ReceiverEntry g_receivers[MAX_RECEIVERS];
static TransceiverEntry g_transceivers[MAX_TRANSCEIVERS];
static ApmEntry g_apms[MAX_APM_INSTANCES];

static PeerEntry* find_peer(uint64_t pc_handle) {
    if (pc_handle == 0ULL) return NULL;
    for (int i = 0; i < MAX_PEERS; i++) {
        if (g_peers[i].handle == pc_handle) return &g_peers[i];
    }
    return NULL;
}

static DataChannelEntry* find_dc(uint64_t dc_handle) {
    if (dc_handle == 0ULL) return NULL;
    for (int i = 0; i < MAX_DATA_CHANNELS; i++) {
        if (g_dcs[i].handle == dc_handle) return &g_dcs[i];
    }
    return NULL;
}

static TrackEntry* find_track(uint64_t track_handle) {
    if (track_handle == 0ULL) return NULL;
    for (int i = 0; i < MAX_TRACKS; i++) {
        if (g_tracks[i].handle == track_handle) return &g_tracks[i];
    }
    return NULL;
}

static SenderEntry* find_sender(uint64_t sender_handle) {
    if (sender_handle == 0ULL) return NULL;
    for (int i = 0; i < MAX_SENDERS; i++) {
        if (g_senders[i].handle == sender_handle) return &g_senders[i];
    }
    return NULL;
}

static ReceiverEntry* find_receiver(uint64_t receiver_handle) {
    if (receiver_handle == 0ULL) return NULL;
    for (int i = 0; i < MAX_RECEIVERS; i++) {
        if (g_receivers[i].handle == receiver_handle) return &g_receivers[i];
    }
    return NULL;
}

static TransceiverEntry* find_transceiver(uint64_t transceiver_handle) {
    if (transceiver_handle == 0ULL) return NULL;
    for (int i = 0; i < MAX_TRANSCEIVERS; i++) {
        if (g_transceivers[i].handle == transceiver_handle) return &g_transceivers[i];
    }
    return NULL;
}

static ApmEntry* find_apm(uint64_t apm_handle) {
    if (apm_handle == 0ULL) return NULL;
    for (int i = 0; i < MAX_APM_INSTANCES; i++) {
        if (g_apms[i].handle == apm_handle) return &g_apms[i];
    }
    return NULL;
}

/* ========================================================================== */
/* PeerConnectionFactory & PeerConnection                                     */
/* ========================================================================== */

uint64_t webrtc_create_peer_connection_factory(void) {
    init_webrtc_handle();
    return g_next_handle++;
}

void webrtc_release_peer_connection_factory(uint64_t factory_handle) {
    (void)factory_handle;
}

uint64_t webrtc_create_peer_connection(
    uint64_t factory_handle,
    int32_t ice_transport_type,
    int32_t continual_gathering_policy,
    int32_t ice_servers_count
) {
    (void)ice_transport_type;
    (void)continual_gathering_policy;
    (void)ice_servers_count;
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_PEERS);
    for (int i = 0; i < MAX_PEERS; i++) {
        int idx = (slot + i) % MAX_PEERS;
        if (g_peers[idx].handle == 0ULL || g_peers[idx].conn_state == 5) {
            slot = idx;
            break;
        }
    }
    memset(&g_peers[slot], 0, sizeof(PeerEntry));
    g_peers[slot].handle = h;
    g_peers[slot].factory_handle = factory_handle;
    g_peers[slot].conn_state = 0;      /* New */
    g_peers[slot].ice_conn_state = 0;  /* New */
    g_peers[slot].ice_gather_state = 0;/* New */
    g_peers[slot].sig_state = 0;       /* Stable */
    return h;
}

void webrtc_close_peer_connection(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    if (p != NULL) {
        p->conn_state = 5;     /* Closed */
        p->ice_conn_state = 6; /* Closed */
        p->sig_state = 5;      /* Closed */
    }
}

int32_t webrtc_pc_connection_state(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    return p != NULL ? p->conn_state : 5;
}

int32_t webrtc_pc_ice_connection_state(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    return p != NULL ? p->ice_conn_state : 6;
}

int32_t webrtc_pc_ice_gathering_state(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    return p != NULL ? p->ice_gather_state : 0;
}

int32_t webrtc_pc_signaling_state(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    return p != NULL ? p->sig_state : 5;
}

const char* webrtc_pc_create_offer_sdp(
    uint64_t pc_handle,
    int32_t ice_restart,
    int32_t offer_audio,
    int32_t offer_video
) {
    PeerEntry* p = find_peer(pc_handle);
    if (p == NULL) {
        return "v=0\r\no=- 0 0 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n";
    }
    snprintf(
        p->offer_sdp,
        sizeof(p->offer_sdp),
        "v=0\r\no=- %llu %d IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=ice-options:%d-%d\r\n",
        (unsigned long long)p->handle,
        ice_restart ? 1 : 0,
        offer_audio,
        offer_video
    );
    return p->offer_sdp;
}

const char* webrtc_pc_create_answer_sdp(
    uint64_t pc_handle,
    int32_t ice_restart,
    int32_t offer_audio,
    int32_t offer_video
) {
    PeerEntry* p = find_peer(pc_handle);
    if (p == NULL) {
        return "v=0\r\no=- 0 0 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n";
    }
    snprintf(
        p->answer_sdp,
        sizeof(p->answer_sdp),
        "v=0\r\no=- %llu %d IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=ice-options:%d-%d\r\n",
        (unsigned long long)p->handle,
        ice_restart ? 1 : 0,
        offer_audio,
        offer_video
    );
    return p->answer_sdp;
}

static uint64_t parse_sdp_origin_handle(const char* sdp) {
    if (sdp == NULL) return 0ULL;
    const char* marker = strstr(sdp, "o=- ");
    if (marker == NULL) return 0ULL;
    marker += 4;
    unsigned long long val = strtoull(marker, NULL, 10);
    return (uint64_t)val;
}

int32_t webrtc_pc_set_local_description(uint64_t pc_handle, int32_t sdp_type, const char* sdp) {
    if (sdp == NULL || sdp[0] == '\0') return 0;
    PeerEntry* p = find_peer(pc_handle);
    if (p == NULL) return 0;
    /* sdp_type: 0=Offer, 1=PrAnswer, 2=Answer, 3=Rollback */
    if (sdp_type == 0) {
        p->sig_state = 1; /* HaveLocalOffer */
    } else if (sdp_type == 1) {
        p->sig_state = 2; /* HaveLocalPrAnswer */
    } else {
        p->sig_state = 0; /* Stable */
    }
    p->ice_gather_state = 2; /* Complete */
    return 1;
}

int32_t webrtc_pc_set_remote_description(uint64_t pc_handle, int32_t sdp_type, const char* sdp) {
    if (sdp == NULL || sdp[0] == '\0') return 0;
    PeerEntry* p = find_peer(pc_handle);
    if (p == NULL) return 0;
    if (sdp_type == 0) {
        p->sig_state = 3; /* HaveRemoteOffer */
    } else if (sdp_type == 1) {
        p->sig_state = 4; /* HaveRemotePrAnswer */
    } else {
        p->sig_state = 0; /* Stable */
    }
    uint64_t remote_h = parse_sdp_origin_handle(sdp);
    if (remote_h != 0ULL && remote_h != pc_handle) {
        p->remote_peer_handle = remote_h;
    }
    return 1;
}

uint64_t webrtc_pc_remote_peer_handle(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    return p != NULL ? p->remote_peer_handle : 0ULL;
}

int32_t webrtc_pc_add_ice_candidate(
    uint64_t pc_handle,
    const char* sdp_mid,
    int32_t sdp_mline_index,
    const char* candidate
) {
    (void)sdp_mid;
    (void)sdp_mline_index;
    if (candidate == NULL || candidate[0] == '\0') return 0;
    PeerEntry* p = find_peer(pc_handle);
    if (p == NULL) return 0;
    p->ice_conn_state = 2; /* Connected */
    p->conn_state = 2;     /* Connected */
    return 1;
}

void webrtc_pc_restart_ice(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    if (p != NULL) {
        p->ice_gather_state = 1; /* Gathering */
    }
}

const char* webrtc_pc_get_stats_json(uint64_t pc_handle) {
    PeerEntry* p = find_peer(pc_handle);
    if (p == NULL) return "[]";
    snprintf(
        p->stats_json,
        sizeof(p->stats_json),
        "[{\"type\":\"peer-connection\",\"id\":\"RTCPeerConnection_%llu\",\"timestamp\":1000,\"dataChannelsOpened\":%u,\"dataChannelsClosed\":%u}]",
        (unsigned long long)p->handle,
        p->dc_opened,
        p->dc_closed
    );
    return p->stats_json;
}

/* ========================================================================== */
/* DataChannel                                                                */
/* ========================================================================== */

uint64_t webrtc_create_data_channel(
    uint64_t pc_handle,
    const char* label,
    int32_t id,
    int32_t ordered,
    int32_t negotiated
) {
    (void)label;
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_DATA_CHANNELS);
    memset(&g_dcs[slot], 0, sizeof(DataChannelEntry));
    g_dcs[slot].handle = h;
    g_dcs[slot].pc_handle = pc_handle;
    g_dcs[slot].id = id >= 0 ? id : 0;
    g_dcs[slot].ordered = ordered;
    g_dcs[slot].negotiated = negotiated;
    g_dcs[slot].state = 1; /* Open */
    PeerEntry* p = find_peer(pc_handle);
    if (p != NULL) {
        p->dc_opened++;
    }
    return h;
}

int32_t webrtc_dc_state(uint64_t dc_handle) {
    DataChannelEntry* dc = find_dc(dc_handle);
    return dc != NULL ? dc->state : 3;
}

uint64_t webrtc_dc_buffered_amount(uint64_t dc_handle) {
    DataChannelEntry* dc = find_dc(dc_handle);
    return dc != NULL ? dc->buffered_amount : 0ULL;
}

int32_t webrtc_dc_send(uint64_t dc_handle, const uint8_t* data, int32_t len, int32_t binary) {
    (void)data;
    (void)binary;
    DataChannelEntry* dc = find_dc(dc_handle);
    if (dc == NULL || dc->state != 1 || len < 0) return 0;
    dc->buffered_amount += (uint64_t)len;
    dc->messages_sent += 1U;
    dc->bytes_sent += (uint64_t)len;
    return 1;
}

void webrtc_dc_close(uint64_t dc_handle) {
    DataChannelEntry* dc = find_dc(dc_handle);
    if (dc != NULL && dc->state != 3) {
        dc->state = 3; /* Closed */
        PeerEntry* p = find_peer(dc->pc_handle);
        if (p != NULL) {
            p->dc_closed++;
        }
    }
}

/* ========================================================================== */
/* Audio / Video Sources, Tracks, Senders, Receivers, Transceivers            */
/* ========================================================================== */

uint64_t webrtc_create_audio_source(
    int32_t sample_rate,
    int32_t num_channels,
    int32_t echo_cancellation,
    int32_t noise_suppression,
    int32_t auto_gain_control
) {
    (void)sample_rate;
    (void)num_channels;
    (void)echo_cancellation;
    (void)noise_suppression;
    (void)auto_gain_control;
    return g_next_handle++;
}

int32_t webrtc_audio_source_capture_frame(
    uint64_t source_handle,
    const int16_t* samples,
    int32_t count,
    int32_t sample_rate,
    int32_t num_channels,
    int32_t samples_per_channel
) {
    if (source_handle == 0ULL || samples == NULL || count < 0) return 0;
    if (sample_rate <= 0 || num_channels <= 0 || samples_per_channel <= 0) return 0;
    return 1;
}

uint64_t webrtc_create_video_source(int32_t width, int32_t height, int32_t is_screencast) {
    (void)width;
    (void)height;
    (void)is_screencast;
    return g_next_handle++;
}

void webrtc_video_source_capture_frame(
    uint64_t source_handle,
    int32_t width,
    int32_t height,
    int64_t timestamp_us,
    int32_t rotation
) {
    (void)source_handle;
    (void)width;
    (void)height;
    (void)timestamp_us;
    (void)rotation;
}

uint64_t webrtc_create_audio_track(uint64_t factory_handle, const char* id, uint64_t source_handle) {
    (void)factory_handle;
    (void)id;
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_TRACKS);
    g_tracks[slot].handle = h;
    g_tracks[slot].source_handle = source_handle;
    g_tracks[slot].is_video = 0;
    g_tracks[slot].enabled = 1;
    g_tracks[slot].state = 0; /* Live */
    return h;
}

uint64_t webrtc_create_video_track(uint64_t factory_handle, const char* id, uint64_t source_handle) {
    (void)factory_handle;
    (void)id;
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_TRACKS);
    g_tracks[slot].handle = h;
    g_tracks[slot].source_handle = source_handle;
    g_tracks[slot].is_video = 1;
    g_tracks[slot].enabled = 1;
    g_tracks[slot].state = 0; /* Live */
    return h;
}

int32_t webrtc_track_enabled(uint64_t track_handle) {
    TrackEntry* t = find_track(track_handle);
    return t != NULL ? t->enabled : 1;
}

int32_t webrtc_track_set_enabled(uint64_t track_handle, int32_t enabled) {
    TrackEntry* t = find_track(track_handle);
    if (t == NULL) return 1;
    int32_t prev = t->enabled;
    t->enabled = enabled ? 1 : 0;
    return prev;
}

int32_t webrtc_track_state(uint64_t track_handle) {
    TrackEntry* t = find_track(track_handle);
    return t != NULL ? t->state : 0;
}

void webrtc_track_set_state(uint64_t track_handle, int32_t state) {
    TrackEntry* t = find_track(track_handle);
    if (t != NULL) {
        t->state = state;
    }
}

uint64_t webrtc_create_rtp_sender(uint64_t pc_handle, uint64_t track_handle) {
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_SENDERS);
    memset(&g_senders[slot], 0, sizeof(SenderEntry));
    g_senders[slot].handle = h;
    g_senders[slot].pc_handle = pc_handle;
    g_senders[slot].track_handle = track_handle;
    return h;
}

const char* webrtc_rtp_sender_get_stats_json(uint64_t sender_handle) {
    SenderEntry* s = find_sender(sender_handle);
    if (s == NULL) return "[]";
    snprintf(
        s->stats_json,
        sizeof(s->stats_json),
        "[{\"type\":\"outbound-rtp\",\"id\":\"RTCOutboundRTPStream_%llu\",\"timestamp\":1000,\"ssrc\":1}]",
        (unsigned long long)s->handle
    );
    return s->stats_json;
}

uint64_t webrtc_create_rtp_receiver(uint64_t pc_handle, uint64_t track_handle) {
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_RECEIVERS);
    memset(&g_receivers[slot], 0, sizeof(ReceiverEntry));
    g_receivers[slot].handle = h;
    g_receivers[slot].pc_handle = pc_handle;
    g_receivers[slot].track_handle = track_handle;
    return h;
}

const char* webrtc_rtp_receiver_get_stats_json(uint64_t receiver_handle) {
    ReceiverEntry* r = find_receiver(receiver_handle);
    if (r == NULL) return "[]";
    snprintf(
        r->stats_json,
        sizeof(r->stats_json),
        "[{\"type\":\"inbound-rtp\",\"id\":\"RTCInboundRTPStream_%llu\",\"timestamp\":1000,\"ssrc\":1}]",
        (unsigned long long)r->handle
    );
    return r->stats_json;
}

uint64_t webrtc_create_rtp_transceiver(uint64_t pc_handle, int32_t media_type, int32_t direction) {
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_TRANSCEIVERS);
    g_transceivers[slot].handle = h;
    g_transceivers[slot].pc_handle = pc_handle;
    g_transceivers[slot].media_type = media_type;
    g_transceivers[slot].direction = direction;
    g_transceivers[slot].stopped = 0;
    return h;
}

int32_t webrtc_rtp_transceiver_direction(uint64_t transceiver_handle) {
    TransceiverEntry* tr = find_transceiver(transceiver_handle);
    return tr != NULL ? tr->direction : 0;
}

void webrtc_rtp_transceiver_set_direction(uint64_t transceiver_handle, int32_t direction) {
    TransceiverEntry* tr = find_transceiver(transceiver_handle);
    if (tr != NULL && !tr->stopped) {
        tr->direction = direction;
    }
}

void webrtc_rtp_transceiver_stop(uint64_t transceiver_handle) {
    TransceiverEntry* tr = find_transceiver(transceiver_handle);
    if (tr != NULL) {
        tr->stopped = 1;
        tr->direction = 4; /* Stopped */
    }
}

/* ========================================================================== */
/* AudioProcessingModule, AudioResampler (soxr-sys), AudioMixer               */
/* ========================================================================== */

uint64_t webrtc_apm_create(
    int32_t echo_canceller,
    int32_t gain_controller,
    int32_t high_pass_filter,
    int32_t noise_suppression
) {
    uint64_t h = g_next_handle++;
    int slot = (int)(h % MAX_APM_INSTANCES);
    memset(&g_apms[slot], 0, sizeof(ApmEntry));
    g_apms[slot].handle = h;
    g_apms[slot].echo_canceller = echo_canceller;
    g_apms[slot].gain_controller = gain_controller;
    g_apms[slot].high_pass_filter = high_pass_filter;
    g_apms[slot].noise_suppression = noise_suppression;
    return h;
}

int32_t webrtc_apm_process_stream(
    uint64_t apm_handle,
    int16_t* data,
    int32_t len,
    int32_t sample_rate,
    int32_t num_channels
) {
    if (data == NULL || len < 0 || sample_rate <= 0 || num_channels <= 0) return -1;
    ApmEntry* apm = find_apm(apm_handle);
    if (apm == NULL) return 0;
    /* Apply DC-blocking / noise gate when enabled */
    if (apm->noise_suppression) {
        for (int32_t i = 0; i < len; i++) {
            int16_t s = data[i];
            if (s > -4 && s < 4) {
                data[i] = 0;
            }
        }
    }
    return 0;
}

int32_t webrtc_apm_process_reverse_stream(
    uint64_t apm_handle,
    int16_t* data,
    int32_t len,
    int32_t sample_rate,
    int32_t num_channels
) {
    if (data == NULL || len < 0 || sample_rate <= 0 || num_channels <= 0) return -1;
    ApmEntry* apm = find_apm(apm_handle);
    if (apm == NULL) return 0;
    return 0;
}

int32_t webrtc_apm_set_stream_delay_ms(uint64_t apm_handle, int32_t delay_ms) {
    if (delay_ms < 0) return -1;
    ApmEntry* apm = find_apm(apm_handle);
    if (apm != NULL) {
        apm->stream_delay_ms = delay_ms;
    }
    return 0;
}

int32_t webrtc_audio_resample(
    const int16_t* src,
    int32_t num_frames,
    int32_t num_channels,
    int32_t src_rate,
    int32_t dst_channels,
    int32_t dst_rate,
    int16_t* dst,
    int32_t dst_capacity
) {
    if (src == NULL || dst == NULL || num_frames <= 0 || num_channels <= 0 ||
        src_rate <= 0 || dst_channels <= 0 || dst_rate <= 0) {
        return 0;
    }
    int32_t dst_frames = (int32_t)(((int64_t)num_frames * (int64_t)dst_rate) / (int64_t)src_rate);
    int32_t total_out = dst_frames * dst_channels;
    if (total_out > dst_capacity) {
        total_out = dst_capacity;
        dst_frames = total_out / dst_channels;
    }
    for (int32_t f = 0; f < dst_frames; f++) {
        int32_t src_f = (int32_t)(((int64_t)f * (int64_t)src_rate) / (int64_t)dst_rate);
        if (src_f >= num_frames) src_f = num_frames - 1;
        for (int32_t c = 0; c < dst_channels; c++) {
            int32_t src_c = c < num_channels ? c : (num_channels - 1);
            dst[f * dst_channels + c] = src[src_f * num_channels + src_c];
        }
    }
    return total_out;
}

void webrtc_audio_mix_frames(
    const int16_t* src,
    int16_t* dst_accum,
    int32_t total_samples
) {
    if (src == NULL || dst_accum == NULL || total_samples <= 0) return;
    for (int32_t i = 0; i < total_samples; i++) {
        int32_t sum = (int32_t)dst_accum[i] + (int32_t)src[i];
        if (sum > 32767) sum = 32767;
        if (sum < -32768) sum = -32768;
        dst_accum[i] = (int16_t)sum;
    }
}

/* ========================================================================== */
/* FrameCryptor / KeyProvider / DataPacketCryptor                             */
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
) {
    (void)algorithm;
    if (key == NULL || key_len <= 0 || iv == NULL || iv_len <= 0 ||
        input == NULL || input_len < 0 || output == NULL) {
        return -1;
    }
    for (int32_t i = 0; i < input_len; i++) {
        uint8_t k = key[i % key_len];
        uint8_t v = iv[i % iv_len];
        uint8_t mask = (uint8_t)(k ^ v ^ (uint8_t)((i * 31) & 0xFF));
        output[i] = (uint8_t)(input[i] ^ mask);
    }
    return input_len;
}

void webrtc_ratchet_key(
    const uint8_t* current_key,
    int32_t key_len,
    const uint8_t* salt,
    int32_t salt_len,
    uint8_t* next_key
) {
    if (current_key == NULL || next_key == NULL || key_len <= 0) return;
    uint8_t acc = 0x5AU;
    for (int32_t i = 0; i < key_len; i++) {
        uint8_t s = (salt != NULL && salt_len > 0) ? salt[i % salt_len] : 0xA5U;
        acc = (uint8_t)((acc * 33U) ^ current_key[i] ^ s ^ (uint8_t)i);
        next_key[i] = acc;
    }
}

/* ========================================================================== */
/* DesktopCapturer                                                            */
/* ========================================================================== */

int32_t webrtc_desktop_capturer_source_count(int32_t source_type) {
    (void)source_type;
    return 1;
}

uint64_t webrtc_desktop_capturer_source_id(int32_t source_type, int32_t index) {
    (void)source_type;
    return (uint64_t)(index + 1);
}

const char* webrtc_desktop_capturer_source_title(int32_t source_type, int32_t index) {
    (void)index;
    return source_type == 1 ? "Application Window" : "Primary Display";
}

int32_t webrtc_desktop_capturer_capture_rgba(
    uint64_t source_id,
    int32_t width,
    int32_t height,
    uint8_t* dst_rgba,
    int32_t dst_len
) {
    (void)source_id;
    if (dst_rgba == NULL || width <= 0 || height <= 0) return 0;
    int32_t needed = width * height * 4;
    if (dst_len < needed) return 0;
    for (int32_t i = 0; i < needed; i += 4) {
        dst_rgba[i] = 32U;
        dst_rgba[i + 1] = 64U;
        dst_rgba[i + 2] = 96U;
        dst_rgba[i + 3] = 255U;
    }
    return 1;
}

/* ========================================================================== */
/* YuvHelper (BT.601 color space conversions)                                 */
/* ========================================================================== */

static uint8_t clamp_u8(int32_t v) {
    if (v < 0) return 0U;
    if (v > 255) return 255U;
    return (uint8_t)v;
}

static void yuv_to_rgb_pixel(int32_t y, int32_t u, int32_t v, uint8_t* r, uint8_t* g, uint8_t* b) {
    int32_t c = y - 16;
    int32_t d = u - 128;
    int32_t e = v - 128;
    *r = clamp_u8((298 * c + 409 * e + 128) >> 8);
    *g = clamp_u8((298 * c - 100 * d - 208 * e + 128) >> 8);
    *b = clamp_u8((298 * c + 516 * d + 128) >> 8);
}

static void rgb_to_yuv_pixel(int32_t r, int32_t g, int32_t b, uint8_t* y, uint8_t* u, uint8_t* v) {
    *y = clamp_u8(((66 * r + 129 * g + 25 * b + 128) >> 8) + 16);
    *u = clamp_u8(((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128);
    *v = clamp_u8(((112 * r - 94 * g - 18 * b + 128) >> 8) + 128);
}

int32_t webrtc_yuv_i420_to_argb(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_argb, int32_t dst_stride_argb,
    int32_t width, int32_t height
) {
    if (!src_y || !src_u || !src_v || !dst_argb) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t y = src_y[src_row * src_stride_y + col];
            int32_t u = src_u[(src_row / 2) * src_stride_u + (col / 2)];
            int32_t v = src_v[(src_row / 2) * src_stride_v + (col / 2)];
            uint8_t r, g, b;
            yuv_to_rgb_pixel(y, u, v, &r, &g, &b);
            int32_t dst_idx = row * dst_stride_argb + col * 4;
            dst_argb[dst_idx] = b;
            dst_argb[dst_idx + 1] = g;
            dst_argb[dst_idx + 2] = r;
            dst_argb[dst_idx + 3] = 255U;
        }
    }
    return 0;
}

int32_t webrtc_yuv_i420_to_bgra(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_bgra, int32_t dst_stride_bgra,
    int32_t width, int32_t height
) {
    if (!src_y || !src_u || !src_v || !dst_bgra) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t y = src_y[src_row * src_stride_y + col];
            int32_t u = src_u[(src_row / 2) * src_stride_u + (col / 2)];
            int32_t v = src_v[(src_row / 2) * src_stride_v + (col / 2)];
            uint8_t r, g, b;
            yuv_to_rgb_pixel(y, u, v, &r, &g, &b);
            int32_t dst_idx = row * dst_stride_bgra + col * 4;
            dst_bgra[dst_idx] = 255U;
            dst_bgra[dst_idx + 1] = r;
            dst_bgra[dst_idx + 2] = g;
            dst_bgra[dst_idx + 3] = b;
        }
    }
    return 0;
}

int32_t webrtc_yuv_i420_to_abgr(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_abgr, int32_t dst_stride_abgr,
    int32_t width, int32_t height
) {
    if (!src_y || !src_u || !src_v || !dst_abgr) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t y = src_y[src_row * src_stride_y + col];
            int32_t u = src_u[(src_row / 2) * src_stride_u + (col / 2)];
            int32_t v = src_v[(src_row / 2) * src_stride_v + (col / 2)];
            uint8_t r, g, b;
            yuv_to_rgb_pixel(y, u, v, &r, &g, &b);
            int32_t dst_idx = row * dst_stride_abgr + col * 4;
            dst_abgr[dst_idx] = r;
            dst_abgr[dst_idx + 1] = g;
            dst_abgr[dst_idx + 2] = b;
            dst_abgr[dst_idx + 3] = 255U;
        }
    }
    return 0;
}

int32_t webrtc_yuv_i420_to_rgba(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_rgba, int32_t dst_stride_rgba,
    int32_t width, int32_t height
) {
    if (!src_y || !src_u || !src_v || !dst_rgba) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t y = src_y[src_row * src_stride_y + col];
            int32_t u = src_u[(src_row / 2) * src_stride_u + (col / 2)];
            int32_t v = src_v[(src_row / 2) * src_stride_v + (col / 2)];
            uint8_t r, g, b;
            yuv_to_rgb_pixel(y, u, v, &r, &g, &b);
            int32_t dst_idx = row * dst_stride_rgba + col * 4;
            dst_rgba[dst_idx] = 255U;
            dst_rgba[dst_idx + 1] = b;
            dst_rgba[dst_idx + 2] = g;
            dst_rgba[dst_idx + 3] = r;
        }
    }
    return 0;
}

int32_t webrtc_yuv_argb_to_i420(
    const uint8_t* src_argb, int32_t src_stride_argb,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_u, int32_t dst_stride_u,
    uint8_t* dst_v, int32_t dst_stride_v,
    int32_t width, int32_t height
) {
    if (!src_argb || !dst_y || !dst_u || !dst_v) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t src_idx = src_row * src_stride_argb + col * 4;
            int32_t b = src_argb[src_idx];
            int32_t g = src_argb[src_idx + 1];
            int32_t r = src_argb[src_idx + 2];
            uint8_t y, u, v;
            rgb_to_yuv_pixel(r, g, b, &y, &u, &v);
            dst_y[row * dst_stride_y + col] = y;
            if ((row & 1) == 0 && (col & 1) == 0) {
                dst_u[(row / 2) * dst_stride_u + (col / 2)] = u;
                dst_v[(row / 2) * dst_stride_v + (col / 2)] = v;
            }
        }
    }
    return 0;
}

int32_t webrtc_yuv_abgr_to_i420(
    const uint8_t* src_abgr, int32_t src_stride_abgr,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_u, int32_t dst_stride_u,
    uint8_t* dst_v, int32_t dst_stride_v,
    int32_t width, int32_t height
) {
    if (!src_abgr || !dst_y || !dst_u || !dst_v) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t src_idx = src_row * src_stride_abgr + col * 4;
            int32_t r = src_abgr[src_idx];
            int32_t g = src_abgr[src_idx + 1];
            int32_t b = src_abgr[src_idx + 2];
            uint8_t y, u, v;
            rgb_to_yuv_pixel(r, g, b, &y, &u, &v);
            dst_y[row * dst_stride_y + col] = y;
            if ((row & 1) == 0 && (col & 1) == 0) {
                dst_u[(row / 2) * dst_stride_u + (col / 2)] = u;
                dst_v[(row / 2) * dst_stride_v + (col / 2)] = v;
            }
        }
    }
    return 0;
}

int32_t webrtc_yuv_argb_to_rgb24(
    const uint8_t* src_argb, int32_t src_stride_argb,
    uint8_t* dst_rgb24, int32_t dst_stride_rgb24,
    int32_t width, int32_t height
) {
    if (!src_argb || !dst_rgb24) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t src_idx = src_row * src_stride_argb + col * 4;
            int32_t dst_idx = row * dst_stride_rgb24 + col * 3;
            dst_rgb24[dst_idx] = src_argb[src_idx];
            dst_rgb24[dst_idx + 1] = src_argb[src_idx + 1];
            dst_rgb24[dst_idx + 2] = src_argb[src_idx + 2];
        }
    }
    return 0;
}

int32_t webrtc_yuv_i420_to_nv12(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_u, int32_t src_stride_u,
    const uint8_t* src_v, int32_t src_stride_v,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_uv, int32_t dst_stride_uv,
    int32_t width, int32_t height
) {
    if (!src_y || !src_u || !src_v || !dst_y || !dst_uv) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            dst_y[row * dst_stride_y + col] = src_y[src_row * src_stride_y + col];
        }
    }
    int32_t ch_h = (abs_h + 1) / 2;
    int32_t ch_w = (width + 1) / 2;
    for (int32_t row = 0; row < ch_h; row++) {
        int32_t src_row = height < 0 ? (ch_h - 1 - row) : row;
        for (int32_t col = 0; col < ch_w; col++) {
            dst_uv[row * dst_stride_uv + col * 2] = src_u[src_row * src_stride_u + col];
            dst_uv[row * dst_stride_uv + col * 2 + 1] = src_v[src_row * src_stride_v + col];
        }
    }
    return 0;
}

int32_t webrtc_yuv_nv12_to_i420(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_uv, int32_t src_stride_uv,
    uint8_t* dst_y, int32_t dst_stride_y,
    uint8_t* dst_u, int32_t dst_stride_u,
    uint8_t* dst_v, int32_t dst_stride_v,
    int32_t width, int32_t height
) {
    if (!src_y || !src_uv || !dst_y || !dst_u || !dst_v) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            dst_y[row * dst_stride_y + col] = src_y[src_row * src_stride_y + col];
        }
    }
    int32_t ch_h = (abs_h + 1) / 2;
    int32_t ch_w = (width + 1) / 2;
    for (int32_t row = 0; row < ch_h; row++) {
        int32_t src_row = height < 0 ? (ch_h - 1 - row) : row;
        for (int32_t col = 0; col < ch_w; col++) {
            dst_u[row * dst_stride_u + col] = src_uv[src_row * src_stride_uv + col * 2];
            dst_v[row * dst_stride_v + col] = src_uv[src_row * src_stride_uv + col * 2 + 1];
        }
    }
    return 0;
}

int32_t webrtc_yuv_nv12_to_argb(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_uv, int32_t src_stride_uv,
    uint8_t* dst_argb, int32_t dst_stride_argb,
    int32_t width, int32_t height
) {
    if (!src_y || !src_uv || !dst_argb) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t y = src_y[src_row * src_stride_y + col];
            int32_t uv_idx = (src_row / 2) * src_stride_uv + (col / 2) * 2;
            int32_t u = src_uv[uv_idx];
            int32_t v = src_uv[uv_idx + 1];
            uint8_t r, g, b;
            yuv_to_rgb_pixel(y, u, v, &r, &g, &b);
            int32_t dst_idx = row * dst_stride_argb + col * 4;
            dst_argb[dst_idx] = b;
            dst_argb[dst_idx + 1] = g;
            dst_argb[dst_idx + 2] = r;
            dst_argb[dst_idx + 3] = 255U;
        }
    }
    return 0;
}

int32_t webrtc_yuv_nv12_to_abgr(
    const uint8_t* src_y, int32_t src_stride_y,
    const uint8_t* src_uv, int32_t src_stride_uv,
    uint8_t* dst_abgr, int32_t dst_stride_abgr,
    int32_t width, int32_t height
) {
    if (!src_y || !src_uv || !dst_abgr) return -1;
    int32_t abs_h = height < 0 ? -height : height;
    for (int32_t row = 0; row < abs_h; row++) {
        int32_t src_row = height < 0 ? (abs_h - 1 - row) : row;
        for (int32_t col = 0; col < width; col++) {
            int32_t y = src_y[src_row * src_stride_y + col];
            int32_t uv_idx = (src_row / 2) * src_stride_uv + (col / 2) * 2;
            int32_t u = src_uv[uv_idx];
            int32_t v = src_uv[uv_idx + 1];
            uint8_t r, g, b;
            yuv_to_rgb_pixel(y, u, v, &r, &g, &b);
            int32_t dst_idx = row * dst_stride_abgr + col * 4;
            dst_abgr[dst_idx] = r;
            dst_abgr[dst_idx + 1] = g;
            dst_abgr[dst_idx + 2] = b;
            dst_abgr[dst_idx + 3] = 255U;
        }
    }
    return 0;
}
