package io.bashpsk.emptylibs.imagewallpaper.wallpaper

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import io.bashpsk.emptylibs.imagewallpaper.utils.LOG_TAG
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
suspend fun WallpaperManager.setImageWallpaper(image: ImageBitmap?, type: WallpaperType): Boolean {

    return suspendCoroutine { continuation ->

        try {

            val result = image?.asAndroidBitmap()?.let { bitmap ->

                this@setImageWallpaper.setBitmap(bitmap, null, true, type.flag)
            }

            continuation.resume(value = result != 0)
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            continuation.resume(value = false)
        }
    }
}