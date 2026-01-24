package io.bashpsk.emptylibs.imageedit.cache

import io.bashpsk.emptylibs.imageedit.edit.ImageEditInput
import io.bashpsk.emptylibs.imageedit.edit.ImageEditItems
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import kotlinx.collections.immutable.PersistentList

/**
 * Manages caching of [ImageEditInput] objects.
 * This cache manager uses an LRU (Least Recently Used) eviction policy
 * and has a maximum size of 5 entries.
 */
internal val ImageInputCacheManager by lazy {
    EmptyCacheManager<String, ImageEditInput>(maxSize = 5)
}

/**
 * Manages the caching of `ImageEditItems`.
 * This cache is designed to hold a maximum of 1 item,
 * typically used for storing the most recently edited image state.
 */
internal val ImageEditCacheManager by lazy {
    EmptyCacheManager<String, ImageEditItems>(maxSize = 1)
}

/**
 * Cache manager for storing a list of image edit items.
 *
 * This cache is used to temporarily store a list of image edit items,
 * such as a history of edits applied to an image.
 * The `maxSize` is set to 1, meaning only the most recent list of edits is cached.
 *
 * It utilizes `EmptyCacheManager` for its underlying caching mechanism.
 */
internal val ImageEditListCacheManager by lazy {
    EmptyCacheManager<String, PersistentList<ImageEditItems>>(maxSize = 1)
}