package io.bashpsk.emptylibs.pdfviewer.utils

import android.util.Log

internal const val LOG_TAG = "PdfViewer"

internal fun String.setDebug() {

    Log.d("PSK", this)
}