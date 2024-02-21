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
import androidx.compose.ui.unit.dp
import camerakmp.sample.composeapp.generated.resources.Res
import extensions.ImageFile
import extensions.toByteArray

import kotlinx.cinterop.reinterpret
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skia.Bitmap
import platform.UIKit.UIImageJPEGRepresentation
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import org.jetbrains.skia.Image
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

@Composable
actual fun GalleryButton(lastPicture: ImageFile?, onClick: () -> Unit) {
    var shouldAnimate by remember { mutableStateOf(false) }
    val animScale by animateFloatAsState(
        targetValue = if (shouldAnimate) 1.25F else 1F,
        label = "gallery_button_animate"
    )

    Image(
        modifier = Modifier
            .scale(animScale)
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5F), CircleShape)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop,
        bitmap = toImageBitmap(lastPicture) ?: ImageBitmap(100,100),
        contentDescription = stringResource(Res.string.gallery)
    )

    LaunchedEffect(lastPicture) {
        shouldAnimate = true
        delay(50)
        shouldAnimate = false
    }
}

 @OptIn(ExperimentalForeignApi::class)
 fun toByteArray(image: UIImage?): ByteArray? {
    return if (image != null) {
        val imageData = UIImageJPEGRepresentation(image, 0.99)
            ?: throw IllegalArgumentException("image data is null")
        val bytes = imageData.bytes ?: throw IllegalArgumentException("image bytes is null")
        val length = imageData.length

        val data: CPointer<ByteVar> = bytes.reinterpret()
        ByteArray(length.toInt()) { index -> data[index] }
    } else {
        null
    }

}

 fun toImageBitmap(image: UIImage?): ImageBitmap? {
    val byteArray = toByteArray(image)
    return if (byteArray != null) {
        Image.makeFromEncoded(byteArray).toComposeImageBitmap()
    } else {
        null
    }
}
