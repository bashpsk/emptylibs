package io.bashpsk.emptylibs.imagewallpaper.wallpaper

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import io.bashpsk.emptylibs.imagewallpaper.utils.LOG_TAG
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Sets the device wallpaper from an [ImageBitmap].
 *
 * This is a suspend function that wraps the [WallpaperManager.setBitmap] call in a coroutine.
 * It handles setting the wallpaper for the home screen, lock screen, or both.
 *
 * @receiver The [WallpaperManager] instance.
 * @param image The [ImageBitmap] to set as the wallpaper. If null, the function will do nothing and
 * return false.
 * @param type The type of wallpaper to set, which can be
 * [WallpaperType.Home], [WallpaperType.Lock], or [WallpaperType.HomeAndLock].
 * @return `true` if the wallpaper was set successfully, `false`
 * otherwise (e.g., if the image is null or an exception occurs).
 * @see WallpaperManager.setBitmap
 * @see WallpaperType
 */
@SuppressLint("MissingPermission")
suspend fun WallpaperManager.setImageWallpaper(image: ImageBitmap?, type: WallpaperType): Boolean {

    return suspendCoroutine { continuation ->

        try {

            val result = image?.asAndroidBitmap()?.let { bitmap ->

                when (type) {

                    WallpaperType.HomeAndLock -> {

                        setBitmap(bitmap, null, true, WallpaperType.Home.flag)
                        setBitmap(bitmap, null, true, WallpaperType.Lock.flag)
                    }

                    else -> setBitmap(bitmap, null, true, type.flag)
                }
            }

            continuation.resume(value = result != 0)
        } catch (exception: Exception) {

            Log.e(LOG_TAG, exception.message, exception)
            continuation.resume(value = false)
        }
    }
}