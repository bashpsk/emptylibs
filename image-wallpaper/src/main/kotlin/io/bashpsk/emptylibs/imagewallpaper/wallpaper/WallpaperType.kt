package io.bashpsk.emptylibs.imagewallpaper.wallpaper

import android.app.WallpaperManager

enum class WallpaperType(val label: String, val flag: Int) {

    Home(label = "Home", flag = WallpaperManager.FLAG_SYSTEM),

    Lock(label = "Lock", flag = WallpaperManager.FLAG_LOCK),

    HomeAndLock(
        label = "Home & Lock",
        flag = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
    );
}