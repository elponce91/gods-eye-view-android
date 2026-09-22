plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.godseye.mobile"
    compileSdk = 35
    buildToolsVersion = "35.0.0"
    ndkVersion = "27.2.12479018"

    defaultConfig {
        applicationId = "com.godseye.mobile"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures { viewBinding = true }
}

tasks.register("verifyCiAndroidVersions") {
    group = "verification"
    description = "Verifies Android SDK, Build Tools, and NDK versions match CI environment variables."

    doLast {
        val expectedCompileSdk = System.getenv("ANDROID_COMPILE_SDK")
            ?: error("ANDROID_COMPILE_SDK environment variable is not set.")
        val expectedBuildTools = System.getenv("ANDROID_BUILD_TOOLS")
            ?: error("ANDROID_BUILD_TOOLS environment variable is not set.")
        val expectedNdk = System.getenv("ANDROID_NDK")
            ?: error("ANDROID_NDK environment variable is not set.")

        val actualCompileSdk = android.compileSdk
            ?: error("compileSdk is not configured.")
        val actualBuildTools = android.buildToolsVersion
        val actualNdk = android.ndkVersion

        val errors = mutableListOf<String>()
        if (actualCompileSdk.toString() != expectedCompileSdk) {
            errors += "compileSdk mismatch: Gradle=$actualCompileSdk, CI=$expectedCompileSdk"
        }
        if (actualBuildTools != expectedBuildTools) {
            errors += "Build Tools mismatch: Gradle=$actualBuildTools, CI=$expectedBuildTools"
        }
        if (actualNdk != expectedNdk) {
            errors += "NDK mismatch: Gradle=$actualNdk, CI=$expectedNdk"
        }

        if (errors.isNotEmpty()) {
            throw GradleException(buildString {
                appendLine("Android toolchain version verification FAILED:")
                errors.forEach { appendLine(" - $it") }
                appendLine()
                appendLine("Gradle and GitHub Actions must use identical Android toolchain versions.")
            })
        }

        logger.lifecycle("Android toolchain version verification PASSED:")
        logger.lifecycle(" - compileSdk: $actualCompileSdk")
        logger.lifecycle(" - Build Tools: $actualBuildTools")
        logger.lifecycle(" - NDK: $actualNdk")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    testImplementation("junit:junit:4.13.2")
}
