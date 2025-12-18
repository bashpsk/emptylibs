package io.bashpsk.emptylibs.datastoreui.extension

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import io.bashpsk.emptylibs.datastoreui.utils.LOG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

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

    return this.data.catch { throwable ->

        when (throwable) {

            is IOException -> {

                Log.e(LOG_TAG, throwable.message, throwable)
                emit(value = emptyPreferences())
            }

            else -> throw throwable
        }
    }.mapLatest { preferences ->

        preferences[key] ?: initial
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