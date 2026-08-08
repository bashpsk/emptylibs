package io.bashpsk.emptylibs.lrucachemanager.manager

import androidx.collection.LruCache

/**
 * Manages an LruCache for storing and retrieving objects of a generic type T.
 *
 * This class provides a simple interface for adding, retrieving, removing, and clearing objects
 * from an LruCache.
 *
 * @param K The type of keys used to identify objects in the cache.
 * @param V The type of objects to be stored in the cache.
 * @param maxSize The maximum number of entries in the cache. Defaults to 10.
 * @param onEntryRemoved A callback function that is invoked when an entry is removed from the cache.
 */
class EmptyCacheManager<K : Any, V : Any>(
    private val maxSize: Int = 10,
    private val onEntryRemoved: (
        cache: EmptyCacheManager<K, V>,
        evicted: Boolean,
        key: K,
        value: V
    ) -> Unit = { _, _, _, _ -> }
) {

    /**
     * The LruCache instance used to store objects of type T.
     * The maximum number of entries in the cache. Defaults to 10.
     */
    val lruCache = object : LruCache<K, V>(maxSize = maxSize) {

        override fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {

            onEntryRemoved(this@EmptyCacheManager, evicted, key, oldValue)
        }
    }

    /**
     * Adds an object to the cache.
     *
     * @param key The key to store the object under.
     * @param value The object to store.
     */
    operator fun set(key: K, value: V) {

        value.let { item -> lruCache.put(key, item) }
    }

    /**
     * Retrieves an object from the cache.
     * @param key The key of the object to retrieve.
     * @return The object of type [V] if found in the cache, or `null` otherwise.
     */
    operator fun get(key: K): V? {

        return lruCache[key]
    }

    /**
     * Checks if an object with the given key exists in the cache.
     * @param key The key to check for.
     * @return `true` if an object with the given key exists in the cache, `false` otherwise.
     */
    operator fun contains(key: K): Boolean {

        return lruCache[key] != null
    }

    /**
     * Removes an object from the cache.
     * @param key The key of the object to remove.
     * @return `true` if an object was removed (i.e., it existed in the cache), `false` otherwise.
     */
    fun remove(key: K): Boolean {

        return lruCache.remove(key) != null
    }

    /**
     * Resizes the LruCache to the specified maximum size.
     *
     * This function allows dynamically changing the capacity of the cache.
     * If the new `maxSize` is smaller than the current number of items in the cache,
     * the least recently used items will be evicted until the cache size matches `maxSize`.
     *
     * @param maxSize The new maximum number of entries for the cache.
     */
    fun resize(maxSize: Int) {

        lruCache.resize(maxSize = maxSize)
    }

    /**
     * Clears the entire cache.
     * This method removes all entries from the LruCache, effectively emptying it.
     */
    fun evictAll() {

        lruCache.evictAll()
    }
}