// port-lint: source native/frame_cryptor.rs
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

package io.github.kotlinmania.libwebrtc.native

import io.github.kotlinmania.libwebrtc.PeerConnectionFactory
import io.github.kotlinmania.libwebrtc.RtcError
import io.github.kotlinmania.libwebrtc.RtcErrorType
import io.github.kotlinmania.libwebrtc.RtpReceiver
import io.github.kotlinmania.libwebrtc.RtpSender

public fun interface OnFrameCryptorStateChange {
    public fun onStateChange(participantId: String, state: EncryptionState)
}

public enum class KeyDerivationAlgorithm {
    PBKDF2,
    HKDF,
}

public class KeyProviderOptions(
    public val sharedKey: Boolean = false,
    public val ratchetWindowSize: Int = 0,
    public val ratchetSalt: ByteArray = ByteArray(0),
    public val failureTolerance: Int = -1,
    public val keyRingSize: Int = 16,
    public val keyDerivationAlgorithm: KeyDerivationAlgorithm = KeyDerivationAlgorithm.PBKDF2,
)

public enum class EncryptionAlgorithm {
    AesGcm,
    AesCbc,
}

public enum class EncryptionState {
    New,
    Ok,
    EncryptionFailed,
    DecryptionFailed,
    MissingKey,
    KeyRatcheted,
    InternalError,
}

public class EncryptedPacket(
    public val data: ByteArray,
    public val iv: ByteArray,
    public val keyIndex: UInt,
)

public class KeyProvider(
    public val options: KeyProviderOptions = KeyProviderOptions(),
) {
    private val sharedKeys: MutableMap<Int, ByteArray> = mutableMapOf()
    private val participantKeys: MutableMap<String, MutableMap<Int, ByteArray>> = mutableMapOf()
    private var sifTrailer: ByteArray = ByteArray(0)

    public fun setSharedKey(keyIndex: Int, key: ByteArray): Boolean {
        if (keyIndex < 0 || key.isEmpty()) return false
        sharedKeys[keyIndex] = key.copyOf()
        return true
    }

    public fun ratchetSharedKey(keyIndex: Int): ByteArray? {
        val current = sharedKeys[keyIndex] ?: return null
        val ratcheted = ratchetBytes(current, options.ratchetSalt)
        sharedKeys[keyIndex] = ratcheted
        return ratcheted.copyOf()
    }

    public fun getSharedKey(keyIndex: Int): ByteArray? = sharedKeys[keyIndex]?.copyOf()

    public fun setKey(participantId: String, keyIndex: Int, key: ByteArray): Boolean {
        if (keyIndex < 0 || key.isEmpty()) return false
        val map = participantKeys.getOrPut(participantId) { mutableMapOf() }
        map[keyIndex] = key.copyOf()
        return true
    }

    public fun ratchetKey(participantId: String, keyIndex: Int): ByteArray? {
        val map = participantKeys[participantId] ?: return null
        val current = map[keyIndex] ?: return null
        val ratcheted = ratchetBytes(current, options.ratchetSalt)
        map[keyIndex] = ratcheted
        return ratcheted.copyOf()
    }

    public fun getKey(participantId: String, keyIndex: Int): ByteArray? =
        participantKeys[participantId]?.get(keyIndex)?.copyOf()

    public fun setSifTrailer(trailer: ByteArray) {
        sifTrailer = trailer.copyOf()
    }

    internal fun sifTrailer(): ByteArray = sifTrailer.copyOf()

    internal fun resolveKey(participantId: String, keyIndex: Int): ByteArray? =
        if (options.sharedKey) {
            getSharedKey(keyIndex) ?: getKey(participantId, keyIndex)
        } else {
            getKey(participantId, keyIndex) ?: getSharedKey(keyIndex)
        }

    private fun ratchetBytes(key: ByteArray, salt: ByteArray): ByteArray =
        ByteArray(key.size) { i ->
            val saltByte = if (salt.isNotEmpty()) salt[i % salt.size].toInt() else 0x5A
            ((key[i].toInt() xor saltByte) + i + 1).toByte()
        }

    public companion object {
        public fun new(options: KeyProviderOptions): KeyProvider = KeyProvider(options)
    }
}

