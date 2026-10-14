#include <jni.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

#include "../../nativeInterop/cinterop/webrtc_wrapper.c"

#define JNI_FN(name) JNICALL Java_io_github_kotlinmania_libwebrtc_internal_WebrtcJni_##name

JNIEXPORT void JNI_FN(initAndroid)(JNIEnv* env, jobject thiz, jlong jvmHandle) {
    (void)env;
    (void)thiz;
    webrtc_init_android((uint64_t)jvmHandle);
}

JNIEXPORT jboolean JNI_FN(isAndroidInitialized)(JNIEnv* env, jobject thiz) {
    (void)env;
    (void)thiz;
    return webrtc_is_android_initialized() != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jlong JNI_FN(createPeerConnectionFactory)(JNIEnv* env, jobject thiz) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_create_peer_connection_factory();
}

JNIEXPORT void JNI_FN(releasePeerConnectionFactory)(JNIEnv* env, jobject thiz, jlong factoryHandle) {
    (void)env;
    (void)thiz;
    webrtc_release_peer_connection_factory((uint64_t)factoryHandle);
}

JNIEXPORT jlong JNI_FN(createPeerConnection)(
    JNIEnv* env,
    jobject thiz,
    jlong factoryHandle,
    jint iceTransportType,
    jint continualGatheringPolicy,
    jint iceServersCount
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_create_peer_connection(
        (uint64_t)factoryHandle,
        (int32_t)iceTransportType,
        (int32_t)continualGatheringPolicy,
        (int32_t)iceServersCount
    );
}

JNIEXPORT void JNI_FN(closePeerConnection)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)env;
    (void)thiz;
    webrtc_close_peer_connection((uint64_t)pcHandle);
}

JNIEXPORT jint JNI_FN(pcConnectionState)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_pc_connection_state((uint64_t)pcHandle);
}

JNIEXPORT jint JNI_FN(pcIceConnectionState)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_pc_ice_connection_state((uint64_t)pcHandle);
}

JNIEXPORT jint JNI_FN(pcIceGatheringState)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_pc_ice_gathering_state((uint64_t)pcHandle);
}

JNIEXPORT jint JNI_FN(pcSignalingState)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_pc_signaling_state((uint64_t)pcHandle);
}

JNIEXPORT jstring JNI_FN(pcCreateOfferSdp)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jboolean iceRestart,
    jboolean offerAudio,
    jboolean offerVideo
) {
    (void)thiz;
    const char* s = webrtc_pc_create_offer_sdp(
        (uint64_t)pcHandle,
        iceRestart ? 1 : 0,
        offerAudio ? 1 : 0,
        offerVideo ? 1 : 0
    );
    return (*env)->NewStringUTF(env, s != NULL ? s : "");
}

JNIEXPORT jstring JNI_FN(pcCreateAnswerSdp)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jboolean iceRestart,
    jboolean offerAudio,
    jboolean offerVideo
) {
    (void)thiz;
    const char* s = webrtc_pc_create_answer_sdp(
        (uint64_t)pcHandle,
        iceRestart ? 1 : 0,
        offerAudio ? 1 : 0,
        offerVideo ? 1 : 0
    );
    return (*env)->NewStringUTF(env, s != NULL ? s : "");
}

JNIEXPORT jboolean JNI_FN(pcSetLocalDescription)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jint sdpType,
    jstring sdp
) {
    (void)thiz;
    const char* c_sdp = sdp != NULL ? (*env)->GetStringUTFChars(env, sdp, NULL) : "";
    int32_t ok = webrtc_pc_set_local_description((uint64_t)pcHandle, (int32_t)sdpType, c_sdp);
    if (sdp != NULL && c_sdp != NULL) {
        (*env)->ReleaseStringUTFChars(env, sdp, c_sdp);
    }
    return ok != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNI_FN(pcSetRemoteDescription)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jint sdpType,
    jstring sdp
) {
    (void)thiz;
    const char* c_sdp = sdp != NULL ? (*env)->GetStringUTFChars(env, sdp, NULL) : "";
    int32_t ok = webrtc_pc_set_remote_description((uint64_t)pcHandle, (int32_t)sdpType, c_sdp);
    if (sdp != NULL && c_sdp != NULL) {
        (*env)->ReleaseStringUTFChars(env, sdp, c_sdp);
    }
    return ok != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jlong JNI_FN(pcRemotePeerHandle)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_pc_remote_peer_handle((uint64_t)pcHandle);
}

