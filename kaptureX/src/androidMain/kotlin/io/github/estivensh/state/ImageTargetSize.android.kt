package io.github.estivensh.state

import android.util.Size
import androidx.camera.view.CameraController.OutputSize

actual class ImageTargetSize(
    private var aspectRatio: Int? = null,
    private var size: Size? = null,
    private var outputSize: OutputSize? = null
) {
    constructor(aspectRatio: Int?) : this(
        aspectRatio = aspectRatio,
        size = null,
        outputSize = null
    )

    constructor(size: Size?) : this(
        size = size,
        aspectRatio = null,
        outputSize = null
    )

    constructor(outputSize: OutputSize?) : this(
        outputSize = outputSize,
        aspectRatio = null,
        size = null
    )

    internal fun toOutputSize(): OutputSize? {
        return outputSize ?: aspectRatio?.let { OutputSize(it) } ?: size?.let { OutputSize(it) }
    }
}