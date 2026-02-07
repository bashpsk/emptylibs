pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "Empty Libs"
include(":app")
include(":animations")
include(":canvas-slate")
include(":compose-utils")
include(":compose-widgets")
include(":datastore-ui")
include(":formatter")
include(":gesture-ui")
include(":image-edit")
include(":image-kolor")
include(":image-krop")
include(":image-utils")
include(":image-view")
include(":image-wallpaper")
include(":jetpack-ui")
include(":kolor-picker")
include(":lrucache-manager")
include(":pdf-template")
include(":pdf-viewer")
include(":serialization-svg")
include(":storage")