JNIEXPORT jboolean JNI_FN(pcAddIceCandidate)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jstring sdpMid,
    jint sdpMlineIndex,
    jstring candidate
) {
    (void)thiz;
    const char* c_mid = sdpMid != NULL ? (*env)->GetStringUTFChars(env, sdpMid, NULL) : "";
    const char* c_cand = candidate != NULL ? (*env)->GetStringUTFChars(env, candidate, NULL) : "";
    int32_t ok = webrtc_pc_add_ice_candidate(
        (uint64_t)pcHandle,
        c_mid,
        (int32_t)sdpMlineIndex,
        c_cand
    );
    if (sdpMid != NULL && c_mid != NULL) {
        (*env)->ReleaseStringUTFChars(env, sdpMid, c_mid);
    }
    if (candidate != NULL && c_cand != NULL) {
        (*env)->ReleaseStringUTFChars(env, candidate, c_cand);
    }
    return ok != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNI_FN(pcRestartIce)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)env;
    (void)thiz;
    webrtc_pc_restart_ice((uint64_t)pcHandle);
}

JNIEXPORT jstring JNI_FN(pcGetStatsJson)(JNIEnv* env, jobject thiz, jlong pcHandle) {
    (void)thiz;
    const char* s = webrtc_pc_get_stats_json((uint64_t)pcHandle);
    return (*env)->NewStringUTF(env, s != NULL ? s : "[]");
}

JNIEXPORT jlong JNI_FN(createDataChannel)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jstring label,
    jint id,
    jboolean ordered,
    jboolean negotiated
) {
    (void)thiz;
    const char* c_label = label != NULL ? (*env)->GetStringUTFChars(env, label, NULL) : "";
    uint64_t h = webrtc_create_data_channel(
        (uint64_t)pcHandle,
        c_label,
        (int32_t)id,
        ordered ? 1 : 0,
        negotiated ? 1 : 0
    );
    if (label != NULL && c_label != NULL) {
        (*env)->ReleaseStringUTFChars(env, label, c_label);
    }
    return (jlong)h;
}

JNIEXPORT jint JNI_FN(dcState)(JNIEnv* env, jobject thiz, jlong dcHandle) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_dc_state((uint64_t)dcHandle);
}

JNIEXPORT jlong JNI_FN(dcBufferedAmount)(JNIEnv* env, jobject thiz, jlong dcHandle) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_dc_buffered_amount((uint64_t)dcHandle);
}

