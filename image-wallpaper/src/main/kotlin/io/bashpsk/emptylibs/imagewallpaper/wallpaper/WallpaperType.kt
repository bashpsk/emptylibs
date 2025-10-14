package io.bashpsk.emptylibs.imagewallpaper.wallpaper

import android.app.WallpaperManager

/**
 * Represents the different types of wallpapers that can be set on an Android device.
 * Each type corresponds to a specific screen or combination of screens.
 *
 * @property label A user-friendly string representing the wallpaper type.
 * @property flag The integer flag used by [android.app.WallpaperManager] to identify the target
 * wallpaper surface.
 */
enum class WallpaperType(val label: String, val flag: Int) {

    /**
     * Represents the home screen wallpaper.
     */
    Home(label = "Home", flag = WallpaperManager.FLAG_SYSTEM),

    /**
     * Represents the device's lock screen wallpaper.
     */
    Lock(label = "Lock", flag = WallpaperManager.FLAG_LOCK),

    /**
     * Represents setting the wallpaper on both the home screen and the lock screen.
     * This combines the functionality of [Home] and [Lock].
     */
    HomeAndLock(
        label = "Home & Lock",
        flag = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
    );
}