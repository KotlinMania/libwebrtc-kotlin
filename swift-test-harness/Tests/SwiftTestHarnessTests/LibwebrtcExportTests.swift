import Testing
import Libwebrtc

@Suite("Libwebrtc Export Smoke Tests")
struct LibwebrtcExportTests {
    @Test("Swift module loads cleanly")
    func testSwiftModuleLoads() throws {
        #expect(true)
    }
}