JNIEXPORT jboolean JNI_FN(dcSend)(
    JNIEnv* env,
    jobject thiz,
    jlong dcHandle,
    jbyteArray data,
    jboolean binary
) {
    (void)thiz;
    jsize len = data != NULL ? (*env)->GetArrayLength(env, data) : 0;
    jbyte* ptr = (data != NULL && len > 0) ? (*env)->GetByteArrayElements(env, data, NULL) : NULL;
    int32_t ok = webrtc_dc_send(
        (uint64_t)dcHandle,
        (const uint8_t*)ptr,
        (int32_t)len,
        binary ? 1 : 0
    );
    if (ptr != NULL) {
        (*env)->ReleaseByteArrayElements(env, data, ptr, JNI_ABORT);
    }
    return ok != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNI_FN(dcClose)(JNIEnv* env, jobject thiz, jlong dcHandle) {
    (void)env;
    (void)thiz;
    webrtc_dc_close((uint64_t)dcHandle);
}

JNIEXPORT jlong JNI_FN(createAudioSource)(
    JNIEnv* env,
    jobject thiz,
    jint sampleRate,
    jint numChannels,
    jboolean echoCancellation,
    jboolean noiseSuppression,
    jboolean autoGainControl
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_create_audio_source(
        (int32_t)sampleRate,
        (int32_t)numChannels,
        echoCancellation ? 1 : 0,
        noiseSuppression ? 1 : 0,
        autoGainControl ? 1 : 0
    );
}

JNIEXPORT jboolean JNI_FN(audioSourceCaptureFrame)(
    JNIEnv* env,
    jobject thiz,
    jlong sourceHandle,
    jshortArray samples,
    jint sampleRate,
    jint numChannels,
    jint samplesPerChannel
) {
    (void)thiz;
    jsize count = samples != NULL ? (*env)->GetArrayLength(env, samples) : 0;
    jshort* ptr = (samples != NULL && count > 0) ? (*env)->GetShortArrayElements(env, samples, NULL) : NULL;
    int32_t ok = webrtc_audio_source_capture_frame(
        (uint64_t)sourceHandle,
        (const int16_t*)ptr,
        (int32_t)count,
        (int32_t)sampleRate,
        (int32_t)numChannels,
        (int32_t)samplesPerChannel
    );
    if (ptr != NULL) {
        (*env)->ReleaseShortArrayElements(env, samples, ptr, JNI_ABORT);
    }
    return ok != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jlong JNI_FN(createVideoSource)(
    JNIEnv* env,
    jobject thiz,
    jint width,
    jint height,
    jboolean isScreencast
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_create_video_source((int32_t)width, (int32_t)height, isScreencast ? 1 : 0);
}

JNIEXPORT void JNI_FN(videoSourceCaptureFrame)(
    JNIEnv* env,
    jobject thiz,
    jlong sourceHandle,
    jint width,
    jint height,
    jlong timestampUs,
    jint rotation
) {
    (void)env;
    (void)thiz;
    webrtc_video_source_capture_frame(
        (uint64_t)sourceHandle,
        (int32_t)width,
        (int32_t)height,
        (int64_t)timestampUs,
        (int32_t)rotation
    );
}

JNIEXPORT jlong JNI_FN(createAudioTrack)(
    JNIEnv* env,
    jobject thiz,
    jlong factoryHandle,
    jstring id,
    jlong sourceHandle
) {
    (void)thiz;
    const char* c_id = id != NULL ? (*env)->GetStringUTFChars(env, id, NULL) : "";
    uint64_t h = webrtc_create_audio_track((uint64_t)factoryHandle, c_id, (uint64_t)sourceHandle);
    if (id != NULL && c_id != NULL) {
        (*env)->ReleaseStringUTFChars(env, id, c_id);
    }
    return (jlong)h;
}

JNIEXPORT jlong JNI_FN(createVideoTrack)(
    JNIEnv* env,
    jobject thiz,
    jlong factoryHandle,
    jstring id,
    jlong sourceHandle
) {
    (void)thiz;
    const char* c_id = id != NULL ? (*env)->GetStringUTFChars(env, id, NULL) : "";
    uint64_t h = webrtc_create_video_track((uint64_t)factoryHandle, c_id, (uint64_t)sourceHandle);
    if (id != NULL && c_id != NULL) {
        (*env)->ReleaseStringUTFChars(env, id, c_id);
    }
    return (jlong)h;
}

JNIEXPORT jboolean JNI_FN(trackEnabled)(JNIEnv* env, jobject thiz, jlong trackHandle) {
    (void)env;
    (void)thiz;
    return webrtc_track_enabled((uint64_t)trackHandle) != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNI_FN(trackSetEnabled)(
    JNIEnv* env,
    jobject thiz,
    jlong trackHandle,
    jboolean enabled
) {
    (void)env;
    (void)thiz;
    return webrtc_track_set_enabled((uint64_t)trackHandle, enabled ? 1 : 0) != 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNI_FN(trackState)(JNIEnv* env, jobject thiz, jlong trackHandle) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_track_state((uint64_t)trackHandle);
}

JNIEXPORT void JNI_FN(trackSetState)(JNIEnv* env, jobject thiz, jlong trackHandle, jint state) {
    (void)env;
    (void)thiz;
    webrtc_track_set_state((uint64_t)trackHandle, (int32_t)state);
}

JNIEXPORT jlong JNI_FN(createRtpSender)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jlong trackHandle
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_create_rtp_sender((uint64_t)pcHandle, (uint64_t)trackHandle);
}

JNIEXPORT jstring JNI_FN(rtpSenderGetStatsJson)(JNIEnv* env, jobject thiz, jlong senderHandle) {
    (void)thiz;
    const char* s = webrtc_rtp_sender_get_stats_json((uint64_t)senderHandle);
    return (*env)->NewStringUTF(env, s != NULL ? s : "[]");
}

JNIEXPORT jlong JNI_FN(createRtpReceiver)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jlong trackHandle
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_create_rtp_receiver((uint64_t)pcHandle, (uint64_t)trackHandle);
}

JNIEXPORT jstring JNI_FN(rtpReceiverGetStatsJson)(JNIEnv* env, jobject thiz, jlong receiverHandle) {
    (void)thiz;
    const char* s = webrtc_rtp_receiver_get_stats_json((uint64_t)receiverHandle);
    return (*env)->NewStringUTF(env, s != NULL ? s : "[]");
}

JNIEXPORT jlong JNI_FN(createRtpTransceiver)(
    JNIEnv* env,
    jobject thiz,
    jlong pcHandle,
    jint mediaType,
    jint direction
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_create_rtp_transceiver(
        (uint64_t)pcHandle,
        (int32_t)mediaType,
        (int32_t)direction
    );
}

JNIEXPORT jint JNI_FN(rtpTransceiverDirection)(JNIEnv* env, jobject thiz, jlong transceiverHandle) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_rtp_transceiver_direction((uint64_t)transceiverHandle);
}

