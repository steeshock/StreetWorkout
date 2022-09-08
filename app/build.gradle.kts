plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}
android {
    defaultConfig {
        applicationId = "com.steeshock.streetworkout"
        compileSdk = 32
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_URL", "\"https://my-json-server.typicode.com/\"")
        resValue("string", "google_maps_key", "AIzaSyAFbIm4Ii_HquUrbI7-BUU668yG6QkG_kQ")
    }

    buildTypes {
        getByName("release")  {
            isMinifyEnabled = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures.viewBinding = true
}

dependencies {

    implementation(projects.design)
    implementation(projects.domain)

    implementation(libs.material)
    implementation(libs.dagger)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragmentKtx)
    implementation(libs.androidx.activityKtx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.workmanager)
    implementation(libs.playServices.maps)
    implementation(libs.playServices.location)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.imageSlider)
    implementation(libs.imagePicker)

    implementation(platform(libs.firebaseBom))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    kapt(libs.androidx.room.compiler)
    kapt(libs.dagger.compiler)

    testImplementation(testLibs.bundles.unitTests)
    androidTestImplementation(testLibs.bundles.instrumentationTests)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
