import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.lumio.lumiotelevison"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lumio.lumiotelevison"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        val localProps = Properties()
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) localProps.load(localFile.inputStream())
        val apiKey = (localProps.getProperty("NEWS_API_KEY") ?: project.findProperty("NEWS_API_KEY")?.toString())?.takeIf { it.isNotBlank() } ?: ""
        buildConfigField("String", "NEWS_API_KEY", "\"$apiKey\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file("lumio-release.keystore")
            storePassword = "lumio@123"
            keyAlias = "lumio"
            keyPassword = "lumio@123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    packaging {
        resources {
            excludes += listOf(
                "**/baseline-profile.txt",
                "**/baseline-prof.txt",
                "**/*.prof",
                "**/*.profm",
                "assets/dexopt/baseline.prof",
                "assets/dexopt/baseline.profm",
                "META-INF/androidx.profileinstaller_profileinstaller.version"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.coil.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Avoid INSTALL_BASELINE_PROFILE_FAILED: do not merge baseline/art profile into release APK
tasks.configureEach {
    if (name == "mergeReleaseArtProfile" || name == "compileReleaseArtProfile") {
        enabled = false
    }
}