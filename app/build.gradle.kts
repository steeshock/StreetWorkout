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

    // Retrofit
    implementation(libs.bundles.retrofit)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

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
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Maps and location
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:19.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("org.mockito:mockito-android:3.12.4")
    testImplementation("org.mockito:mockito-core:3.12.4")

    //For runBlockingTest, CoroutineDispatcher etc.
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
    //For InstantTaskExecutorRule
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    //Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.3")

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
}
