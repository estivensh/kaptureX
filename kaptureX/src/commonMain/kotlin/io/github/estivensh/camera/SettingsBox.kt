package io.github.estivensh.camera

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.estivensh.camera.model.Flash
import io.github.estivensh.extensions.roundTo
import kotlinx.coroutines.delay

@Composable
fun SettingsBox(
    modifier: Modifier = Modifier,
    zoomRatio: Float,
    zoomHasChanged: Boolean,
    flashMode: Flash,
    isRecording: Boolean,
    isVideo: Boolean,
    hasFlashUnit: Boolean,
    onFlashModeChanged: (Flash) -> Unit,
    onZoomFinish: () -> Unit,
) {
    Box(modifier = modifier) {
        FlashBox(
            modifier = Modifier.align(Alignment.TopStart),
            hasFlashUnit = hasFlashUnit,
            flashMode = flashMode,
            isVideo = isVideo,
            onFlashModeChanged = onFlashModeChanged
        )
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VideoBox(
                modifier = Modifier.padding(top = 16.dp),
                isRecording = isRecording,
            )
            AnimatedVisibility(
                modifier = Modifier.padding(top = 16.dp),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                visible = zoomHasChanged
            ) {
                Text(
                    text = "${zoomRatio.roundTo(1)}X",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
            }
        }
    }
    LaunchedEffect(zoomRatio, zoomHasChanged) {
        delay(1_000)
        onZoomFinish()
    }
}