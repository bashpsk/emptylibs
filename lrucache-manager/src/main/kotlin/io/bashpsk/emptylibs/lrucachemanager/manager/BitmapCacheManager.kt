package io.bashpsk.emptylibs.lrucachemanager.manager

import android.graphics.Bitmap
import androidx.collection.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * The LruCache instance used to store bitmaps.
 * The maximum number of entries in the cache. Defaults to 10.
 */
private val LruCacheBitmap = LruCache<String, Bitmap>(10)

/**
 * Manages an LruCache for storing and retrieving bitmaps.
 *
 * This class provides a simple interface for adding, retrieving, removing, and clearing bitmaps
 * from an LruCache.
 *
 * @param maxSize The maximum number of entries in the cache. Defaults to 10.
 */
class BitmapCacheManager(private val maxSize: Int = 10) {

    init {

        LruCacheBitmap.resize(maxSize = maxSize)
    }

    /**
     * Adds a bitmap to the cache.
     * This is an overloaded version of [addBitmap] that accepts an [ImageBitmap].
     *
     * @param key The key to store the bitmap under.
     * @param bitmap The [ImageBitmap] to store.
     * @return The previously stored [ImageBitmap] for the given key, or null if there was no
     * mapping for the key.
     */
    fun addBitmap(key: String, bitmap: ImageBitmap): ImageBitmap? {

        return addBitmap(key = key, bitmap = bitmap.asAndroidBitmap())?.asImageBitmap()
    }

    /**
     * Adds a bitmap to the cache.
     *
     * If the bitmap is successfully added to the cache, this function returns the bitmap that was
     * previously at the specified key, or null if the key was not present in the cache.
     * If there is an issue adding the bitmap (e.g., the cache is full and cannot evict items),
     * it attempts to retrieve the bitmap using `getBitmap(key)` as a fallback.
     *
     * @param key The key to store the bitmap under.
     * @param bitmap The bitmap to store.
     * @return The previously stored bitmap associated with the key if the new bitmap was
     * successfully added, or the bitmap associated with the key if retrieval was successful after a
     * failed put, or null if the bitmap could not be added or retrieved.
     */
    fun addBitmap(key: String, bitmap: Bitmap): Bitmap? {

        return LruCacheBitmap.put(key = key, value = bitmap) ?: getBitmap(key = key)
    }

    /**
     * Retrieves a bitmap from the cache as an ImageBitmap.
     *
     * This function first retrieves the Bitmap using [getBitmap] and then converts it
     * to an [ImageBitmap] using [asImageBitmap].
     *
     * @param key The key of the bitmap to retrieve.
     * @return The [ImageBitmap] if found and conversion is successful, or null otherwise.
     */
    fun getImageBitmap(key: String): ImageBitmap? {

        return getBitmap(key = key)?.asImageBitmap()
    }

    /**
     * Retrieves a bitmap from the cache.
     * @param key The key of the bitmap to retrieve.
     * @return The [Bitmap] if found in the cache, or `null` otherwise.
     */
    fun getBitmap(key: String): Bitmap? {

        return LruCacheBitmap[key]
    }

    /**
     * Removes a bitmap from the cache.
     * @param key The key of the bitmap to remove.
     * @return `true` if a bitmap was removed (i.e., it existed in the cache), `false` otherwise.
     */
    fun removeBitmap(key: String): Boolean {

        return LruCacheBitmap.remove(key) != null
    }

    /**
     * Clears the entire cache.
     * This method removes all entries from the LruCache, effectively emptying it.
     */
    fun evictAll() {

        LruCacheBitmap.evictAll()
    }
}