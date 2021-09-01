val kotlinVersion = "15.30"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.30"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("javax.inject:javax.inject:1")

    testImplementation("org.amshove.kluent:kluent:1.68")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.mockito:mockito-junit-jupiter:3.12.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_16.toString()
    }
}
