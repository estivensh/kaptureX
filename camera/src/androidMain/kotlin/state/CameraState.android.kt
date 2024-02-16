package state

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.TorchState
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.CameraController.OutputSize.UNASSIGNED_ASPECT_RATIO
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import extensions.compatMainExecutor
import extensions.isImageAnalysisSupported

actual class CameraState(private val context: Context) {

    actual val controller: LifecycleCameraController = LifecycleCameraController(context)
    private val mainExecutor = context.compatMainExecutor
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    init {
        controller.initializationFuture.addListener(
            {
                resetCamera()
            },
            mainExecutor
        )
    }

    private fun resetCamera() {
        hasFlashUnit = controller.cameraInfo?.hasFlashUnit() ?: false
        startZoom()
    }

    internal actual var flashMode: FlashMode
        get() = FlashMode.find(controller.imageCaptureFlashMode)
        set(value) {
            if (hasFlashUnit && flashMode != value) {
                controller.imageCaptureFlashMode = value.mode
            }
        }
    internal actual var hasFlashUnit: Boolean
            by mutableStateOf(controller.cameraInfo?.hasFlashUnit() ?: true)
    actual val isZoomSupported: Boolean
            by derivedStateOf { maxZoom != 1F }
    actual var maxZoom: Float
            by mutableFloatStateOf(controller.zoomState.value?.maxZoomRatio ?: INITIAL_ZOOM_VALUE)

    actual companion object {
        private val TAG = this::class.java.name
        actual val INITIAL_ZOOM_VALUE: Float = 1F
        actual val INITIAL_EXPOSURE_VALUE: Int = 0
    }

    actual var minZoom: Float
            by mutableFloatStateOf(controller.zoomState.value?.minZoomRatio ?: INITIAL_ZOOM_VALUE)

    actual fun startZoom() {
        controller.isPinchToZoomEnabled = false
        val zoom = controller.zoomState.value
        minZoom = zoom?.minZoomRatio ?: INITIAL_ZOOM_VALUE
        maxZoom = zoom?.maxZoomRatio ?: INITIAL_ZOOM_VALUE
    }

    internal actual var scaleType: ScaleType = ScaleType.FillCenter
    internal actual var camSelector: CamSelector = CamSelector.Back
        set(value) {
            when {
                value == field -> Unit
                !isRecording && hasCamera(value) -> {
                    if (controller.cameraSelector != value.selector) {
                        controller.cameraSelector = value.selector
                        field = value
                        resetCamera()
                    }
                }

                isRecording -> Log.e(TAG, "Device is recording, switch camera is unavailable")
                else -> Log.e(TAG, "Device does not have ${value.selector} camera")
            }
        }
    actual var isRecording: Boolean by mutableStateOf(controller.isRecording)
    actual var isStreaming: Boolean by mutableStateOf(false)
    actual var isInitialized: Boolean by mutableStateOf(false)
    actual fun hasCamera(cameraSelector: CamSelector): Boolean {
        return isInitialized && controller.hasCamera(cameraSelector.selector)
    }

    internal actual var captureMode: CaptureMode = CaptureMode.Image
        set(value) {
            if (field != value) {
                field = value
                updateCaptureMode()
            }
        }

    private var imageAnalyzer: ImageAnalysis.Analyzer? = null
        set(value) {
            field = value
            updateImageAnalyzer(value)
        }

    private fun updateCaptureMode() {
        try {
            var useCases = captureMode.value
            if (captureMode == CaptureMode.Image && isImageAnalysisEnabled) {
                useCases = useCases or IMAGE_ANALYSIS
                updateImageAnalyzer(imageAnalyzer)
            } else {
                updateImageAnalyzer(null)
            }
            controller.setEnabledUseCases(useCases)
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "Use case Image Analysis not supported")
            controller.setEnabledUseCases(captureMode.value)
        }
    }

    private fun updateImageAnalyzer(
        analyzer: ImageAnalysis.Analyzer? = imageAnalyzer
    ) = with(controller) {
        clearImageAnalysisAnalyzer()
        if (captureMode == CaptureMode.Video) {
            return
        }

        setImageAnalysisAnalyzer(mainExecutor, analyzer ?: return)
    }

    actual var isImageAnalysisSupported: Boolean by mutableStateOf(
        isImageAnalysisSupported(camSelector)
    )

    internal actual var isImageAnalysisEnabled: Boolean = isImageAnalysisSupported
        set(value) {
            if (!isImageAnalysisSupported) {
                Log.e(TAG, "Image analysis is not supported")
                return
            }
            field = value
            updateCaptureMode()
        }


    @SuppressLint("RestrictedApi")
    actual fun isImageAnalysisSupported(cameraSelector: CamSelector): Boolean {
        return cameraManager?.isImageAnalysisSupported(cameraSelector.selector.lensFacing) ?: false
    }

    internal actual var imageCaptureMode: ImageCaptureMode = ImageCaptureMode.MinLatency
        set(value) {
            if (field != value) {
                field = value
                controller.imageCaptureMode = value.mode
            }
        }
    internal actual var enableTorch: Boolean
        get() = controller.torchState.value == TorchState.ON
        set(value) {
            if (enableTorch != value) {
                controller.enableTorch(hasFlashUnit && value)
            }
        }

    internal actual var imageCaptureTargetSize: ImageTargetSize?
        get() = controller.imageCaptureTargetSize.toImageTargetSize()
        set(value) {
            if (value != imageCaptureTargetSize) {
                controller.imageCaptureTargetSize = value?.toOutputSize()
            }
        }
    actual val initialExposure: Int = INITIAL_EXPOSURE_VALUE
        get() = controller.cameraInfo?.exposureState?.exposureCompensationIndex ?: field


}

private fun CameraController.OutputSize?.toImageTargetSize(): ImageTargetSize? {
    return this?.let {
        if (it.aspectRatio != UNASSIGNED_ASPECT_RATIO) {
            ImageTargetSize(aspectRatio = it.aspectRatio)
        } else {
            ImageTargetSize(size = it.resolution)
        }
    }
}

@Composable
actual fun rememberCameraState(): CameraState {
    val context = LocalContext.current
    return remember { CameraState(context) }
}