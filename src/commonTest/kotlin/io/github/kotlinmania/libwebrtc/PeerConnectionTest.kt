// port-lint: tests peer_connection.rs
package io.github.kotlinmania.libwebrtc

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PeerConnectionTest {
    @Test
    fun createPc() =
        runTest {
            val factory = PeerConnectionFactory.default()
            val config =
                RtcConfiguration(
                    iceServers =
                        listOf(
                            IceServer(
                                urls = listOf("stun:stun1.l.google.com:19302"),
                                username = "",
                                password = "",
                            ),
                        ),
                    continualGatheringPolicy = ContinualGatheringPolicy.GatherOnce,
                    iceTransportType = IceTransportsType.All,
                )

            val bob = factory.createPeerConnection(config)
            val alice = factory.createPeerConnection(config)

            val bobIceRx = Channel<IceCandidate>(Channel.UNLIMITED)
            val aliceIceRx = Channel<IceCandidate>(Channel.UNLIMITED)
            val aliceDcRx = Channel<DataChannel>(Channel.UNLIMITED)

            bob.onIceCandidate { candidate ->
                bobIceRx.trySend(candidate)
            }

            alice.onIceCandidate { candidate ->
                aliceIceRx.trySend(candidate)
            }

            alice.onDataChannel { dc ->
                aliceDcRx.trySend(dc)
            }

            val bobDc = bob.createDataChannel("test_dc", DataChannelInit.default())

            val offer = bob.createOffer(OfferOptions.default())
            bob.setLocalDescription(offer)
            alice.setRemoteDescription(offer)

            val answer = alice.createAnswer(AnswerOptions.default())
            alice.setLocalDescription(answer)
            bob.setRemoteDescription(answer)

            val bobIce = bobIceRx.receive()
            val aliceIce = aliceIceRx.receive()

            bob.addIceCandidate(aliceIce)
            alice.addIceCandidate(bobIce)

            val dataRx = Channel<String>(Channel.UNLIMITED)
            val aliceDc = aliceDcRx.receive()
            aliceDc.onMessage { buffer ->
                dataRx.trySend(buffer.data.decodeToString())
            }

            bobDc.send("This is a test".encodeToByteArray(), true)
            assertEquals("This is a test", dataRx.receive())

            alice.close()
            bob.close()
        }
}
