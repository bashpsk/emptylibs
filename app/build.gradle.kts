import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

android {

    namespace = "io.bashpsk.emptylibs"

    compileSdk {

        version = release(37)
    }

    defaultConfig {

        applicationId = "io.bashpsk.emptylibs"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            optimization {

                enable = false
            }
        }
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {

        compose = true
    }
}

kotlin {

    compilerOptions.jvmTarget = JvmTarget.JVM_17
}

dependencies {

    //  DEFAULT         :
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //  ICON            :
    implementation(libs.androidx.material.icons.extended)

    //  SHAPES          :
    implementation(libs.androidx.graphics.shapes)

    //  DATASTORE       :
    implementation(libs.androidx.datastore.preferences)

    //  KOTLINX         :
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.datetime)

    //  NAVIGATION      :
    implementation(libs.androidx.navigation.compose)

    implementation("androidx.pdf:pdf-viewer:1.0.0-alpha18")
    implementation("androidx.pdf:pdf-compose:1.0.0-alpha18")
    implementation("androidx.pdf:pdf-viewer-fragment:1.0.0-alpha18")

    implementation("androidx.ink:ink-authoring-compose:1.0.0")
    implementation("androidx.ink:ink-brush-compose:1.0.0")
    implementation("androidx.ink:ink-geometry-compose:1.0.0")
    implementation("androidx.ink:ink-nativeloader:1.0.0")
    implementation("androidx.ink:ink-rendering:1.0.0")
    implementation("androidx.ink:ink-storage:1.0.0")
    implementation("androidx.ink:ink-strokes:1.0.0")

    //  MODULE          :
    implementation(project(":animations"))
    implementation(project(":canvas-slate"))
    implementation(project(":compose-utils"))
    implementation(project(":compose-widgets"))
    implementation(project(":datastore-ui"))
    implementation(project(":formatter"))
    implementation(project(":gesture-ui"))
    implementation(project(":image-edit"))
    implementation(project(":image-kolor"))
    implementation(project(":image-krop"))
    implementation(project(":image-utils"))
    implementation(project(":image-view"))
    implementation(project(":image-wallpaper"))
    implementation(project(":jetpack-ui"))
    implementation(project(":kolor-picker"))
    implementation(project(":layouts"))
    implementation(project(":lrucache-manager"))
    implementation(project(":pdf-template"))
    implementation(project(":pdf-viewer"))
    implementation(project(":storage"))
}