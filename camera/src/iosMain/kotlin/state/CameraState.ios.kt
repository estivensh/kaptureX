package state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionUnspecified
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.exposureMode
import platform.AVFoundation.exposureTargetOffset
import platform.AVFoundation.flashMode
import platform.AVFoundation.hasFlash
import platform.AVFoundation.isTorchActive
import platform.AVFoundation.position
import platform.AVFoundation.setFlashMode
import platform.AVFoundation.videoMinZoomFactorForCenterStage

@OptIn(ExperimentalForeignApi::class)
actual class CameraState() {


    private var captureSession: AVCaptureSession? = null
    actual val controller: AVCaptureDevice =
        AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: AVCaptureDevice()

    init {
        captureSession = AVCaptureSession()
        val input =
            AVCaptureDeviceInput.deviceInputWithDevice(controller ?: AVCaptureDevice(), null)
    }

    internal actual var flashMode: FlashMode
        get() = FlashMode.find(controller.flashMode)
        set(value) {
            if (hasFlashUnit && flashMode != value){
                controller.setFlashMode(value.mode)
            }
        }

    actual var hasFlashUnit: Boolean by mutableStateOf(controller?.hasFlash ?: true)
    actual val isZoomSupported: Boolean
            by derivedStateOf { maxZoom != 1F }
    actual var maxZoom: Float
            by mutableFloatStateOf(
                controller?.activeFormat?.videoMaxZoomFactor?.toFloat() ?: INITIAL_ZOOM_VALUE
            )

    actual companion object {
        actual val INITIAL_ZOOM_VALUE: Float = 1F
        actual val INITIAL_EXPOSURE_VALUE: Int = 0
    }

    actual var minZoom: Float
            by mutableFloatStateOf(
                controller?.activeFormat?.videoMinZoomFactorForCenterStage?.toFloat()
                    ?: INITIAL_ZOOM_VALUE
            )

    actual fun startZoom() {
        val zoom = controller?.activeFormat
        minZoom = zoom?.videoMinZoomFactorForCenterStage?.toFloat() ?: INITIAL_ZOOM_VALUE
        maxZoom = zoom?.videoMaxZoomFactor?.toFloat() ?: INITIAL_ZOOM_VALUE
    }

    internal actual var scaleType: ScaleType = ScaleType.Fill
    internal actual var camSelector: CamSelector
        = CamSelector.Back
        set(value) {
            when {
                value == field -> Unit
                !isRecording && hasCamera(value) -> {
                    if (controller.position != value.selector){
                        //controller.setposi = value.selector
                        field = value
                    }
                }

            }
        }
    actual var isRecording: Boolean by mutableStateOf(false)
    internal actual var captureMode: CaptureMode = CaptureMode.Image
        set(value) {
            if (field != value) {
                field = value
            }
        }
    internal actual var imageCaptureMode: ImageCaptureMode
        get() = TODO("Not yet implemented")
        set(value) {}
    actual var isImageAnalysisSupported: Boolean by mutableStateOf(
        isImageAnalysisSupported(camSelector)
    )
    internal actual var isImageAnalysisEnabled: Boolean = isImageAnalysisSupported
        set(value) {
            if (!isImageAnalysisSupported) {
                println("Image analysis is not supported")
                return
            }
            field = value
        }

    actual var isStreaming: Boolean by mutableStateOf(false)
    actual var isInitialized: Boolean by mutableStateOf(false)

    actual fun hasCamera(cameraSelector: CamSelector): Boolean {
        val devices = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            mutableListOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            AVMediaTypeVideo,
            AVCaptureDevicePositionUnspecified
        )
        return devices.devices.isNotEmpty()
    }

    actual fun isImageAnalysisSupported(cameraSelector: CamSelector): Boolean {
        TODO("Not yet implemented")
    }

    internal actual var enableTorch: Boolean
        get() = controller.hasFlash && controller.isTorchActive()
        set(value) {}
    internal actual var imageCaptureTargetSize: ImageTargetSize?
        get() = TODO("Not yet implemented")
        set(value) {}
    actual val initialExposure: Int
        get() = controller.exposureTargetOffset.toInt()

    internal actual fun update(
        camSelector: CamSelector,
        captureMode: CaptureMode,
        scaleType: ScaleType,
        imageCaptureTargetSize: ImageTargetSize?,
        isImageAnalysisEnabled: Boolean,
        flashMode: FlashMode,
        zoomRatio: Float,
        imageCaptureMode: ImageCaptureMode,
        enableTorch: Boolean,
        exposureCompensation: Int
    ) {
        this.camSelector = camSelector
        this.captureMode = captureMode
        this.scaleType = scaleType
        this.imageCaptureTargetSize = imageCaptureTargetSize
        this.isImageAnalysisEnabled = isImageAnalysisEnabled
        /*this.imageAnalyzer = imageAnalyzer?.analyzer
        this.implementationMode = implementationMode
        this.isFocusOnTapEnabled = isFocusOnTapEnabled*/
        this.flashMode = flashMode
        this.enableTorch = enableTorch
        //this.isFocusOnTapSupported = meteringPoint.isFocusMeteringSupported
        this.imageCaptureMode = imageCaptureMode
        //this.videoQualitySelector = videoQualitySelector
        //setExposureCompensation(exposureCompensation)
        //setZoomRatio(zoomRatio)
    }

    actual fun takePicture(onResult: (ImageCaptureResult) -> Unit) {
    }

    actual fun startRecording(onResult: (VideoCaptureResult) -> Unit) {
    }

    actual fun stopRecording() {
    }

    actual fun pauseRecording() {
    }

    actual fun resumeRecording() {
    }

    actual fun toggleRecording(onResult: (VideoCaptureResult) -> Unit) {
    }


}

@Composable
actual fun rememberCameraState(): CameraState {
    return remember { CameraState() }
}

@Composable
actual fun rememberCamSelector(selector: CamSelector): MutableState<CamSelector> =
    rememberSaveable(saver = CamSelector.Saver) {
        mutableStateOf(selector)
    }

@Composable
actual fun CameraState.rememberFlashMode(
    initialFlashMode: FlashMode,
    useSaver: Boolean
): MutableState<FlashMode> {
    TODO("Not yet implemented")
}