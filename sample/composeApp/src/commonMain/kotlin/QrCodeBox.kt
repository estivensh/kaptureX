import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun QrCodeBox(modifier: Modifier = Modifier, qrCodeText: String?) {
    var latestQrCode by remember(Unit) { mutableStateOf(qrCodeText.orEmpty()) }
    val uriHandler = LocalUriHandler.current
    var showQrCode by remember { mutableStateOf(false) }
    if (showQrCode) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        uriHandler.openUri(latestQrCode.ifEmpty { "https://google.com" })
                    }
                    .width(240.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                text = latestQrCode,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }

    LaunchedEffect(qrCodeText) {
        if (qrCodeText != null) {
            showQrCode = true
            latestQrCode = qrCodeText
        } else {
            delay(1000)
            showQrCode = false
        }
    }
}