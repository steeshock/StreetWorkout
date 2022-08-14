buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("com.google.gms:google-services:4.3.13")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
    }
    extra.apply {
        set("compose_version", "1.0.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}