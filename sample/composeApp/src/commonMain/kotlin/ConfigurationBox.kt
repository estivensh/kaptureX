import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import camerakmp.sample.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ConfigurationBox(
    modifier: Modifier = Modifier,
    onConfigurationClick: () -> Unit,
) {
    Box(modifier) {
        Button(
            modifier = Modifier.clip(CircleShape),
            contentPaddingValues = PaddingValues(16.dp),
            onClick = onConfigurationClick,
        ) {
            Image(
                painter = painterResource(Res.drawable.configuration),
                contentDescription = stringResource(Res.string.configuration)
            )
        }
    }
}