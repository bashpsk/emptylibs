package io.bashpsk.emptylibs.datastoreui.extension

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.bashpsk.emptylibs.datastoreui.utils.LOG_TAG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

/**
 * A [CoroutineExceptionHandler] that logs any uncaught exceptions from coroutines
 * using the Android Log framework with the tag [LOG_TAG].
 */
internal val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

    Log.e(LOG_TAG, throwable.message, throwable)
}

/**
 * Retrieves a preference value from the DataStore.
 *
 * This function observes changes to the preference associated with the given [key].
 * If the preference is not found, it returns the [initial] value.
 * Operations are performed on the IO dispatcher.
 *
 * @param T The type of the preference value.
 * @param key The [Preferences.Key] for the desired preference.
 * @param initial The default value to return if the preference is not set.
 * @return A [Flow] that emits the preference value.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> DataStore<Preferences>.getPreference(key: Preferences.Key<T>, initial: T): Flow<T> {

    return this.data.mapLatest { preferences ->

        preferences[key] ?: initial
    }.flowOn(context = Dispatchers.IO)
}

/**
 * Sets a preference value in the DataStore.
 *
 * This function is a suspend function, meaning it should be called from a coroutine or another
 * suspend function.
 * It operates on the IO dispatcher to avoid blocking the main thread.
 * Any exceptions during the DataStore operation are caught by the [exceptionHandler].
 *
 * @param T The type of the preference value.
 * @param key The [Preferences.Key] for the preference to be set.
 * @param value The value to set for the preference.
 */
suspend fun <T> DataStore<Preferences>.setPreference(key: Preferences.Key<T>, value: T) {

    withContext(context = Dispatchers.IO + exceptionHandler) {

        updateData { preferences -> preferences.toMutablePreferences().apply { this[key] = value } }
    }
}

/**
 * Resets a preference value associated with the given key in the DataStore.
 *
 * This function removes the preference entry identified by the provided `key`.
 * The operation is performed on an IO dispatcher and includes an exception handler
 * to log any errors that might occur during the process.
 *
 * @param T The type of the preference value.
 * @param key The [Preferences.Key] identifying the preference to be reset.
 */
suspend fun <T> DataStore<Preferences>.resetPreference(key: Preferences.Key<T>) {

    withContext(context = Dispatchers.IO + exceptionHandler) {

        this@resetPreference.edit { preferences -> preferences.remove(key = key) }
    }
}