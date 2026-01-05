package io.bashpsk.emptylibs.imageview.utils

import android.util.Log

internal const val LOG_TAG = "ImageView"

internal fun String.setDebug() {

    Log.d("PSK", this)
}