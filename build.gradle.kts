import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.7.10"
    id("io.freefair.lombok") version "6.5.0.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.github.ben-manes.versions") version "0.42.0"
}

group = "org.mongodb.scratch"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_13

repositories {
    mavenCentral()
}

val log4Version="2.18.0"
val junitVersion="5.9.0"
val assertjVersion="3.23.1"
val mongoVersion= "4.7.0"
val mongoCryptVersion="1.5.1.1"

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:$mongoVersion")
    implementation("org.mongodb:mongodb-crypt:$mongoCryptVersion")
    implementation(platform("org.apache.logging.log4j:log4j-bom:$log4Version"))
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}

//application {
//    mainClassName = "uk.dioxic.mongo.benchmark.Application"
//}