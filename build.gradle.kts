plugins {
    java
    // Kütüphaneyi JAR içine gömmek için gerekli plugin
    id("com.gradleup.shadow") version "9.3.0"
}

group = "io.hearlov.nexus.db"
version = "1.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 1. Senin yerel PowerNukkitX dosyan (Sadece derleme için, JAR içine girmez)
    compileOnly(files("../libs/powernukkitx.jar"))

    implementation("com.h2database:h2:2.2.224")
    implementation("com.mysql:mysql-connector-j:9.5.0")
}

tasks {
    // Standart JAR görevini devre dışı bırakıp ShadowJar'ı ana yapıyoruz
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}