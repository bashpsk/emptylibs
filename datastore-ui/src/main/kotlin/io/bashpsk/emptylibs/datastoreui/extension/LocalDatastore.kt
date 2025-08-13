package io.bashpsk.emptylibs.datastoreui.extension

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * A CompositionLocal that provides access to the DataStore instance.
 *
 * This allows components within the composition to read and write preferences
 * without needing to explicitly pass the DataStore instance down the tree.
 *
 * It's crucial to provide a DataStore instance at the root of your Composable
 * hierarchy using `CompositionLocalProvider`.
 *
 * Example usage:
 * ```kotlin
 * val dataStore = LocalDatastore.current
 * // Now you can use dataStore to interact with preferences
 * ```
 *
 * If no DataStore is provided, accessing `LocalDatastore.current` will result
 * in an error.
 */
val LocalDatastore = staticCompositionLocalOf<DataStore<Preferences>> {
    error(message = "CompositionLocal LocalDatastore not present")
}