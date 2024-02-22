import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.interop.UIKitView
import extensions.ImageFile
import focus.FocusTap
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import permissions.PermissionCallback
import permissions.PermissionStatus
import permissions.PermissionType
import permissions.createPermissionsManager
import permissions.rememberCameraManager
import permissions.rememberGalleryManager
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePosition
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureFlashModeAuto
import platform.AVFoundation.AVCaptureFlashModeOff
import platform.AVFoundation.AVCaptureFlashModeOn
import platform.AVFoundation.AVCaptureInput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.fileDataRepresentation
import platform.AVFoundation.lensPosition
import platform.AVFoundation.position
import platform.AVFoundation.setFlashMode
import platform.CoreGraphics.CGRect
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHAssetCollectionChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.darwin.NSObject
import state.CamSelector
import state.CameraState
import state.CaptureMode
import state.FlashMode
import state.ImageCaptureMode
import state.ImageCaptureResult
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
    //implementationMode: ImplementationMode,
    //imageAnalyzer: ImageAnalyzer?,
    exposureCompensation: Int,
    isImageAnalysisEnabled: Boolean,
    //isFocusOnTapEnabled: Boolean,
    isPinchToZoomEnabled: Boolean,
    //videoQualitySelector: QualitySelector,
    onZoomRatioChanged: (Float) -> Unit,
    onPreviewStreamChanged: () -> Unit,
    onFocus: suspend (() -> Unit) -> Unit,
    focusTapContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val photoOutput = remember { cameraState.photoOutput }
    val captureSession = remember { AVCaptureSession() }
    val videoPreviewLayer = remember { AVCaptureVideoPreviewLayer(session = captureSession) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    var currentCamSelector by remember { mutableStateOf(camSelector) }
    var currentFlashMode by remember { mutableStateOf(flashMode) }

    LaunchedEffect(camSelector) { currentCamSelector = camSelector }
    LaunchedEffect(flashMode) { currentFlashMode = flashMode }

    LaunchedEffect(currentCamSelector) {

        val x = AVCaptureDevice.devicesWithMediaType(AVMediaTypeVideo) as List<AVCaptureDevice>

        val captureDevice = when (currentCamSelector) {
            CamSelector.Front -> x
                .firstOrNull { it.position == AVCaptureDevicePositionFront }
            CamSelector.Back -> x
                .firstOrNull { it.position == AVCaptureDevicePositionBack }
        }

        val inputs = captureSession.inputs as List<AVCaptureInput>
        inputs.forEach { captureSession.removeInput(it) }

        val input = captureDevice?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) }
        if (input != null && captureSession.canAddInput(input)) {
            captureSession.addInput(input)
        }

        // Configurar el flash
        captureDevice?.lockForConfiguration(null)
        captureDevice?.setFlashMode(currentFlashMode.mode)
        captureDevice?.unlockForConfiguration()

        // Configurar la captura de fotos
        captureSession.addOutput(photoOutput)

        // Configurar la capa de vista previa del video
        videoPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        videoPreviewLayer.frame = UIScreen.mainScreen.bounds

        // Iniciar la sesión de captura
        this.launch(Dispatchers.Main) {
            captureSession.startRunning()
        }
    }

    UIKitView(
        factory = {
            val playerContainer = UIView()
            playerContainer.layer.addSublayer(videoPreviewLayer)
            playerContainer
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            videoPreviewLayer.setFrame(rect)
            CATransaction.commit()
        },
        modifier = modifier
    )

    FocusTap(
        offset = tapOffset,
        onFocus = { onFocus { tapOffset = Offset.Zero } },
    ) { focusTapContent() }

    content()
    val coroutineScope = rememberCoroutineScope()
    var launchCamera by remember { mutableStateOf(value = false) }
    var launchGallery by remember { mutableStateOf(value = false) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var permissionRationalDialog by remember { mutableStateOf(value = false) }
    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(
            permissionType: PermissionType,
            status: PermissionStatus
        ) {
            when (status) {
                PermissionStatus.GRANTED -> {
                    when (permissionType) {
                        PermissionType.CAMERA -> launchCamera = true
                        PermissionType.GALLERY -> launchGallery = true
                    }
                }

                else -> {
                    permissionRationalDialog = true
                }
            }
        }


    })

    val cameraManager = rememberCameraManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                it?.toImageBitmap()
            }
            imageBitmap = bitmap
        }
    }

    val galleryManager = rememberGalleryManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                it?.toImageBitmap()
            }
            imageBitmap = bitmap
        }
    }

    if (launchGallery) {
        if (permissionsManager.isPermissionGranted(PermissionType.GALLERY)) {
            galleryManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.GALLERY)
        }
        launchGallery = false
    }
    if (launchCamera) {
        if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
            cameraManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.CAMERA)
        }
        launchCamera = false
    }

    Button(
        onClick = {
            galleryManager.launch()
        }
    ) {
        Text("Permisos")
    }
}