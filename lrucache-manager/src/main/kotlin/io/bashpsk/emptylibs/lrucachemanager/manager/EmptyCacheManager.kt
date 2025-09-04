package io.bashpsk.emptylibs.lrucachemanager.manager

import android.util.Log
import androidx.collection.LruCache
import io.bashpsk.emptylibs.lrucachemanager.utils.LOG_TAG

/**
 * The LruCache instance used to store objects of type T.
 * The maximum number of entries in the cache. Defaults to 10.
 */
private val LruCacheEmpty = LruCache<String, Any>(10)

/**
 * Manages an LruCache for storing and retrieving objects of a generic type T.
 *
 * This class provides a simple interface for adding, retrieving, removing, and clearing objects
 * from an LruCache.
 *
 * @param T The type of objects to be stored in the cache.
 * @param maxSize The maximum number of entries in the cache. Defaults to 10.
 */
class EmptyCacheManager<T>(private val maxSize: Int = 10) {

    init {

        LruCacheEmpty.resize(maxSize = maxSize)
    }

    /**
     * Adds an object to the cache.
     *
     * If the object is successfully added to the cache, this function returns the object that was
     * previously at the specified key, or null if the key was not present in the cache.
     * If there is an issue adding the object (e.g., the cache is full and cannot evict items),
     * it attempts to retrieve the object using `get(key)` as a fallback.
     *
     * @param key The key to store the object under.
     * @param value The object to store.
     * @return The previously stored object associated with the key if the new object was
     * successfully added, or the object associated with the key if retrieval was successful after a
     * failed put, or null if the object could not be added or retrieved.
     */
    @Suppress("UNCHECKED_CAST")
    fun add(key: String, value: T): T? {

        return (LruCacheEmpty.put(key, value!!) ?: get(key)) as T?
    }

    /**
     * Retrieves an object from the cache.
     * @param key The key of the object to retrieve.
     * @return The object of type [T] if found in the cache, or `null` otherwise.
     */
    @Suppress("UNCHECKED_CAST")
    fun get(key: String): T? {

        return LruCacheEmpty[key] as T?
    }

    /**
     * Checks if an object with the given key exists in the cache.
     * @param key The key to check for.
     * @return `true` if an object with the given key exists in the cache, `false` otherwise.
     */
    fun exist(key: String): Boolean {

        return LruCacheEmpty[key] != null
    }

    /**
     * Removes an object from the cache.
     * @param key The key of the object to remove.
     * @return `true` if an object was removed (i.e., it existed in the cache), `false` otherwise.
     */
    fun remove(key: String): Boolean {

        return LruCacheEmpty.remove(key) != null
    }

    /**
     * Clears the entire cache.
     * This method removes all entries from the LruCache, effectively emptying it.
     */
    fun evictAll() {

        LruCacheEmpty.evictAll()
        Log.i(LOG_TAG, "Cache Cleared")
    }
}