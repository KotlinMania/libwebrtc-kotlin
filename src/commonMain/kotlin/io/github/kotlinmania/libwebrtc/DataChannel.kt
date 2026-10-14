// port-lint: source data_channel.rs
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

package io.github.kotlinmania.libwebrtc

import io.github.kotlinmania.libwebrtc.native.NativeDataState
import io.github.kotlinmania.libwebrtc.native.DataChannel as ImpDataChannel

public fun interface OnStateChange {
    public fun onStateChange(state: DataChannelState)
}

public fun interface OnMessage {
    public fun onMessage(buffer: DataBuffer)
}

public fun interface OnBufferedAmountChange {
    public fun onBufferedAmountChange(sentDataSize: ULong)
}

public enum class DataChannelState {
    Connecting,
    Open,
    Closing,
    Closed,
}

public sealed class DataChannelError(
    public val errorDescription: String,
) : RuntimeException(errorDescription) {
    public object Send : DataChannelError("failure to send data on the data channel")

    public class Utf8(
        public val reason: String,
    ) : DataChannelError("utf-8 error: $reason")
}

public typealias DataChannelException = DataChannelError

public class DataChannelInit(
    public val ordered: Boolean = true,
    public val maxRetransmitTime: Int? = null,
    public val maxRetransmits: Int? = null,
    public val protocol: String = "",
    public val negotiated: Boolean = false,
    public val id: Int = -1,
) {
    public companion object {
        public fun default(): DataChannelInit = DataChannelInit()
    }
}

public class DataBuffer(
    public val data: ByteArray,
    public val binary: Boolean = true,
)

public class DataChannel internal constructor(
    internal val handle: ImpDataChannel,
) {
    public constructor(
        label: String,
        id: Int = 0,
        state: DataChannelState = DataChannelState.Open,
    ) : this(
        handle =
            ImpDataChannel(
                channelLabel = label,
                channelId = id,
                currentState =
                    when (state) {
                        DataChannelState.Connecting -> NativeDataState.Connecting
                        DataChannelState.Open -> NativeDataState.Open
                        DataChannelState.Closing -> NativeDataState.Closing
                        DataChannelState.Closed -> NativeDataState.Closed
                    },
            ),
    )

    internal val label: String
        get() = handle.label()

    internal val id: Int
        get() = handle.id()

    public fun send(data: ByteArray, binary: Boolean) {
        handle.send(data, binary)
    }

    public fun id(): Int = handle.id()

    public fun label(): String = handle.label()

    public fun state(): DataChannelState = handle.state()

    public fun close() {
        handle.close()
    }

    public fun bufferedAmount(): ULong = handle.bufferedAmount()

    public fun onStateChange(handler: OnStateChange?) {
        handle.onStateChange(handler)
    }

    public fun onMessage(handler: OnMessage?) {
        handle.onMessage(handler)
    }

    public fun onBufferedAmountChange(handler: OnBufferedAmountChange?) {
        handle.onBufferedAmountChange(handler)
    }

    override fun toString(): String =
        "DataChannel(id=${id()}, label=${label()}, state=${state()})"
}
