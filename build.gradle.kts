import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

group = "cz.moznabude"
version = "0.0"

val serializationVersion = "0.13.0"
plugins {
    kotlin("multiplatform") version "1.3.61"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.61"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.10.0"
}

buildscript {
    val dokkaVersion = "0.9.18"
    dependencies {
        classpath("org.jetbrains.dokka:dokka-android-gradle-plugin:$dokkaVersion")
    }
}


repositories {
    jcenter()
    mavenCentral()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/JoHavel/ko-std-alg")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GIT_KEY")
            }
        }
    }
    publications {
        register("gpr", MavenPublication::class) {
            from(components["kotlin"])
        }
    }
}

kotlin {
    jvm()
    js("js") {
        browser {
        }
        nodejs {
        }
    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    mingwX64("mingw")
    sourceSets {
        val commonMain by getting {
            dependencies {
                kotlin("stdlib-common")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val mingwMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serializationVersion")
            }
        }
        val mingwTest by getting {
        }
    }
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
        configuration {
            externalDocumentationLink {
                url = URL("https://example.com/docs/")
            }
        }
        multiplatform {
            val js by creating { // The same name as in Kotlin Multiplatform plugin, so the sources are fetched automatically
                includes = listOf("packages.md")
            }

            register("jvm") { // Different name, so source roots must be passed explicitly
                targets = listOf("JVM")
                platform = "jvm"
            }
        }
    }
}
