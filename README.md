<div align="center">
  <h1>KaptureX</h1>
</div>

KaptureX is a library designed to facilitate the integration of image and video capture capabilities in multiplatform applications developed with Kotlin. This library is especially useful for those who want to create applications that support multiple platforms such as Android and iOS, providing a unified API and reusable components.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.estivensh4/kaptureX)](https://mvnrepository.com/artifact/io.github.estivensh)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.22-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
![badge][badge-android]
![badge][badge-ios]
![badge][badge-last-commit]

## Status

| Product | Android  | iOS      |
|---------|----------|----------|
| Camera  | ☑️ alpha | ☑️ alpha |
| Video   | ☑️ alpha | ☑️ alpha |
| Flash   | ☑️ alpha | ☑️ alpha |

## Implementation

add the dependency `build.gradle.kts` file:

```kotlin
commonMain.dependencies {
    implementation("io.github.estivensh:kaptureX:$version")
}
```

## Basic Usage
Below is a basic example of how to use the CameraPreview component in your application:

```kotlin
@OptIn(ExperimentalCameraPreview::class)
@Composable
fun MyCameraScreen() {
    val cameraState = rememberCameraState()

    CameraPreview(
        cameraState = cameraState,
        camSelectorOnChanged = { newSelector ->

        },
        flashModeOnChanged = { newFlashMode ->

        },
        cameraOptionOnChanged = { newOption ->

        }
    )
}
```

## Customizable Parameters
The CameraPreview component offers a wide variety of parameters to customize its behavior:

- <b>cameraState:</b> Current camera state. Use rememberCameraState() to handle and remember the camera state

- <b>camSelector:</b> Selects the camera (front or back). You can handle camera switching using camSelectorOnChanged.

- <b>flashMode:</b> Configures the camera's flash mode. You can change it using flashModeOnChanged.

- <b>captureMode:</b> Defines the capture mode (image or video).

- <b>imageCaptureMode:</b> Sets the image capture mode (high quality or fast speed).

- <b>zoomRatio:</b> CControls the zoom level. You can handle zoom changes with onZoomRatioChanged.

- <b>isImageAnalysisEnabled:</b> Enables or disables real-time image analysis.

- <b>onPreviewStreamChanged:</b> Callback executed when the preview stream changes.

```kotlin
@OptIn(ExperimentalCameraPreview::class)
@Composable
fun CameraSection(
    cameraState: CameraState,
    useFrontCamera: Boolean,
    usePinchToZoom: Boolean
) {
    var flashMode by cameraState.rememberFlashMode(FlashMode.valueOf("On"))
    var camSelector by rememberCamSelector(if (useFrontCamera) CamSelector.Front else CamSelector.Back)
    var zoomRatio by rememberSaveable { mutableStateOf(cameraState.minZoom) }
    var zoomHasChanged by rememberSaveable { mutableStateOf(false) }
    var cameraOption by rememberSaveable { mutableStateOf(CameraOption.Video) }
    val enableTorch = true
    CameraPreview(
        cameraState = cameraState,
        camSelector = camSelector,
        captureMode = cameraOption.toCaptureMode(),
        flashMode = flashMode,
        enableTorch = enableTorch,
        zoomRatio = zoomRatio,
        isPinchToZoomEnabled = usePinchToZoom,
        onZoomRatioChanged = {
            zoomHasChanged = true
            zoomRatio = it
        },
        onSwitchToFront = { _ ->

        },
        onSwitchToBack = { _ ->

        },
        camSelectorOnChanged = { camSelector = it },
        flashModeOnChanged = { flashMode = it },
        cameraOptionOnChanged = { cameraOption = it },
        cameraOption = cameraOption,
    )
}
```

## Inspiration
This library was mostly inspired by [Camposer](https://github.com/ujizin/Camposer).<br>

> Camera Library for Android Jetpack Compose. 📸✨

## Find this repository useful? :heart:

Support it by joining __[estivensh4](https://github.com/estivensh4/kaptureX)__ for this
repository. :star: <br>
Also __[follow](https://github.com/estivensh4)__ me for my next creations! 🤩

[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-last-commit]: https://img.shields.io/github/last-commit/estivensh4/kaptureX?style=flat-square

## License

```
Copyright 2024 Estiven Sánchez
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```