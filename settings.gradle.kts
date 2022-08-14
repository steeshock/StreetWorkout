include(
    ":app",
    ":design"
)
rootProject.name = "StreetWorkout"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // region Versions
            version("room", "2.4.2")
            version("retrofit", "2.9.0")
            version("coroutines", "1.6.1")
            // endregion

            // region Room
            library("room-runtime", "androidx.room", "room-runtime").versionRef("room")
            library("room-compiler", "androidx.room", "room-compiler").versionRef("room")
            library("room-ktx", "androidx.room", "room-ktx").versionRef("room")
            // endregion

            // region Retrofit
            library("retrofit", "com.squareup.retrofit2", "retrofit").versionRef("retrofit")
            library("converter-gson", "com.squareup.retrofit2", "converter-gson").versionRef("retrofit")
            library("adapter-rxjava2", "com.squareup.retrofit2", "adapter-rxjava2").versionRef("retrofit")
            bundle("retrofit", listOf("retrofit", "converter-gson", "adapter-rxjava2"))
            // endregion

            // region Lifecycle
            library("lifecycle-extensions", "androidx.lifecycle:lifecycle-extensions:2.2.0")
            library("lifecycle-viewmodel", "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
            bundle("lifecycle", listOf("lifecycle-extensions", "lifecycle-viewmodel"))
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
        }
        create("testLibs") {
            library("junit4", "junit:junit:4.13.2")
            library("mockito-core", "org.mockito:mockito-core:3.12.4")
            library("junit-ext", "androidx.test.ext:junit:1.1.3")
            library("espresso-core", "androidx.test.espresso:espresso-core:3.4.0")
            library("mockito-android", "org.mockito:mockito-android:3.12.4")
            bundle("unitTests", listOf("junit4", "mockito-core"))
            bundle("instrumentationTests", listOf("junit-ext", "espresso-core", "mockito-android"))
        }
    }
}

