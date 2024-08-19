package io.github.estivensh.extensions

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

actual typealias ImageFile = UIImage

actual fun ImageFile.toByteArray() =
    UIImagePNGRepresentation(this)?.toByteArray() ?: emptyArray<Byte>().toByteArray()

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), bytes, length)
    }
}