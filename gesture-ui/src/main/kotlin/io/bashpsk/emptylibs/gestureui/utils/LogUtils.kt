package io.bashpsk.emptylibs.gestureui.utils

import android.util.Log

internal const val LOG_UTILS = "EmptyLibs"

internal fun String.setDebug() {

    Log.d("PSK", this)
}