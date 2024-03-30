plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    jacoco
    id("org.sonarqube") version "4.4.1.3373"
}

android {
    namespace = "at.aau.serg"
    compileSdk = 34

    defaultConfig {
        applicationId = "at.aau.serg"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoTestReport/html"))
    }

    val exclusionList = listOf("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*", "android/**/*.*")
    val kotlinClassesDir = layout.buildDirectory.dir("tmp/kotlin-classes/debug").get().asFile.absolutePath
    val javaClassesDir = layout.buildDirectory.dir("intermediates/javac/debug/classes").get().asFile.absolutePath
    val classesDir = fileTree(mapOf("dir" to kotlinClassesDir, "excludes" to exclusionList)) +
            fileTree(mapOf("dir" to javaClassesDir, "excludes" to exclusionList))
    val srcDir = files("src/main/java", "src/main/kotlin")

    classDirectories.setFrom(classesDir)
    sourceDirectories.setFrom(srcDir)
    executionData.setFrom(files(layout.buildDirectory.asFile.get().toString() + "/jacoco/jacocoTestReport.exec"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

sonar {
    properties {
        property("sonar.projectKey", "SE2Project-BHKPTZ_frontend")
        property("sonar.organization", "se2project-bhkptz")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.projectDir}/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}