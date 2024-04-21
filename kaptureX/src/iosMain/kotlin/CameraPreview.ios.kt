import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.interop.UIKitView
import focus.FocusTap
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import permissions.SharedImage
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureInput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.position
import platform.AVFoundation.setExposureTargetBias
import platform.AVFoundation.setFlashMode
import platform.CoreGraphics.CGRect
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIImage
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import state.CamSelector
import state.CameraState
import state.CaptureMode
import state.FlashMode
import state.ImageCaptureMode
import state.ImageTargetSize
import state.ScaleType

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraPreviewImpl(
    modifier: Modifier,
    cameraState: CameraState,
    camSelector: CamSelector,
    captureMode: CaptureMode,
    imageCaptureMode: ImageCaptureMode,
    imageCaptureTargetSize: ImageTargetSize?,
    flashMode: FlashMode,
    scaleType: ScaleType,
    enableTorch: Boolean,
    zoomRatio: Float,
    exposureCompensation: Int,
    isImageAnalysisEnabled: Boolean,
    isFocusOnTapEnabled: Boolean,
    isPinchToZoomEnabled: Boolean,
    onZoomRatioChanged: (Float) -> Unit,
    onPreviewStreamChanged: () -> Unit,
    onFocus: suspend (() -> Unit) -> Unit,
    onSwipeToFront: @Composable (SharedImage) -> Unit,
    onSwipeToBack: @Composable (SharedImage) -> Unit,
    focusTapContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val photoOutput = remember { cameraState.photoOutput }
    //val movieOutput = remember { cameraState.movieFileOutput }
    val captureSession = remember { cameraState.captureSession }
    val videoPreviewLayer = remember { AVCaptureVideoPreviewLayer(session = captureSession) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    var currentCamSelector by remember { mutableStateOf(camSelector) }
    var currentFlashMode by remember { mutableStateOf(flashMode) }
    var currentExposure by remember { mutableStateOf(exposureCompensation) }
    val isCameraIdle by rememberUpdatedState(!cameraState.isStreaming)
    var latestBitmap by remember { mutableStateOf<UIImage?>(null) }
    val cameraIsInitialized by rememberUpdatedState(cameraState.isInitialized)

    LaunchedEffect(camSelector) { currentCamSelector = camSelector }
    LaunchedEffect(flashMode) { currentFlashMode = flashMode }
    LaunchedEffect(exposureCompensation) { currentExposure = exposureCompensation }

    LaunchedEffect(currentCamSelector, currentFlashMode) {

        val avCaptureDeviceList = AVCaptureDevice.devicesWithMediaType(AVMediaTypeVideo) as List<AVCaptureDevice>

        val captureDevice = when (currentCamSelector) {
            CamSelector.Front -> avCaptureDeviceList
                .firstOrNull { it.position == AVCaptureDevicePositionFront }

            CamSelector.Back -> avCaptureDeviceList
                .firstOrNull { it.position == AVCaptureDevicePositionBack }
        }

        val inputs = captureSession.inputs as List<AVCaptureInput>
        inputs.forEach { captureSession.removeInput(it) }

        val input = captureDevice?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) }
        if (input != null && captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        // Configuring the flash
        captureDevice?.lockForConfiguration(null)
        captureDevice?.setFlashMode(currentFlashMode.mode)
        captureDevice?.unlockForConfiguration()

        // Configuring the exposure
        /*captureDevice?.lockForConfiguration(null)
        captureDevice?.setExposureTargetBias(currentExposure.toFloat(), null)
        captureDevice?.unlockForConfiguration()*/
    }

    LaunchedEffect(true) {
        // Configure photo capture
        captureSession.addOutput(photoOutput)

        // Configure the video preview layer
        videoPreviewLayer.videoGravity = scaleType.type
        videoPreviewLayer.frame = UIScreen.mainScreen.bounds
        //videoPreviewLayer.setAccessibilityDirectTouchOptions(UIControlEventTouchUpInside)

        // Start capture session
        launch(Dispatchers.Main) {
            captureSession.startRunning()
        }
    }

    UIKitView(
        factory = {
            UIView().apply { layer.addSublayer(videoPreviewLayer) }
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            videoPreviewLayer.setFrame(rect)
            CATransaction.commit()
        },
        modifier = modifier,
        update = { uiKitView ->
            if (cameraIsInitialized) {

                cameraState.update(
                    camSelector = camSelector,
                    captureMode = captureMode,
                    imageCaptureTargetSize = imageCaptureTargetSize,
                    scaleType = scaleType,
                    isImageAnalysisEnabled = isImageAnalysisEnabled,
                    isFocusOnTapEnabled = isFocusOnTapEnabled,
                    flashMode = flashMode,
                    enableTorch = enableTorch,
                    zoomRatio = zoomRatio,
                    imageCaptureMode = imageCaptureMode,
                    exposureCompensation = exposureCompensation,
                )
            }
        }
    )

    FocusTap(
        offset = tapOffset,
        onFocus = { onFocus { tapOffset = Offset.Zero } },
    ) { focusTapContent() }

    if (isCameraIdle) {
        latestBitmap?.let {
            when (camSelector.selector) {
                AVCaptureDevicePositionFront -> onSwipeToFront(SharedImage(it))
                AVCaptureDevicePositionBack -> onSwipeToBack(SharedImage(it))
                else -> Unit
            }
            LaunchedEffect(latestBitmap) {
                onPreviewStreamChanged()
                if (latestBitmap != null) onZoomRatioChanged(cameraState.minZoom)
            }
        }
    }

    content()
}