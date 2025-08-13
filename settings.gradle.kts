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
include(":formatter")
include(":datastore-ui")
include(":jetpack-ui")
include(":storage")
include(":kolor-picker")
include(":image-kolor")
include(":image-krop")
include(":image-edit")
