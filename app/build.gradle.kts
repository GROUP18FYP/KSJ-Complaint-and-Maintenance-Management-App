plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ksjcomplaintnmaintenance"

    // Set this to 34 since you cannot use 35
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ksjcomplaintnmaintenance"
        minSdk = 24

        // Set this to 34 as well
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Volley for API calls
    implementation("com.android.volley:volley:1.2.1")

    // In dependencies block of build.gradle.kts
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Standard Android Libraries
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)

    // Other libraries
    implementation(libs.filament.android)
    implementation(libs.camera.camera2.pipe)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}