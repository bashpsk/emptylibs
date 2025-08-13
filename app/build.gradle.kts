import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

android {

    namespace = "io.bashpsk.emptylibs"
    compileSdk = 36

    defaultConfig {

        applicationId = "io.bashpsk.emptylibs"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {

        compilerOptions.jvmTarget = JvmTarget.JVM_17
    }

    buildFeatures {

        compose = true
    }
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

    //  DATASTORE       :
    implementation(libs.androidx.datastore.preferences)

    //  KOTLINX         :
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.datetime)

    //  NAVIGATION      :
    implementation(libs.androidx.navigation.compose)

    //  MODULE              :
    implementation(project(":datastore-ui"))
    implementation(project(":formatter"))
    implementation(project(":gesture-ui"))
    implementation(project(":image-edit"))
    implementation(project(":image-kolor"))
    implementation(project(":image-krop"))
    implementation(project(":image-view"))
    implementation(project(":jetpack-ui"))
    implementation(project(":kolor-picker"))
    implementation(project(":storage"))
}