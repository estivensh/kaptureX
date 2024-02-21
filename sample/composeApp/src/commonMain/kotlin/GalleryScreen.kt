import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import camerakmp.sample.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
//import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GalleryScreen(
    onBackPressed: () -> Unit,
    onPreviewClick: (String) -> Unit,
) {
    Section(
        title = {
            Text("stringResource(Res.string.gallery).replaceFirstChar { it.uppercase() }")
        }, onBackPressed = onBackPressed
    ) {
        Box(Modifier.padding(it)) {
            GallerySection(
                onPreviewClick = onPreviewClick,
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun GalleryEmpty() {
    Box(
        Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
            text = "stringResource(Res.string.gallery_empty_description).replaceFirstChar { it.uppercase() }",
            fontSize = 18.sp,
            color = Color.Gray,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
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

@Composable
private fun GalleryLoading() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}