JNIEXPORT void JNI_FN(rtpTransceiverSetDirection)(
    JNIEnv* env,
    jobject thiz,
    jlong transceiverHandle,
    jint direction
) {
    (void)env;
    (void)thiz;
    webrtc_rtp_transceiver_set_direction((uint64_t)transceiverHandle, (int32_t)direction);
}

JNIEXPORT void JNI_FN(rtpTransceiverStop)(JNIEnv* env, jobject thiz, jlong transceiverHandle) {
    (void)env;
    (void)thiz;
    webrtc_rtp_transceiver_stop((uint64_t)transceiverHandle);
}

JNIEXPORT jlong JNI_FN(apmCreate)(
    JNIEnv* env,
    jobject thiz,
    jboolean echoCanceller,
    jboolean gainController,
    jboolean highPassFilter,
    jboolean noiseSuppression
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_apm_create(
        echoCanceller ? 1 : 0,
        gainController ? 1 : 0,
        highPassFilter ? 1 : 0,
        noiseSuppression ? 1 : 0
    );
}

JNIEXPORT jint JNI_FN(apmProcessStream)(
    JNIEnv* env,
    jobject thiz,
    jlong apmHandle,
    jshortArray data,
    jint sampleRate,
    jint numChannels
) {
    (void)thiz;
    jsize len = data != NULL ? (*env)->GetArrayLength(env, data) : 0;
    jshort* ptr = (data != NULL && len > 0) ? (*env)->GetShortArrayElements(env, data, NULL) : NULL;
    int32_t res = webrtc_apm_process_stream(
        (uint64_t)apmHandle,
        (int16_t*)ptr,
        (int32_t)len,
        (int32_t)sampleRate,
        (int32_t)numChannels
    );
    if (ptr != NULL) {
        (*env)->ReleaseShortArrayElements(env, data, ptr, 0);
    }
    return (jint)res;
}

JNIEXPORT jint JNI_FN(apmProcessReverseStream)(
    JNIEnv* env,
    jobject thiz,
    jlong apmHandle,
    jshortArray data,
    jint sampleRate,
    jint numChannels
) {
    (void)thiz;
    jsize len = data != NULL ? (*env)->GetArrayLength(env, data) : 0;
    jshort* ptr = (data != NULL && len > 0) ? (*env)->GetShortArrayElements(env, data, NULL) : NULL;
    int32_t res = webrtc_apm_process_reverse_stream(
        (uint64_t)apmHandle,
        (int16_t*)ptr,
        (int32_t)len,
        (int32_t)sampleRate,
        (int32_t)numChannels
    );
    if (ptr != NULL) {
        (*env)->ReleaseShortArrayElements(env, data, ptr, 0);
    }
    return (jint)res;
}

JNIEXPORT jint JNI_FN(apmSetStreamDelayMs)(
    JNIEnv* env,
    jobject thiz,
    jlong apmHandle,
    jint delayMs
) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_apm_set_stream_delay_ms((uint64_t)apmHandle, (int32_t)delayMs);
}

JNIEXPORT jint JNI_FN(audioResample)(
    JNIEnv* env,
    jobject thiz,
    jshortArray src,
    jint numFrames,
    jint numChannels,
    jint srcRate,
    jint dstChannels,
    jint dstRate,
    jshortArray dst
) {
    (void)thiz;
    jsize srcLen = src != NULL ? (*env)->GetArrayLength(env, src) : 0;
    jsize dstCap = dst != NULL ? (*env)->GetArrayLength(env, dst) : 0;
    jshort* srcPtr = (src != NULL && srcLen > 0) ? (*env)->GetShortArrayElements(env, src, NULL) : NULL;
    jshort* dstPtr = (dst != NULL && dstCap > 0) ? (*env)->GetShortArrayElements(env, dst, NULL) : NULL;
    int32_t produced = webrtc_audio_resample(
        (const int16_t*)srcPtr,
        (int32_t)numFrames,
        (int32_t)numChannels,
        (int32_t)srcRate,
        (int32_t)dstChannels,
        (int32_t)dstRate,
        (int16_t*)dstPtr,
        (int32_t)dstCap
    );
    if (srcPtr != NULL) {
        (*env)->ReleaseShortArrayElements(env, src, srcPtr, JNI_ABORT);
    }
    if (dstPtr != NULL) {
        (*env)->ReleaseShortArrayElements(env, dst, dstPtr, 0);
    }
    return (jint)produced;
}

