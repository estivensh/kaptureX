import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import camera.Section
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GalleryScreen(
    onBackPressed: () -> Unit,
    onPreviewClick: (String) -> Unit,
) {
    Section(
        title = {
            Text("Gallery".replaceFirstChar { it.uppercase() })
        }, onBackPressed = onBackPressed
    ) {
        Box(Modifier.padding(it)) {
            GallerySection(
                onPreviewClick = onPreviewClick,
            )
        }
    }
}

@Composable
expect fun GallerySection(onPreviewClick: (String) -> Unit)

@Composable
fun GalleryAnimationVisibility(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier, enter = fadeIn(), exit = fadeOut(), visible = visible
    ) { content() }
}