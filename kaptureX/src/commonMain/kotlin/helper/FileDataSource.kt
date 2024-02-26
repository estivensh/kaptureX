package helper

import extensions.ImageFile

expect class FileDataSource {
    val externalDir: String
    val currentFileName: String
    val lastPicture: ImageFile
}
