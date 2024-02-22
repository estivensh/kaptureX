package state

import platform.AVFoundation.AVLayerVideoGravity
import platform.AVFoundation.AVLayerVideoGravityResize
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill

actual enum class ScaleType(
    val type: AVLayerVideoGravity
) {
    Fit(AVLayerVideoGravityResize),
    Fill(AVLayerVideoGravityResizeAspectFill),
}