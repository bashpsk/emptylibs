package io.bashpsk.emptylibs.imagekrop.cache

import androidx.compose.ui.graphics.ImageBitmap
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager

internal val BitmapCacheManager by lazy { EmptyCacheManager<ImageBitmap>(maxSize = 3) }

internal val BitmapListCacheManager by lazy { EmptyCacheManager<ImageBitmap>(maxSize = 3) }