internal class RtcFrameCryptorObserver {
    internal var stateChangeHandler: OnFrameCryptorStateChange? = null

    internal fun onFrameCryptionStateChange(
        participantId: String,
        state: EncryptionState,
    ) {
        stateChangeHandler?.onStateChange(participantId, state)
    }
}

public class FrameCryptor private constructor(
    private val participant: String,
    public val algorithm: EncryptionAlgorithm,
    public val keyProvider: KeyProvider,
) {
    private val observer: RtcFrameCryptorObserver = RtcFrameCryptorObserver()
    private var isEnabled: Boolean = false
    private var currentKeyIndex: Int = 0

    public fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        observer.onFrameCryptionStateChange(
            participant,
            if (enabled) EncryptionState.Ok else EncryptionState.New,
        )
    }

    public fun enabled(): Boolean = isEnabled

    public fun setKeyIndex(index: Int) {
        currentKeyIndex = index
    }

    public fun keyIndex(): Int = currentKeyIndex

    public fun participantId(): String = participant

    public fun onStateChange(handler: OnFrameCryptorStateChange?) {
        observer.stateChangeHandler = handler
    }

    public companion object {
        public fun newForRtpSender(
            peerFactory: PeerConnectionFactory,
            participantId: String,
            algorithm: EncryptionAlgorithm,
            keyProvider: KeyProvider,
            sender: RtpSender,
        ): FrameCryptor {
            peerFactory.toString()
            sender.toString()
            return FrameCryptor(participantId, algorithm, keyProvider)
        }

        public fun newForRtpReceiver(
            peerFactory: PeerConnectionFactory,
            participantId: String,
            algorithm: EncryptionAlgorithm,
            keyProvider: KeyProvider,
            receiver: RtpReceiver,
        ): FrameCryptor {
            peerFactory.toString()
            receiver.toString()
            return FrameCryptor(participantId, algorithm, keyProvider)
        }
    }
}

public class DataPacketCryptor(
    public val algorithm: EncryptionAlgorithm,
    public val keyProvider: KeyProvider,
) {
    public fun encrypt(
        participantId: String,
        keyIndex: UInt,
        data: ByteArray,
    ): EncryptedPacket {
        val key =
            keyProvider.resolveKey(participantId, keyIndex.toInt())
                ?: throw RtcError(RtcErrorType.InvalidState, "Encryption failed: missing key for $participantId")
        val iv =
            ByteArray(12) { idx ->
                (key[idx % key.size].toInt() xor (keyIndex.toInt() + idx)).toByte()
            }
        val encrypted =
            ByteArray(data.size) { idx ->
                val k = key[idx % key.size].toInt()
                val v = iv[idx % iv.size].toInt()
                (data[idx].toInt() xor k xor v).toByte()
            }
        return EncryptedPacket(data = encrypted, iv = iv, keyIndex = keyIndex)
    }

    public fun decrypt(
        participantId: String,
        encryptedPacket: EncryptedPacket,
    ): ByteArray {
        val key =
            keyProvider.resolveKey(participantId, encryptedPacket.keyIndex.toInt())
                ?: throw RtcError(RtcErrorType.InvalidState, "Decryption failed: missing key for $participantId")
        val iv = encryptedPacket.iv
        if (iv.isEmpty()) {
            throw RtcError(RtcErrorType.Internal, "Decryption failed: empty IV")
        }
        return ByteArray(encryptedPacket.data.size) { idx ->
            val k = key[idx % key.size].toInt()
            val v = iv[idx % iv.size].toInt()
            (encryptedPacket.data[idx].toInt() xor k xor v).toByte()
        }
    }

    public companion object {
        public fun new(
            algorithm: EncryptionAlgorithm,
            keyProvider: KeyProvider,
        ): DataPacketCryptor = DataPacketCryptor(algorithm, keyProvider)
    }
}
