// port-lint: source native/data_channel.rs
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

import io.github.kotlinmania.libwebrtc.DataBuffer
import io.github.kotlinmania.libwebrtc.DataChannelError
import io.github.kotlinmania.libwebrtc.DataChannelInit
import io.github.kotlinmania.libwebrtc.DataChannelState
import io.github.kotlinmania.libwebrtc.OnBufferedAmountChange
import io.github.kotlinmania.libwebrtc.OnMessage
import io.github.kotlinmania.libwebrtc.OnStateChange
import io.github.kotlinmania.libwebrtc.Priority

internal enum class NativeDataState {
    Connecting,
    Open,
    Closing,
    Closed,
    ;

    internal fun toDataChannelState(): DataChannelState =
        when (this) {
            Connecting -> DataChannelState.Connecting
            Open -> DataChannelState.Open
            Closing -> DataChannelState.Closing
            Closed -> DataChannelState.Closed
        }
}

internal class NativeDataChannelInit(
    internal val ordered: Boolean,
    internal val hasMaxRetransmitTime: Boolean,
    internal val maxRetransmitTime: Int,
    internal val hasMaxRetransmits: Boolean,
    internal val maxRetransmits: Int,
    internal val protocol: String,
    internal val id: Int,
    internal val hasPriority: Boolean,
    internal val priority: Priority,
    internal val negotiated: Boolean,
) {
    internal companion object {
        internal fun from(value: DataChannelInit): NativeDataChannelInit =
            NativeDataChannelInit(
                ordered = value.ordered,
                hasMaxRetransmitTime = value.maxRetransmitTime != null,
                maxRetransmitTime = value.maxRetransmitTime ?: 0,
                hasMaxRetransmits = value.maxRetransmits != null,
                maxRetransmits = value.maxRetransmits ?: 0,
                protocol = value.protocol,
                id = value.id,
                hasPriority = false,
                priority = Priority.Medium,
                negotiated = value.negotiated,
            )
    }
}

internal class DataChannelObserver {
    internal var stateChangeHandler: OnStateChange? = null
    internal var messageHandler: OnMessage? = null
    internal var bufferedAmountChangeHandler: OnBufferedAmountChange? = null

    internal fun onStateChange(state: NativeDataState) {
        stateChangeHandler?.onStateChange(state.toDataChannelState())
    }

    internal fun onMessage(data: ByteArray, binary: Boolean) {
        messageHandler?.onMessage(DataBuffer(data = data, binary = binary))
    }

    internal fun onBufferedAmountChange(sentDataSize: ULong) {
        bufferedAmountChangeHandler?.onBufferedAmountChange(sentDataSize)
    }
}

internal class DataChannel(
    private val channelLabel: String,
    private val channelId: Int = 0,
    private var currentState: NativeDataState = NativeDataState.Open,
    private var currentBufferedAmount: ULong = 0uL,
    internal val observer: DataChannelObserver = DataChannelObserver(),
) {
    internal var remotePeerChannel: DataChannel? = null

    internal fun send(data: ByteArray, binary: Boolean) {
        if (!binary) {
            try {
                data.decodeToString(throwOnInvalidSequence = true)
            } catch (e: CharacterCodingException) {
                throw DataChannelError.Utf8(e.message ?: "invalid utf-8 sequence")
            }
        }
        if (currentState != NativeDataState.Open) {
            throw DataChannelError.Send
        }
        currentBufferedAmount += data.size.toULong()
        observer.onBufferedAmountChange(currentBufferedAmount)
        val remote = remotePeerChannel
        if (remote != null) {
            remote.observer.onMessage(data, binary)
        } else {
            observer.onMessage(data, binary)
        }
    }

    internal fun id(): Int = channelId

    internal fun label(): String = channelLabel

    internal fun state(): DataChannelState = currentState.toDataChannelState()

    internal fun close() {
        currentState = NativeDataState.Closed
        observer.onStateChange(currentState)
    }

    internal fun bufferedAmount(): ULong = currentBufferedAmount

    internal fun onStateChange(handler: OnStateChange?) {
        observer.stateChangeHandler = handler
    }

    internal fun onMessage(handler: OnMessage?) {
        observer.messageHandler = handler
    }

    internal fun onBufferedAmountChange(handler: OnBufferedAmountChange?) {
        observer.bufferedAmountChangeHandler = handler
    }

    internal companion object {
        internal fun configure(
            label: String,
            init: DataChannelInit = DataChannelInit(),
            state: NativeDataState = NativeDataState.Open,
        ): DataChannel {
            val nativeInit = NativeDataChannelInit.from(init)
            val resolvedId = if (nativeInit.id >= 0) nativeInit.id else 0
            return DataChannel(
                channelLabel = label,
                channelId = resolvedId,
                currentState = state,
                observer = DataChannelObserver(),
            )
        }
    }
}