JNIEXPORT void JNI_FN(audioMixFrames)(
    JNIEnv* env,
    jobject thiz,
    jshortArray src,
    jshortArray dstAccum,
    jint totalSamples
) {
    (void)thiz;
    jsize srcLen = src != NULL ? (*env)->GetArrayLength(env, src) : 0;
    jsize dstLen = dstAccum != NULL ? (*env)->GetArrayLength(env, dstAccum) : 0;
    jshort* srcPtr = (src != NULL && srcLen > 0) ? (*env)->GetShortArrayElements(env, src, NULL) : NULL;
    jshort* dstPtr = (dstAccum != NULL && dstLen > 0) ? (*env)->GetShortArrayElements(env, dstAccum, NULL) : NULL;
    webrtc_audio_mix_frames((const int16_t*)srcPtr, (int16_t*)dstPtr, (int32_t)totalSamples);
    if (srcPtr != NULL) {
        (*env)->ReleaseShortArrayElements(env, src, srcPtr, JNI_ABORT);
    }
    if (dstPtr != NULL) {
        (*env)->ReleaseShortArrayElements(env, dstAccum, dstPtr, 0);
    }
}

JNIEXPORT jbyteArray JNI_FN(cryptorTransform)(
    JNIEnv* env,
    jobject thiz,
    jint algorithm,
    jbyteArray key,
    jbyteArray iv,
    jbyteArray input
) {
    (void)thiz;
    jsize keyLen = key != NULL ? (*env)->GetArrayLength(env, key) : 0;
    jsize ivLen = iv != NULL ? (*env)->GetArrayLength(env, iv) : 0;
    jsize inputLen = input != NULL ? (*env)->GetArrayLength(env, input) : 0;
    jbyte* keyPtr = (key != NULL && keyLen > 0) ? (*env)->GetByteArrayElements(env, key, NULL) : NULL;
    jbyte* ivPtr = (iv != NULL && ivLen > 0) ? (*env)->GetByteArrayElements(env, iv, NULL) : NULL;
    jbyte* inputPtr = (input != NULL && inputLen > 0) ? (*env)->GetByteArrayElements(env, input, NULL) : NULL;
    uint8_t* outBuf = (uint8_t*)calloc((size_t)(inputLen > 0 ? inputLen : 1), sizeof(uint8_t));
    int32_t outLen = webrtc_cryptor_transform(
        (int32_t)algorithm,
        (const uint8_t*)keyPtr,
        (int32_t)keyLen,
        (const uint8_t*)ivPtr,
        (int32_t)ivLen,
        (const uint8_t*)inputPtr,
        (int32_t)inputLen,
        outBuf
    );
    if (keyPtr != NULL) (*env)->ReleaseByteArrayElements(env, key, keyPtr, JNI_ABORT);
    if (ivPtr != NULL) (*env)->ReleaseByteArrayElements(env, iv, ivPtr, JNI_ABORT);
    if (inputPtr != NULL) (*env)->ReleaseByteArrayElements(env, input, inputPtr, JNI_ABORT);
    if (outLen < 0) {
        free(outBuf);
        return (*env)->NewByteArray(env, 0);
    }
    jbyteArray result = (*env)->NewByteArray(env, (jsize)outLen);
    if (outLen > 0) {
        (*env)->SetByteArrayRegion(env, result, 0, (jsize)outLen, (const jbyte*)outBuf);
    }
    free(outBuf);
    return result;
}

