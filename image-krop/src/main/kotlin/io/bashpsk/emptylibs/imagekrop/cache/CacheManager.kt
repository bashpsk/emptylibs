package io.bashpsk.emptylibs.imagekrop.cache

import androidx.compose.ui.graphics.ImageBitmap
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager

/**
 * A lazy-initialized cache manager for [ImageBitmap] objects.
 * This manager uses an LRU (Least Recently Used) strategy and has a maximum size of 3.
 * It is primarily used for caching individual image bitmaps.
 */
internal val BitmapCacheManager by lazy { EmptyCacheManager<String, ImageBitmap>(maxSize = 3) }

/**
 * A cache manager for lists of `ImageBitmap` objects.
 *
 * This manager uses an `EmptyCacheManager` with a maximum size of 3, meaning it can hold up to 3
 * lists of bitmaps.
 * It's designed to cache lists of images, potentially for scenarios like image sequences or
 * galleries.
 */
internal val BitmapListCacheManager by lazy { EmptyCacheManager<String, ImageBitmap>(maxSize = 3) }