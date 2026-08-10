import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {

    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

android {

    namespace = "io.bashpsk.emptylibs.imageview"

    compileSdk {

        version = release(37)
    }

    defaultConfig {

        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    publishing {

        singleVariant("release")
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
    implementation(libs.androidx.window.size)
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

    //  KOTLINX         :
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.datetime)

    //  COIL            :
    implementation(platform(libs.coil3.bom))
    implementation(libs.coil3.compose)

    //  MODULE          :
    implementation(project(":compose-utils"))
    implementation(project(":formatter"))
    implementation(project(":gesture-ui"))
    implementation(project(":image-utils"))
    implementation(project(":jetpack-ui"))
    implementation(project(":layouts"))
    implementation(project(":lrucache-manager"))
}

publishing {

    publications {

        register<MavenPublication>("release") {

            groupId = "io.bashpsk.emptylibs"
            artifactId = "image-view"
            version = "1.0.0"

            afterEvaluate {

                from(components["release"])
            }
        }
    }
}