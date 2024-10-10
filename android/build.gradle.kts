plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget()
    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":common"))
                implementation(libs.activity.compose)
            }
        }
    }
}

android {
    @Suppress("UnstableApiUsage")
    buildToolsVersion = "34.0.0"
    compileSdk = 34
    @Suppress("UnstableApiUsage")
    sourceSets {
        // Quirk - for some reason this is unrecognised unless I use "main" - which is not its name
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
    defaultConfig {
        applicationId = "com.example.android"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            @Suppress("UnstableApiUsage")
            isMinifyEnabled = false
        }
    }
}