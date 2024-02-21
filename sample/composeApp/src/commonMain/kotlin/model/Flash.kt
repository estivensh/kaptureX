package model

/*import camerakmp.sample.composeapp.generated.resources.Res
import camerakmp.sample.composeapp.generated.resources.flash_always
import camerakmp.sample.composeapp.generated.resources.flash_auto
import camerakmp.sample.composeapp.generated.resources.flash_off
import camerakmp.sample.composeapp.generated.resources.flash_on
import org.jetbrains.compose.resources.DrawableResource*/
import org.jetbrains.compose.resources.ExperimentalResourceApi
//import org.jetbrains.compose.resources.StringResource
import state.FlashMode

@OptIn(ExperimentalResourceApi::class)
enum class Flash(
   /* val drawableRes: DrawableResource,
    val contentRes: StringResource,*/
    val drawableRes: String,
    val contentRes: String,
) {
    Off("Res.drawable.flash_off", "Res.string.flash_off"),
    On("Res.drawable.flash_on", "Res.string.flash_on"),
    Auto("Res.drawable.flash_auto", "Res.string.flash_auto"),
    Always("Res.drawable.flash_always", "Res.string.flash_always");

    companion object {
        fun getCurrentValues(isVideo: Boolean) = when {
            isVideo -> listOf(Off, Always)
            else -> entries
        }
    }
}

expect fun Flash.toFlashMode(): FlashMode

expect fun FlashMode.toFlash(isTorchEnabled: Boolean): Flash