JNIEXPORT jbyteArray JNI_FN(ratchetKey)(
    JNIEnv* env,
    jobject thiz,
    jbyteArray currentKey,
    jbyteArray salt
) {
    (void)thiz;
    jsize keyLen = currentKey != NULL ? (*env)->GetArrayLength(env, currentKey) : 0;
    jsize saltLen = salt != NULL ? (*env)->GetArrayLength(env, salt) : 0;
    jbyte* keyPtr = (currentKey != NULL && keyLen > 0) ? (*env)->GetByteArrayElements(env, currentKey, NULL) : NULL;
    jbyte* saltPtr = (salt != NULL && saltLen > 0) ? (*env)->GetByteArrayElements(env, salt, NULL) : NULL;
    uint8_t* nextKey = (uint8_t*)calloc((size_t)(keyLen > 0 ? keyLen : 1), sizeof(uint8_t));
    webrtc_ratchet_key(
        (const uint8_t*)keyPtr,
        (int32_t)keyLen,
        (const uint8_t*)saltPtr,
        (int32_t)saltLen,
        nextKey
    );
    if (keyPtr != NULL) (*env)->ReleaseByteArrayElements(env, currentKey, keyPtr, JNI_ABORT);
    if (saltPtr != NULL) (*env)->ReleaseByteArrayElements(env, salt, saltPtr, JNI_ABORT);
    jbyteArray result = (*env)->NewByteArray(env, keyLen);
    if (keyLen > 0) {
        (*env)->SetByteArrayRegion(env, result, 0, keyLen, (const jbyte*)nextKey);
    }
    free(nextKey);
    return result;
}

JNIEXPORT jint JNI_FN(desktopCapturerSourceCount)(JNIEnv* env, jobject thiz, jint sourceType) {
    (void)env;
    (void)thiz;
    return (jint)webrtc_desktop_capturer_source_count((int32_t)sourceType);
}

JNIEXPORT jlong JNI_FN(desktopCapturerSourceId)(
    JNIEnv* env,
    jobject thiz,
    jint sourceType,
    jint index
) {
    (void)env;
    (void)thiz;
    return (jlong)webrtc_desktop_capturer_source_id((int32_t)sourceType, (int32_t)index);
}

JNIEXPORT jstring JNI_FN(desktopCapturerSourceTitle)(
    JNIEnv* env,
    jobject thiz,
    jint sourceType,
    jint index
) {
    (void)thiz;
    const char* s = webrtc_desktop_capturer_source_title((int32_t)sourceType, (int32_t)index);
    return (*env)->NewStringUTF(env, s != NULL ? s : "");
}

JNIEXPORT jbyteArray JNI_FN(desktopCapturerCaptureRgba)(
    JNIEnv* env,
    jobject thiz,
    jlong sourceId,
    jint width,
    jint height
) {
    (void)thiz;
    int32_t len = (int32_t)width * (int32_t)height * 4;
    if (len <= 0) return (*env)->NewByteArray(env, 0);
    uint8_t* buf = (uint8_t*)calloc((size_t)len, sizeof(uint8_t));
    int32_t written = webrtc_desktop_capturer_capture_rgba(
        (uint64_t)sourceId,
        (int32_t)width,
        (int32_t)height,
        buf,
        len
    );
    jbyteArray result = (*env)->NewByteArray(env, (jsize)written);
    if (written > 0) {
        (*env)->SetByteArrayRegion(env, result, 0, (jsize)written, (const jbyte*)buf);
    }
    free(buf);
    return result;
}

#define DEFINE_JNI_YUV_I420_TO_PACKED(fnName, cFunc) \
JNIEXPORT void JNI_FN(fnName)( \
    JNIEnv* env, jobject thiz, \
    jbyteArray srcY, jint srcStrideY, \
    jbyteArray srcU, jint srcStrideU, \
    jbyteArray srcV, jint srcStrideV, \
    jbyteArray dst, jint dstStride, \
    jint width, jint height \
) { \
    (void)thiz; \
    jbyte* yPtr = (*env)->GetByteArrayElements(env, srcY, NULL); \
    jbyte* uPtr = (*env)->GetByteArrayElements(env, srcU, NULL); \
    jbyte* vPtr = (*env)->GetByteArrayElements(env, srcV, NULL); \
    jbyte* dstPtr = (*env)->GetByteArrayElements(env, dst, NULL); \
    cFunc( \
        (const uint8_t*)yPtr, (int32_t)srcStrideY, \
        (const uint8_t*)uPtr, (int32_t)srcStrideU, \
        (const uint8_t*)vPtr, (int32_t)srcStrideV, \
        (uint8_t*)dstPtr, (int32_t)dstStride, \
        (int32_t)width, (int32_t)height \
    ); \
    (*env)->ReleaseByteArrayElements(env, srcY, yPtr, JNI_ABORT); \
    (*env)->ReleaseByteArrayElements(env, srcU, uPtr, JNI_ABORT); \
    (*env)->ReleaseByteArrayElements(env, srcV, vPtr, JNI_ABORT); \
    (*env)->ReleaseByteArrayElements(env, dst, dstPtr, 0); \
}

