package io.bashpsk.emptylibs.imagewallpaper.utils

import android.util.Log

internal const val LOG_TAG = "Image-Wallpaper"

fun String.setDebug() {

    Log.d("PSK", this)
}