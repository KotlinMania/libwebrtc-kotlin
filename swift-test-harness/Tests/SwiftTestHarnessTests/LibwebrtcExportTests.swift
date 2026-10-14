import Testing
import Libwebrtc

@Suite("Libwebrtc Export Smoke Tests")
struct LibwebrtcExportTests {
    @Test("Swift module loads cleanly and exercises exported WebRTC types")
    func testSwiftModuleLoads() throws {
        let frame = AudioFrame(sampleRate: 48000, numChannels: 2, samplesPerChannel: 480)
        #expect(frame.sampleRate == 48000)
        #expect(frame.numChannels == 2)
        #expect(frame.samplesPerChannel == 480)

        let factory = PeerConnectionFactory.Companion.shared.default()
        let pc = factory.createPeerConnection(config: RtcConfiguration.Companion.shared.default())
        #expect(pc.connectionState() == PeerConnectionState.new)
        pc.close()
        #expect(pc.connectionState() == PeerConnectionState.closed)
    }
}
