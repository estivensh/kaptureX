package io.github.estivensh.extensions

import android.content.ContentResolver
import android.net.Uri

actual typealias ImageFile = ImageUri

actual fun ImageFile.toByteArray() = uri?.let { newUri ->
    contentResolver.openInputStream(newUri)?.use {
        it.readBytes()
    }
} ?: throw IllegalStateException("Couldn't open inputStream $uri")

class ImageUri(val uri: Uri?, val contentResolver: ContentResolver)

fun Uri.toImageFile(contentResolver: ContentResolver): ImageFile {
    return ImageFile(this, contentResolver)
}