package io.github.estivensh.helper

import io.github.estivensh.extensions.ImageFile

expect class FileDataSource {
    val externalDir: String
    val currentFileName: String
    val lastPicture: ImageFile
}
