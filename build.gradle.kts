plugins {
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
    id("com.gradle.plugin-publish") version "1.1.0"
    signing
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("io.methvin", "directory-watcher", "0.18.0")
}

val javaVersion = JavaVersion.VERSION_17
val charset = Charsets.UTF_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.ordinal + 1))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
    }
}

tasks {
    compileJava {
        options.encoding = charset.name()
        options.release.set(javaVersion.ordinal + 1)
    }

    processResources {
        filteringCharset = charset.name()
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = javaVersion.majorVersion
        }
    }
}

gradlePlugin {
    website.set("https://github.com/Siroshun09/MessageClassGenerator")
    vcsUrl.set("https://github.com/Siroshun09/MessageClassGenerator.git")
    plugins {
        create("message-class-generator") {
            id = "com.github.siroshun09.messageclassgenerator"
            displayName = "Message Class Generator Plugin"
            description = "A Gradle plugin that generate message classes from files."
            tags.set(listOf("generator", "minecraft", "adventure"))
            implementationClass = "com.github.siroshun09.messageclassgenerator.MessageClassGeneratorPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "StagingRepository"
            url = uri("./staging")
        }
    }
}

signing {
    isRequired = !System.getenv().containsKey("GITHUB_ACTIONS")
    if (isRequired) {
        useGpgCmd()
    }
}
