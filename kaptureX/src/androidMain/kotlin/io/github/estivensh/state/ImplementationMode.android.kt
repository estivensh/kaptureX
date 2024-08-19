package io.github.estivensh.state

import androidx.camera.view.PreviewView

actual enum class ImplementationMode(
    internal val value: PreviewView.ImplementationMode
) {
    Compatible(PreviewView.ImplementationMode.COMPATIBLE),
    Performance(PreviewView.ImplementationMode.PERFORMANCE);

    public val inverse: io.github.estivensh.state.ImplementationMode
        get() = when (this) {
            Compatible -> Performance
            else -> Compatible
        }
}