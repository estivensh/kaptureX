import android.os.Environment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camerakmp.sample.composeapp.generated.resources.Res
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFramePercent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
actual fun GallerySection(onPreviewClick: (String) -> Unit) {
    val externalDir = "${Environment.DIRECTORY_DCIM}${File.separator}Camposer"
    val externalStorage =
        Environment.getExternalStoragePublicDirectory(externalDir).apply { mkdirs() }
    val externalFiles =
        externalStorage.listFiles()?.sortedByDescending { it.lastModified() }.orEmpty()
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {


        items(externalFiles, { it.name }) { image ->
            val context = LocalContext.current
            var duration by rememberSaveable { mutableStateOf<Int?>(null) }
            LaunchedEffect(Unit) { duration = image.getDuration(context) }
            PlaceholderImage(
                modifier = Modifier
                    .fillMaxSize()
                    .animateItemPlacement()
                    .aspectRatio(1F)
                    .clickable(onClick = { onPreviewClick(image.path) }),
                data = image,
                contentDescription = image.name,
                placeholder = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                },
            ) {
                duration?.let { duration ->
                    Box(
                        modifier = Modifier.background(Color.Black.copy(0.25F)),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "${duration.minutes}:${duration.seconds}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            )
                            Icon(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.White, CircleShape),
                                imageVector = Icons.Rounded.PlayArrow,
                                tint = Color.Black,
                                contentDescription = org.jetbrains.compose.resources.stringResource(
                                    Res.string.play
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderImage(
    modifier: Modifier = Modifier,
    data: Any,
    placeholder: @Composable () -> Unit,
    contentDescription: String?,
    innerContent: @Composable () -> Unit,
) {
    var imageState: AsyncImagePainter.State by remember { mutableStateOf(AsyncImagePainter.State.Empty) }
    Box(modifier) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .decoderFactory(VideoFrameDecoder.Factory())
                .videoFramePercent(0.5)
                .build(),
            onState = { imageState = it },
            contentScale = ContentScale.Crop,
            contentDescription = contentDescription,
        )
        GalleryAnimationVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = when (imageState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Success,
                -> false

                is AsyncImagePainter.State.Loading,
                is AsyncImagePainter.State.Error,
                -> true
            }
        ) { placeholder() }

        GalleryAnimationVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = when (imageState) {
                is AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Loading,
                is AsyncImagePainter.State.Error,
                -> false

                is AsyncImagePainter.State.Success -> true
            }
        ) { innerContent() }
    }

}
