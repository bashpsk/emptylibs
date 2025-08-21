package io.bashpsk.emptylibs.extension

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap

val ImageBitmap.size: Size
    get() = Size(width = width.toFloat(), height = height.toFloat())

val Bitmap.size: Size
    get() = Size(width = width.toFloat(), height = height.toFloat())