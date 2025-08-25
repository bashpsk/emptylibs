package io.bashpsk.emptylibs.imageedit.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

internal fun Offset.toTopRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y)
}

internal fun Offset.toBottomLeft(size: Size): Offset {

    return Offset(x = x, y = y + size.height)
}

internal fun Offset.toBottomRight(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height)
}

internal fun Offset.toTopCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y)
}

internal fun Offset.toBottomCenter(size: Size): Offset {

    return Offset(x = x + size.width / 2, y = y + size.height)
}

internal fun Offset.toLeftCenter(size: Size): Offset {

    return Offset(x = x, y = y + size.height / 2)
}

internal fun Offset.toRightCenter(size: Size): Offset {

    return Offset(x = x + size.width, y = y + size.height / 2)
}