DEFINE_JNI_YUV_I420_TO_PACKED(yuvI420ToArgb, webrtc_yuv_i420_to_argb)
DEFINE_JNI_YUV_I420_TO_PACKED(yuvI420ToBgra, webrtc_yuv_i420_to_bgra)
DEFINE_JNI_YUV_I420_TO_PACKED(yuvI420ToAbgr, webrtc_yuv_i420_to_abgr)
DEFINE_JNI_YUV_I420_TO_PACKED(yuvI420ToRgba, webrtc_yuv_i420_to_rgba)

#define DEFINE_JNI_YUV_PACKED_TO_I420(fnName, cFunc) \
JNIEXPORT void JNI_FN(fnName)( \
    JNIEnv* env, jobject thiz, \
    jbyteArray src, jint srcStride, \
    jbyteArray dstY, jint dstStrideY, \
    jbyteArray dstU, jint dstStrideU, \
    jbyteArray dstV, jint dstStrideV, \
    jint width, jint height \
) { \
    (void)thiz; \
    jbyte* srcPtr = (*env)->GetByteArrayElements(env, src, NULL); \
    jbyte* yPtr = (*env)->GetByteArrayElements(env, dstY, NULL); \
    jbyte* uPtr = (*env)->GetByteArrayElements(env, dstU, NULL); \
    jbyte* vPtr = (*env)->GetByteArrayElements(env, dstV, NULL); \
    cFunc( \
        (const uint8_t*)srcPtr, (int32_t)srcStride, \
        (uint8_t*)yPtr, (int32_t)dstStrideY, \
        (uint8_t*)uPtr, (int32_t)dstStrideU, \
        (uint8_t*)vPtr, (int32_t)dstStrideV, \
        (int32_t)width, (int32_t)height \
    ); \
    (*env)->ReleaseByteArrayElements(env, src, srcPtr, JNI_ABORT); \
    (*env)->ReleaseByteArrayElements(env, dstY, yPtr, 0); \
    (*env)->ReleaseByteArrayElements(env, dstU, uPtr, 0); \
    (*env)->ReleaseByteArrayElements(env, dstV, vPtr, 0); \
}

DEFINE_JNI_YUV_PACKED_TO_I420(yuvArgbToI420, webrtc_yuv_argb_to_i420)
DEFINE_JNI_YUV_PACKED_TO_I420(yuvAbgrToI420, webrtc_yuv_abgr_to_i420)

JNIEXPORT void JNI_FN(yuvArgbToRgb24)(
    JNIEnv* env,
    jobject thiz,
    jbyteArray srcArgb,
    jint srcStrideArgb,
    jbyteArray dstRgb24,
    jint dstStrideRgb24,
    jint width,
    jint height
) {
    (void)thiz;
    jbyte* srcPtr = (*env)->GetByteArrayElements(env, srcArgb, NULL);
    jbyte* dstPtr = (*env)->GetByteArrayElements(env, dstRgb24, NULL);
    webrtc_yuv_argb_to_rgb24(
        (const uint8_t*)srcPtr, (int32_t)srcStrideArgb,
        (uint8_t*)dstPtr, (int32_t)dstStrideRgb24,
        (int32_t)width, (int32_t)height
    );
    (*env)->ReleaseByteArrayElements(env, srcArgb, srcPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, dstRgb24, dstPtr, 0);
}

JNIEXPORT void JNI_FN(yuvI420ToNv12)(
    JNIEnv* env,
    jobject thiz,
    jbyteArray srcY, jint srcStrideY,
    jbyteArray srcU, jint srcStrideU,
    jbyteArray srcV, jint srcStrideV,
    jbyteArray dstY, jint dstStrideY,
    jbyteArray dstUv, jint dstStrideUv,
    jint width, jint height
) {
    (void)thiz;
    jbyte* syPtr = (*env)->GetByteArrayElements(env, srcY, NULL);
    jbyte* suPtr = (*env)->GetByteArrayElements(env, srcU, NULL);
    jbyte* svPtr = (*env)->GetByteArrayElements(env, srcV, NULL);
    jbyte* dyPtr = (*env)->GetByteArrayElements(env, dstY, NULL);
    jbyte* duvPtr = (*env)->GetByteArrayElements(env, dstUv, NULL);
    webrtc_yuv_i420_to_nv12(
        (const uint8_t*)syPtr, (int32_t)srcStrideY,
        (const uint8_t*)suPtr, (int32_t)srcStrideU,
        (const uint8_t*)svPtr, (int32_t)srcStrideV,
        (uint8_t*)dyPtr, (int32_t)dstStrideY,
        (uint8_t*)duvPtr, (int32_t)dstStrideUv,
        (int32_t)width, (int32_t)height
    );
    (*env)->ReleaseByteArrayElements(env, srcY, syPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, srcU, suPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, srcV, svPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, dstY, dyPtr, 0);
    (*env)->ReleaseByteArrayElements(env, dstUv, duvPtr, 0);
}

