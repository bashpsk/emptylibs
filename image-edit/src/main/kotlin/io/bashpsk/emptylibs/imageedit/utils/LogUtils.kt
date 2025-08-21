package io.bashpsk.emptylibs.imageedit.utils

import android.util.Log

internal const val LOG_TAG = "ImageEdit"

internal fun String.setDebug() {

    Log.d("PSK", this)
}