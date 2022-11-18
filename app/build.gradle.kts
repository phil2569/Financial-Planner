plugins {
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.scott.financialplanner"
        minSdk = 26
        targetSdk = 33
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
}

dependencies {
    Versions.apply {
        // Android Core
        implementation("androidx.appcompat:appcompat:$androidx_appcompat")
        implementation("androidx.core:core-ktx:$androidx_core_ktx")
        implementation("androidx.constraintlayout:constraintlayout:$androidx_constraint_layout")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")
        implementation("com.google.android.material:material:$material")

        // Compose
        val composeBom = platform("androidx.compose:compose-bom:2022.10.00")
        implementation(composeBom)
        androidTestImplementation(composeBom)
        implementation("androidx.compose.material:material")
        implementation("androidx.compose.ui:ui-tooling:$compose_version")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.constraintlayout:constraintlayout-compose:$compose_constraint_version")
        implementation("androidx.activity:activity-compose:$compose_activity_version")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$compose_viewmodel")

        // Hilt
        kapt("com.google.dagger:hilt-android-compiler:$hilt")
        implementation("com.google.dagger:hilt-android:$hilt")

        // Room
        implementation("androidx.room:room-runtime:$room")
        kapt("androidx.room:room-compiler:$room")

        // Unit Test
        testImplementation("app.cash.turbine:turbine:$turbine")
        testImplementation("io.mockk:mockk:$mockk")
        testImplementation("junit:junit:$junit")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines")

        // Android Test
        androidTestImplementation("androidx.test:core:$androidx_test")
        androidTestImplementation("androidx.test:runner:$androidx_test")
        androidTestImplementation("androidx.test:rules:$androidx_test")
        androidTestImplementation("androidx.test.espresso:espresso-core:$espresso")
        androidTestImplementation("androidx.test.ext:junit:$androidx_test_junit")
        androidTestImplementation("io.mockk:mockk-android:$mockk")

        // Shared Test
        api("io.kotest:kotest-assertions-api:$kotest")
        api("io.kotest:kotest-assertions-core:$kotest")
        api("io.kotest:kotest-assertions-shared:$kotest")
    }
}