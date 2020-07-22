plugins {
    java
    id("io.freefair.lombok") version "5.1.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "org.mongodb.scratch"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    jcenter()
}

val log4Version="2.13.3"
val junitVersion="5.6.2"
val assertjVersion="3.16.1"
val mongoVersion="3.12.5"

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:$mongoVersion")
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

//application {
//    mainClassName = "uk.dioxic.mongo.benchmark.Application"
//}