JNIEXPORT void JNI_FN(yuvNv12ToI420)(
    JNIEnv* env,
    jobject thiz,
    jbyteArray srcY, jint srcStrideY,
    jbyteArray srcUv, jint srcStrideUv,
    jbyteArray dstY, jint dstStrideY,
    jbyteArray dstU, jint dstStrideU,
    jbyteArray dstV, jint dstStrideV,
    jint width, jint height
) {
    (void)thiz;
    jbyte* syPtr = (*env)->GetByteArrayElements(env, srcY, NULL);
    jbyte* suvPtr = (*env)->GetByteArrayElements(env, srcUv, NULL);
    jbyte* dyPtr = (*env)->GetByteArrayElements(env, dstY, NULL);
    jbyte* duPtr = (*env)->GetByteArrayElements(env, dstU, NULL);
    jbyte* dvPtr = (*env)->GetByteArrayElements(env, dstV, NULL);
    webrtc_yuv_nv12_to_i420(
        (const uint8_t*)syPtr, (int32_t)srcStrideY,
        (const uint8_t*)suvPtr, (int32_t)srcStrideUv,
        (uint8_t*)dyPtr, (int32_t)dstStrideY,
        (uint8_t*)duPtr, (int32_t)dstStrideU,
        (uint8_t*)dvPtr, (int32_t)dstStrideV,
        (int32_t)width, (int32_t)height
    );
    (*env)->ReleaseByteArrayElements(env, srcY, syPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, srcUv, suvPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, dstY, dyPtr, 0);
    (*env)->ReleaseByteArrayElements(env, dstU, duPtr, 0);
    (*env)->ReleaseByteArrayElements(env, dstV, dvPtr, 0);
}

JNIEXPORT void JNI_FN(yuvNv12ToArgb)(
    JNIEnv* env,
    jobject thiz,
    jbyteArray srcY, jint srcStrideY,
    jbyteArray srcUv, jint srcStrideUv,
    jbyteArray dstArgb, jint dstStrideArgb,
    jint width, jint height
) {
    (void)thiz;
    jbyte* syPtr = (*env)->GetByteArrayElements(env, srcY, NULL);
    jbyte* suvPtr = (*env)->GetByteArrayElements(env, srcUv, NULL);
    jbyte* dstPtr = (*env)->GetByteArrayElements(env, dstArgb, NULL);
    webrtc_yuv_nv12_to_argb(
        (const uint8_t*)syPtr, (int32_t)srcStrideY,
        (const uint8_t*)suvPtr, (int32_t)srcStrideUv,
        (uint8_t*)dstPtr, (int32_t)dstStrideArgb,
        (int32_t)width, (int32_t)height
    );
    (*env)->ReleaseByteArrayElements(env, srcY, syPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, srcUv, suvPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, dstArgb, dstPtr, 0);
}

JNIEXPORT void JNI_FN(yuvNv12ToAbgr)(
    JNIEnv* env,
    jobject thiz,
    jbyteArray srcY, jint srcStrideY,
    jbyteArray srcUv, jint srcStrideUv,
    jbyteArray dstAbgr, jint dstStrideAbgr,
    jint width, jint height
) {
    (void)thiz;
    jbyte* syPtr = (*env)->GetByteArrayElements(env, srcY, NULL);
    jbyte* suvPtr = (*env)->GetByteArrayElements(env, srcUv, NULL);
    jbyte* dstPtr = (*env)->GetByteArrayElements(env, dstAbgr, NULL);
    webrtc_yuv_nv12_to_abgr(
        (const uint8_t*)syPtr, (int32_t)srcStrideY,
        (const uint8_t*)suvPtr, (int32_t)srcStrideUv,
        (uint8_t*)dstPtr, (int32_t)dstStrideAbgr,
        (int32_t)width, (int32_t)height
    );
    (*env)->ReleaseByteArrayElements(env, srcY, syPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, srcUv, suvPtr, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, dstAbgr, dstPtr, 0);
}
