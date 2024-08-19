package io.github.estivensh.state

import platform.AVFoundation.AVMediaType
import platform.AVFoundation.AVMediaTypeVideo

actual enum class CaptureMode(
    val value: AVMediaType
) {
    Image(AVMediaTypeVideo),
    Video(AVMediaTypeVideo),
}