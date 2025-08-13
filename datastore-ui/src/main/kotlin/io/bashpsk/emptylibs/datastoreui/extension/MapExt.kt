package io.bashpsk.emptylibs.datastoreui.extension

/**
 * Creates a new map where the keys and values of the original map are swapped.
 *
 * If the original map contains duplicate values, the resulting map will only contain one entry
 * for each unique value, and the key associated with that value will be the key of the last
 * occurrence of that value in the original map.
 *
 * For example:
 * ```
 * val originalMap = mapOf("a" to 1, "b" to 2, "c" to 1)
 * val reversedMap = originalMap.toReverseMap() // reversedMap will be {1="c", 2="b"}
 * ```
 *
 * @return A new map with keys and values swapped.
 */
fun <K, V> Map<K, V>.toReverseMap(): Map<V, K> {

    return entries.associate { (key, value) -> value to key }
}