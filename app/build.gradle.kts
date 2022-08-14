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

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.kotlinx.coroutines)
    testImplementation(testLibs.bundles.unitTests)
    testImplementation(testLibs.bundles.instrumentationTests)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Maps and location
    implementation(libs.playServices.maps)
    implementation(libs.playServices.location)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:28.2.0"))

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database-ktx")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")

    // Declare the dependency for the Cloud Storage library
    implementation("com.google.firebase:firebase-storage-ktx")

    // Support Libraries
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.6.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")

    //For runBlockingTest, CoroutineDispatcher etc.
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
    //For InstantTaskExecutorRule
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    // Dagger
    implementation("com.google.dagger:dagger:2.40.5")
    kapt("com.google.dagger:dagger-compiler:2.40.5")

    // Fragments
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // WorkManager Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:2.7.1")


    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
