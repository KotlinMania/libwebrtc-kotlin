// port-lint: source libwebrtc/libwebrtc/src/video_frame.rs
package io.github.kotlinmania.libwebrtc

public enum class VideoRotation(public val degrees: Int) {
    VideoRotation0(0),
    VideoRotation90(90),
    VideoRotation180(180),
    VideoRotation270(270),
}

public enum class VideoFormatType {
    Argb,
    Bgra,
    Abgr,
    Rgba,
}

public enum class VideoBufferType {
    Native,
    I420,
    I420A,
    I422,
    I444,
    I010,
    Nv12,
}

public interface VideoBuffer {
    public val width: Int
    public val height: Int
    public val type: VideoBufferType

    public fun toI420(): I420Buffer
}

public data class I420Buffer(
    override val width: Int,
    override val height: Int,
    val dataY: ByteArray,
    val strideY: Int,
    val dataU: ByteArray,
    val strideU: Int,
    val dataV: ByteArray,
    val strideV: Int,
) : VideoBuffer {
    override val type: VideoBufferType = VideoBufferType.I420

    override fun toI420(): I420Buffer = this

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is I420Buffer) return false
        return width == other.width &&
            height == other.height &&
            strideY == other.strideY &&
            strideU == other.strideU &&
            strideV == other.strideV &&
            dataY.contentEquals(other.dataY) &&
            dataU.contentEquals(other.dataU) &&
            dataV.contentEquals(other.dataV)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + dataY.contentHashCode()
        result = 31 * result + dataU.contentHashCode()
        result = 31 * result + dataV.contentHashCode()
        return result
    }
}

public data class VideoFrame(
    val rotation: VideoRotation,
    val timestampUs: Long,
    val buffer: VideoBuffer,
)
