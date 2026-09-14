package io.bashpsk.emptylibs.datastoreui.utils

import android.util.Log

/**
 * Tag used for logging messages within the Datastore-UI library.
 */
internal const val LOG_TAG = "Datastore-UI"

@PublishedApi
internal fun String.setDebug() {

    Log.d("PSK", this)
}