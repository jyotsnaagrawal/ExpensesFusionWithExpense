plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services") // Ensure Firebase services are enabled
}

android {
    namespace = "com.jyotsna.expensesfusion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jyotsna.expensesfusion"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Android and Material Design dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.7.0")) // Firebase BOM for version management
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication
    implementation("com.google.firebase:firebase-database-ktx") // Firebase Realtime Database (KTX)

    // MPAndroidChart dependency
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Foundation and support libraries
    implementation(libs.androidx.foundation.android)
    implementation(libs.support.annotations)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)

    // Unit Testing dependencies
    testImplementation(libs.junit)

    // Android Testing dependencies
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
