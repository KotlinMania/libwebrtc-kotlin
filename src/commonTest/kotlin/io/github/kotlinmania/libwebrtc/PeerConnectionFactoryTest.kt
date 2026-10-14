// port-lint: tests native/peer_connection_factory.rs
package io.github.kotlinmania.libwebrtc

import kotlin.test.Test
import kotlin.test.assertEquals

class PeerConnectionFactoryTest {
    @Test
    fun testPeerConnectionFactory() {
        val factory = PeerConnectionFactory.default()
        val source = NativeVideoSource.default()
        val track = factory.createVideoTrack("test", source)
        assertEquals("test", track.id())
        assertEquals(TrackState.Live, track.state())
    }
}
