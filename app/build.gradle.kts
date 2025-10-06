val ktor_version: String by project
val nav_version:  String by project
val hilt_version:  String by project

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id ("com.google.dagger.hilt.android")
    kotlin("plugin.serialization")
}


android {
    namespace = "com.example.SCRA"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.SCRA"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version") //Добавил
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

    implementation("androidx.navigation:navigation-compose:$nav_version") //2.7.7

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation ("androidx.datastore:datastore-preferences:1.1.1")

    implementation ("androidx.datastore:datastore-preferences-rxjava2:1.1.1")
    implementation ("androidx.datastore:datastore-preferences-rxjava3:1.1.1")

    implementation ("com.google.dagger:hilt-android:$hilt_version")
    kapt ("com.google.dagger:hilt-android-compiler:$hilt_version")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.5")

    implementation ("com.google.code.gson:gson:2.10.1")

    val room_version = "2.6.1"

    // FOR ROOM
    implementation ("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:$room_version")
    // END FOR ROOM

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")


    // FOR CAMERA
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")


    implementation ("androidx.camera:camera-core:1.3.4")
    implementation ("androidx.camera:camera-camera2:1.3.4")
    implementation ("androidx.camera:camera-lifecycle:1.3.4")
    implementation ("androidx.camera:camera-video:1.3.4")

    implementation ("androidx.camera:camera-view:1.3.4")
    implementation ("androidx.camera:camera-extensions:1.3.4")


    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("commons-io:commons-io:2.13.0")


    implementation("androidx.work:work-runtime-ktx:2.9.1")

    implementation("network.chaintech:qr-kit:1.0.2")

    // usb-serial-for-android
    //implementation("com.hoho:usb-serial-for-android:3.4.3")
    // https://mvnrepository.com/artifact/com.github.mik3y/usb-serial-for-android
    implementation("com.github.mik3y:usb-serial-for-android:3.4.6")

    //implementation("com.github.mik3y:usb-serial-for-android:3.4.6")

    // Kotlin Coroutines
    //implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
   // implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    //testImplementation ("junit:junit:4.13.2")
    //androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    //androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")


}