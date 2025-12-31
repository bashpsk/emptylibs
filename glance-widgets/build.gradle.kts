import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {

    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

android {

    namespace = "io.bashpsk.emptylibs.glancewidgets"

    compileSdk {

        version = release(36)
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

    kotlin {

        compilerOptions.jvmTarget = JvmTarget.JVM_17
    }

    publishing {

        singleVariant("release")
    }
}

dependencies {

    //  DEFAULT         :
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //  ICON            :
    implementation(libs.androidx.material.icons.extended)

    //  DATASTORE       :
    implementation(libs.androidx.datastore.preferences)

    //  KOTLINX         :
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.datetime)

    //  GLANCE          :
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)

    //  MODULE          :
    implementation(project(":formatter"))
}

publishing {

    publications {

        register<MavenPublication>("release") {

            groupId = "io.bashpsk.emptylibs"
            artifactId = "glance-widgets"
            version = "1.0.0"

            afterEvaluate {

                from(components["release"])
            }
        }
    }
}