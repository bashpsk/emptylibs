package io.bashpsk.emptylibs.datastoreui.extension

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import io.bashpsk.emptylibs.datastoreui.font.FontPreferenceItem
import io.bashpsk.emptylibs.datastoreui.utils.LOG_TAG
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

/**
 * Retrieves a preference value from the [DataStore] as a [Flow], returning `null` if not found.
 *
 * This function observes changes to the preference associated with the given [key].
 * If the preference is not set, it emits `null`. Any [IOException] encountered during
 * the read operation is caught and logged, emitting empty preferences to prevent
 * flow interruption. The flow is executed on [Dispatchers.IO].
 *
 * @param T The type of the preference value.
 * @param key The [Preferences.Key] for the desired preference.
 * @return A [Flow] that emits the preference value of type [T], or `null` if the key is missing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> DataStore<Preferences>.getPreferenceOrNull(key: Preferences.Key<T>): Flow<T?> {

    return data.catch { throwable ->

        when (throwable) {

            is IOException -> {

                Log.e(LOG_TAG, throwable.message, throwable)
                emit(value = emptyPreferences())
            }

            else -> throw throwable
        }
    }.mapLatest { preferences ->

        preferences[key]
    }.flowOn(context = Dispatchers.IO)
}

/**
 * Retrieves a preference value from the DataStore as a [Flow].
 *
 * This function observes changes to the preference associated with the given [key].
 * If the preference is not found, it emits the provided [initial] value. Any [IOException]
 * encountered during the read operation will be caught and logged, but will not
 * interrupt the flow. The flow is configured to run on the [Dispatchers.IO] context.
 *
 * @param T The type of the preference value.
 * @param key The [Preferences.Key] for the desired preference.
 * @param initial The default value to emit if the preference is not set.
 * @return A [Flow] that emits the preference value, or the [initial] value if not found.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> DataStore<Preferences>.getPreference(key: Preferences.Key<T>, initial: T): Flow<T> {

    return getPreferenceOrNull(key = key).flatMapLatest { valueLatest ->

        flowOf(value = valueLatest ?: initial)
    }.flowOn(context = Dispatchers.IO)
}

/**
 * Retrieves a [FontPreferenceItem] from the [DataStore] as a [Flow].
 *
 * This function reads a [String] value associated with the given [key] and attempts to match
 * it against the label of an item within the [entities] list. If a match is found, that
 * item is emitted; otherwise, `null` is emitted. Any [IOException] encountered during the
 * read operation is caught and logged, returning an empty preference set.
 *
 * @param key The [Preferences.Key] associated with the [FontPreferenceItem] label.
 * @param entities An [ImmutableList] of available [FontPreferenceItem]s to search within.
 * @return A [Flow] emitting the matched [FontPreferenceItem], or `null` if no match is found.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun DataStore<Preferences>.getPreference(
    key: Preferences.Key<String>,
    entities: ImmutableList<FontPreferenceItem>
): Flow<FontPreferenceItem?> {

    return getPreferenceOrNull(key = key).flatMapLatest { labelLatest ->

        flowOf(value = entities.find { fontItem -> fontItem.label == labelLatest })
    }.flowOn(context = Dispatchers.IO)
}

/**
 * Sets a preference value in the DataStore.
 *
 * This function is a `suspend` function and should be called from a coroutine scope.
 * It safely updates the preference value for the given [key]. Any exceptions during
 * the DataStore operation are caught and logged.
 *
 * @param T The type of the preference value.
 * @param key The [Preferences.Key] for the preference to be set.
 * @param value The value to set for the preference.
 */
suspend fun <T> DataStore<Preferences>.setPreference(key: Preferences.Key<T>, value: T) {

    try {

        updateData { preferences -> preferences.toMutablePreferences().apply { this[key] = value } }
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
    }
}

/**
 * Resets a preference value associated with the given key in the DataStore.
 *
 * This suspend function removes the preference entry identified by the provided [key].
 * If an exception occurs during the DataStore operation, it is caught and logged.
 *
 * @param T The type of the preference value.
 * @param key The [Preferences.Key] identifying the preference to be reset.
 */
suspend fun <T> DataStore<Preferences>.resetPreference(key: Preferences.Key<T>) {

    try {

        this@resetPreference.edit { preferences -> preferences.remove(key = key) }
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
    }
}

/**
 * Clears all preferences from the DataStore.
 *
 * This suspend function removes all key-value pairs stored in the DataStore.
 * It's a destructive operation and should be used with caution.
 * If an exception occurs during the clear operation, it is caught and logged.
 */
suspend fun DataStore<Preferences>.clearAllPreference() {

    try {

        this@clearAllPreference.edit { preferences -> preferences.clear() }
    } catch (exception: Exception) {

        Log.e(LOG_TAG, exception.message, exception)
    }
}