// port-lint: source web/data_channel.rs
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

package io.github.kotlinmania.libwebrtc.web

import io.github.kotlinmania.libwebrtc.DataBuffer
import io.github.kotlinmania.libwebrtc.DataChannelError
import io.github.kotlinmania.libwebrtc.DataChannelState
import io.github.kotlinmania.libwebrtc.OnBufferedAmountChange
import io.github.kotlinmania.libwebrtc.OnMessage
import io.github.kotlinmania.libwebrtc.OnStateChange

internal enum class RtcDataChannelState {
    Connecting,
    Open,
    Closing,
    Closed,
    ;

    internal fun toDataState(): DataChannelState =
        when (this) {
            Connecting -> DataChannelState.Connecting
            Open -> DataChannelState.Open
            Closing -> DataChannelState.Closing
            Closed -> DataChannelState.Closed
        }
}

internal interface DataChannelTrait {
    fun send(data: ByteArray, binary: Boolean)

    fun label(): String

    fun state(): DataChannelState

    fun close()

    fun onStateChange(callback: OnStateChange?)

    fun onMessage(callback: OnMessage?)

    fun onBufferedAmountChange(callback: OnBufferedAmountChange?)
}

internal class DataChannel(
    private val channelLabel: String = "",
    private var readyState: RtcDataChannelState = RtcDataChannelState.Open,
) : DataChannelTrait {
    private var onClosing: OnStateChange? = null
    private var stateChangeCallback: OnStateChange? = null
    private var messageCallback: OnMessage? = null
    private var bufferedAmountChangeCallback: OnBufferedAmountChange? = null

    override fun send(data: ByteArray, binary: Boolean) {
        if (readyState != RtcDataChannelState.Open) {
            throw DataChannelError.Send
        }
        if (!binary) {
            try {
                data.decodeToString(throwOnInvalidSequence = true)
            } catch (e: CharacterCodingException) {
                throw DataChannelError.Utf8(e.message ?: "invalid utf-8 sequence")
            }
        }
        messageCallback?.onMessage(DataBuffer(data = data, binary = binary))
    }

    override fun label(): String = channelLabel

    override fun state(): DataChannelState = readyState.toDataState()

    override fun close() {
        readyState = RtcDataChannelState.Closing
        onClosing?.onStateChange(state())
        readyState = RtcDataChannelState.Closed
        stateChangeCallback?.onStateChange(state())
    }

    override fun onStateChange(callback: OnStateChange?) {
        if (callback != null) {
            stateChangeCallback = callback
            onClosing = callback
        } else {
            stateChangeCallback = null
            onClosing = null
        }
    }

    override fun onMessage(callback: OnMessage?) {
        messageCallback = callback
    }

    override fun onBufferedAmountChange(callback: OnBufferedAmountChange?) {
        bufferedAmountChangeCallback = callback
    }
}
