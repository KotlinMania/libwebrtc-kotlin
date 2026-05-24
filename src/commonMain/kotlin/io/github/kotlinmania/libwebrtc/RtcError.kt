// port-lint: source src/lib.rs
package io.github.kotlinmania.libwebrtc

// Upstream derives thiserror's #[error("an RtcError occured: {error_type:?} - {message}")].
// In Kotlin the formatted message is composed eagerly at construction time.
public class RtcError(
    public val errorType: RtcErrorType,
    public val rtcMessage: String,
) : RuntimeException("an RtcError occured: $errorType - $rtcMessage") {
    // Upstream Rust field is named `message`; Kotlin's Throwable already owns
    // a nullable `message`, so the upstream field is exposed under
    // `rtcMessage` to avoid shadowing the standard exception accessor while
    // still surfacing the raw, unformatted text.
}
