include(
    ":app",
    ":design"
)
rootProject.name = "StreetWorkout"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // region Room
            version("room", "2.4.2")
            library("room-runtime", "androidx.room", "room-runtime").versionRef("room")
            library("room-compiler", "androidx.room", "room-compiler").versionRef("room")
            library("room-ktx", "androidx.room", "room-ktx").versionRef("room")
            // endregion
        }
    }
}

