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
        }
    }
}

