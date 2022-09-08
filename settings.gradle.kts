include(
    ":app",
    ":design"
)
rootProject.name = "StreetWorkout"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("room", "2.4.2")
            version("retrofit", "2.9.0")
            version("coroutines", "1.6.1")
            version("navigation", "2.5.1")
            version("appcompat", "1.5.0")
            version("material", "1.6.1")
            version("datastore", "1.0.0")
            version("workmanager", "2.7.1")
            version("dagger", "2.40.5")
            version("firebaseBom", "28.2.0")

            // region Room
            library("androidx-room-runtime", "androidx.room", "room-runtime").versionRef("room")
            library("androidx-room-compiler", "androidx.room", "room-compiler").versionRef("room")
            library("androidx-room-ktx", "androidx.room", "room-ktx").versionRef("room")
            // endregion

            // region Retrofit
            library("retrofit", "com.squareup.retrofit2", "retrofit").versionRef("retrofit")
            library("converter-gson", "com.squareup.retrofit2", "converter-gson").versionRef("retrofit")
            library("adapter-rxjava2", "com.squareup.retrofit2", "adapter-rxjava2").versionRef("retrofit")
            bundle("retrofit", listOf("retrofit", "converter-gson", "adapter-rxjava2"))
            // endregion

            // region Lifecycle
            library("androidx-lifecycle-extensions", "androidx.lifecycle:lifecycle-extensions:2.2.0")
            library("androidx-lifecycle-viewmodel", "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
            bundle("androidx-lifecycle", listOf("androidx-lifecycle-extensions", "androidx-lifecycle-viewmodel"))
            // endregion

            // region Maps and location
            library("playServices-maps", "com.google.android.gms:play-services-maps:18.1.0")
            library("playServices-location", "com.google.android.gms:play-services-location:20.0.0")
            // endregion

            // Kotlin Coroutines
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("coroutines")
            library("kotlinx-coroutines-android", "org.jetbrains.kotlinx", "kotlinx-coroutines-android").versionRef("coroutines")
            library("kotlinx-coroutines-playServices", "org.jetbrains.kotlinx", "kotlinx-coroutines-play-services").versionRef("coroutines")
            bundle("kotlinx-coroutines", listOf("kotlinx-coroutines-core", "kotlinx-coroutines-android", "kotlinx-coroutines-playServices"))
            // endregion

            // Navigation
            library("androidx-navigation-fragmentKtx", "androidx.navigation", "navigation-fragment-ktx").versionRef("navigation")
            library("androidx-navigation-uiKtx", "androidx.navigation", "navigation-ui-ktx").versionRef("navigation")
            bundle("androidx-navigation", listOf("androidx-navigation-fragmentKtx", "androidx-navigation-uiKtx"))
            // endregion

            // Dagger
            library("dagger", "com.google.dagger", "dagger").versionRef("dagger")
            library("dagger-compiler", "com.google.dagger", "dagger-compiler").versionRef("dagger")
            // endregion

            // Other libs
            library("imageSlider", "com.github.denzcoskun:ImageSlideshow:0.1.0")
            library("imagePicker", "com.github.dhaval2404:imagepicker:2.1")
            // endregion

            library("androidx-appcompat", "androidx.appcompat", "appcompat").versionRef("appcompat")
            library("material", "com.google.android.material", "material").versionRef("material")
            library("androidx-activityKtx", "androidx.activity:activity-ktx:1.5.1")
            library("androidx-fragmentKtx", "androidx.fragment:fragment-ktx:1.5.2")
            library("androidx-coreKtx", "androidx.core:core-ktx:1.7.0")
            library("androidx-datastore", "androidx.datastore", "datastore-preferences").versionRef("datastore")
            library("androidx-workmanager", "androidx.work", "work-runtime-ktx").versionRef("workmanager")
            library("firebaseBom", "com.google.firebase", "firebase-bom").versionRef("firebaseBom")
        }
        create("testLibs") {
            version("mockito", "3.12.4")

            library("junit4", "junit:junit:4.13.2")
            library("junit-ext", "androidx.test.ext:junit:1.1.3")
            library("espresso-core", "androidx.test.espresso:espresso-core:3.4.0")
            library("mockito-core", "org.mockito", "mockito-core").versionRef("mockito")
            library("mockito-android", "org.mockito", "mockito-android").versionRef("mockito")
            bundle("unitTests", listOf("junit4", "mockito-core"))
            bundle("instrumentationTests", listOf("junit-ext", "espresso-core", "mockito-android"))
        }
    }
}

include(":domain")
