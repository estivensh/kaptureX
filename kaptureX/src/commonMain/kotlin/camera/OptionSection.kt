package camera

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camera.model.CameraOption
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun OptionSection(
    modifier: Modifier = Modifier,
    currentCameraOption: CameraOption,
    isVideoSupported: Boolean,
    onCameraOptionChanged: (CameraOption) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        CameraOption.entries.forEach { option ->
            if (!isVideoSupported && option == CameraOption.Video) return@forEach

            Text(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onCameraOptionChanged(option) },
                    )
                    .padding(vertical = 4.dp)
                    .width(80.dp),
                text = stringResource(option.titleRes).replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (currentCameraOption == option) Color.Yellow else Color.White
            )
        }
    }
}