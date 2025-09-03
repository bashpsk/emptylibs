package io.bashpsk.emptylibs.lrucachemanager.utils

import android.util.Log

internal const val LOG_TAG = "LruCache-Manager"

internal fun String.setDebug() {

    Log.d("PSK", this)
}