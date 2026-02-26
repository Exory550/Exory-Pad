plugins {
    id("com.android.application") version libs.versions.android.gradlePlugin.get() apply false
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin.get() apply false
    id("com.squareup.sqldelight") version libs.versions.sqldelight.get() apply false
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get() apply false
    id("com.github.ben-manes.versions") version libs.versions.versionsPlugin.get()
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
