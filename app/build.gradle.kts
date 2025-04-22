plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Google Service
    id("com.google.gms.google-services")
    // Enabling kapt
    kotlin("kapt")
}

android {
    namespace = "com.basecampers"
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.basecampers"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
        // used to fix kapt issue?
        languageVersion = "1.9"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation ("com.google.code.gson:gson:2.10.1")
    // Firebase - using BOM pattern for consistent versioning
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    ///Analytics
    implementation("com.google.firebase:firebase-analytics")
    ///Auth
    implementation("com.google.firebase:firebase-auth")
    ///Realtime Database
    implementation("com.google.firebase:firebase-database")
    ///FireStore
    implementation("com.google.firebase:firebase-firestore-ktx")
    
    // Navigation & UI Components
    implementation("com.arkivanov.decompose:decompose:1.0.0")
    implementation("com.arkivanov.decompose:extensions-compose-jetpack:1.0.0")
    
    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended")
    
    // Core Android & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")//2.6.2||2.8.7?


    //Storage


    implementation("com.google.firebase:firebase-storage-ktx")

//Coil
    implementation("io.coil-kt:coil-compose:2.4.0")




    // Room Database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    // Annotation processor for Room
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}