package io.bashpsk.emptylibs.imageedit.edit

sealed class ImageEditItems(var id: Long = 0L) {

    data class Image(val image: ImageEditBitmap) : ImageEditItems()

    data class Path(val path: ImageEditPath) : ImageEditItems()

    data class Shape(val shape: ImageEditShape) : ImageEditItems()

    data class Text(val text: ImageEditText) : ImageEditItems()
}