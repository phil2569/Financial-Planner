// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    Versions.apply {
        id("com.android.application") version androidx_gradle_plugin apply false
        id("com.android.library") version androidx_gradle_plugin apply false
        id("org.jetbrains.kotlin.android") version jetbrains apply false
        id("com.google.dagger.hilt.android") version hilt apply false
    }

}

tasks {
    register("type", Delete::class) {
        delete(rootProject.buildDir)
    }
}