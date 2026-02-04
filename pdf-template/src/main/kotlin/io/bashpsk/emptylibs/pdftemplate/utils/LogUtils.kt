package io.bashpsk.emptylibs.pdftemplate.utils

import android.util.Log

internal const val LOG_TAG = "PdfTemplate"

internal fun String.setDebug() {

    Log.d("PSK", this)
}