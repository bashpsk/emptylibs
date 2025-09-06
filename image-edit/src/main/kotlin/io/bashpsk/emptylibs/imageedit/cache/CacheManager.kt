package io.bashpsk.emptylibs.imageedit.cache

import io.bashpsk.emptylibs.imageedit.edit.ImageEditInput
import io.bashpsk.emptylibs.imageedit.edit.ImageEditItems
import io.bashpsk.emptylibs.lrucachemanager.manager.EmptyCacheManager
import kotlinx.collections.immutable.PersistentList

internal val ImageInputCacheManager by lazy {
    EmptyCacheManager<ImageEditInput>(maxSize = 5)
}

internal val ImageEditCacheManager by lazy {
    EmptyCacheManager<ImageEditItems>(maxSize = 1)
}

internal val ImageEditListCacheManager by lazy {
    EmptyCacheManager<PersistentList<ImageEditItems>>(maxSize = 1)
}