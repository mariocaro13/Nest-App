import java.util.Properties

// ============================================================
// Load local properties (e.g., API keys)
// ============================================================
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val imgBbApiKey: String = localProperties.getProperty("imgbbApiKey", "YOUR_DEFAULT_KEY_IF_MISSING")

// ============================================================
// Plugins configuration
// ============================================================
plugins {
    // Android application, Kotlin, and Compose plugins via version catalog alias
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Google services Gradle plugin
    alias(libs.plugins.google.services)
    // Hilt for dependency injection
//    alias(libs.plugins.hilt.android)
    // Kotlin Serialization plugin
    alias(libs.plugins.kotlin.serialization)
    // Parcelize plugin for making classes Parcelable
    //kotlin("parcelize")
    // Kapt for annotation processing
//    id("kotlin-kapt")
}

// ============================================================
// Android module configuration
// ============================================================
android {
    namespace = "com.example.carolsnest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.carolsnest"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject ImgBB API key into BuildConfig at compile time
        buildConfigField("String", "IMGBB_API_KEY", "\"${imgBbApiKey}\"")
    }

    // ============================================================
    // Build types configuration
    // ============================================================
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // ============================================================
    // Compile options and Kotlin configuration
    // ============================================================
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // ============================================================
    // Enable Jetpack Compose and other build features
    // ============================================================
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // ============================================================
    // Packaging options to avoid conflicts
    // ============================================================
    packaging {
        resources {
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}

// ============================================================
// Dependencies configuration
// ============================================================
dependencies {
    // ------------------------------------------------------------
    // Core AndroidX and Compose dependencies
    // ------------------------------------------------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    // Compose BOM to manage versions for Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ------------------------------------------------------------
    // Additional AndroidX, Navigation and credentials
    // ------------------------------------------------------------
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

    implementation(libs.androidx.navigation.compose)
//    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.firebase.database.ktx)

    // ------------------------------------------------------------
    // Testing dependencies
    // ------------------------------------------------------------
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ------------------------------------------------------------
    // Hilt dependencies for dependency injection
    // ------------------------------------------------------------
//    implementation(libs.hilt.android)
//    //kapt(libs.hilt.compiler)
//    kapt(libs.androidx.hilt.compiler)
//    implementation(libs.dagger.hilt.android.compiler)
//    implementation(libs.androidx.hilt.navigation.compose)

    // ------------------------------------------------------------
    // Firebase dependencies via Firebase BoM for version alignment
    // ------------------------------------------------------------
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.firestore.ktx)

    // ------------------------------------------------------------
    // Other third-party dependencies
    // ------------------------------------------------------------
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.play.services)

    // ------------------------------------------------------------
    // Ktor and Kotlinx Serialization for networking and JSON
    // ------------------------------------------------------------
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
}
