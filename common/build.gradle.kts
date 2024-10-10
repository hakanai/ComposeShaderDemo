import org.jetbrains.compose.compose

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.library)
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(11)
    androidTarget()
    jvm()
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            api(libs.appcompat)
            api(libs.core.ktx)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit)
        }
        androidInstrumentedTest.dependencies {
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        jvmTest.dependencies {
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
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
