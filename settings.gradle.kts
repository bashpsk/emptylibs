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
    }
}

rootProject.name = "Empty Libs"
include(":app")
include(":canvas-slate")
include(":compose-utils")
include(":datastore-ui")
include(":formatter")
include(":gesture-ui")
include(":image-edit")
include(":image-kolor")
include(":image-krop")
include(":image-utils")
include(":image-view")
include(":kolor-picker")
include(":lrucache-manager")
include(":jetpack-ui")
include(":storage")