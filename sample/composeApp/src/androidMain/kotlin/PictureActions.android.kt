import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import camerakmp.sample.composeapp.generated.resources.Res
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import extensions.ImageFile
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun GalleryButton(lastPicture: ImageFile?, onClick: () -> Unit) {
    var shouldAnimate by remember { mutableStateOf(false) }
    val animScale by animateFloatAsState(targetValue = if (shouldAnimate) 1.25F else 1F, label = "gallery_button_animate")

    AsyncImage(
        modifier = Modifier
            .scale(animScale)
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5F), CircleShape)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop,
        model = ImageRequest.Builder(LocalContext.current)
            .data(lastPicture?.uri)
            .decoderFactory(VideoFrameDecoder.Factory())
            .videoFrameMillis(1)
            .build(),
        contentDescription = stringResource(Res.string.gallery)
    )

    LaunchedEffect(lastPicture) {
        shouldAnimate = true
        delay(50)
        shouldAnimate = false
    }
}