plugins {
    kotlin("jvm") version "1.9.23"
}

group = "asubb"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven { setUrl("https://jogamp.org/deployment/maven") }
    mavenCentral()
    maven("https://raw.githubusercontent.com/kotlin-graphics/mary/master")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("com.danielgergely.kgl:kgl:0.0.1-SNAPSHOT")
    implementation("com.danielgergely.kgl:kgl-jogl:0.0.1-SNAPSHOT")

//    implementation(libs.org.jogamp.gluegen.rt)
//    implementation(libs.org.jogamp.jogl.all)
//    implementation(libs.kotlin.grafics.glm)
//    implementation(libs.kotlin.grafics.unosdk)
    // TODO autodownload
// https://mvnrepository.com/artifact/org.jogamp.gluegen/gluegen-rt-natives-macosx-universal
    implementation(files("jar/gluegen-rt.jar", "jar/jogl-all.jar"))
    implementation("io.github.kotlin-graphics:glm:0.9.9.1-12")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
}

