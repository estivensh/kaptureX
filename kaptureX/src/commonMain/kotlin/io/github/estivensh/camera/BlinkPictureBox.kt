package io.github.estivensh.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.estivensh.extensions.ImageFile
import kotlinx.coroutines.delay

@Composable
fun BlinkPictureBox(lastPicture: ImageFile?, isVideo: Boolean) {
    var picture by remember(Unit) { mutableStateOf(lastPicture) }
    if (!isVideo && lastPicture != picture) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        LaunchedEffect(lastPicture) {
            delay(25)
            picture = lastPicture
        }
    }
}