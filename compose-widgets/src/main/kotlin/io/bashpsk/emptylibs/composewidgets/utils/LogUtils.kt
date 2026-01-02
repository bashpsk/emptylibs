package io.bashpsk.emptylibs.composewidgets.utils

import android.util.Log

internal const val LOG_TAG = "ComposeWidgets"

internal fun String.setDebug() {

    Log.d("PSK", this)
}