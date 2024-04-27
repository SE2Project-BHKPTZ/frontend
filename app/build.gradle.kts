import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    jacoco
    id("org.sonarqube") version "4.4.1.3373"
}

android {
    namespace = "at.aau.serg"
    compileSdk = 34

    val properties = Properties()
    if(project.rootProject.file("local.properties").canRead()){
        properties.load(project.rootProject.file("local.properties").inputStream())
    }

    defaultConfig {
        applicationId = "at.aau.serg"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "api_url", properties.getProperty("api.url", "http://localhost:8081"), )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest")

    dependsOn(
        ":app:checkDebugDuplicateClasses",
        ":app:compressDebugAssets",
        ":app:generateDebugAndroidTestResValues",
        ":app:generateDebugAndroidTestLintModel",
        ":app:lintAnalyzeDebugAndroidTest",
        ":app:mergeReleaseAssets",
        ":app:jacocoDebug",
        ":app:compressReleaseAssets",
        ":app:extractProguardFiles",
        ":app:dexBuilderDebug",
        ":app:dexBuilderRelease",
        ":app:mergeExtDexDebug",
        ":app:mergeLibDexDebug",
        ":app:mergeProjectDexDebug",
        ":app:mergeDexRelease",
        ":app:compileReleaseArtProfile",
        ":app:checkReleaseDuplicateClasses",
        ":app:mergeDebugJavaResource",
        ":app:mergeDebugJniLibFolders",
        ":app:mergeReleaseJniLibFolders",
        ":app:packageDebug",
        ":app:packageRelease",
        ":app:lintAnalyzeDebug",
        ":app:generateDebugLintReportModel",
        ":app:lintVitalAnalyzeRelease",
        ":app:generateReleaseLintVitalReportModel",
        ":app:generateDebugUnitTestLintModel",
        ":app:lintAnalyzeDebugUnitTest",
        ":app:checkDebugAndroidTestAarMetadata",
        ":app:mergeDebugAndroidTestResources",
        ":app:mergeDebugAndroidTestResources",
        ":app:processDebugAndroidTestManifest",
        ":app:processDebugAndroidTestResources",
        ":app:compileDebugAndroidTestKotlin",
        ":app:compileDebugAndroidTestKotlin"
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoTestReport/html"))
    }

    val exclusionList = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    val kotlinClassesDir =
        layout.buildDirectory.dir("tmp/kotlin-classes/debug").get().asFile.absolutePath
    val javaClassesDir =
        layout.buildDirectory.dir("intermediates/javac/debug/classes").get().asFile.absolutePath
    val classesDir = fileTree(mapOf("dir" to kotlinClassesDir, "excludes" to exclusionList)) +
            fileTree(mapOf("dir" to javaClassesDir, "excludes" to exclusionList))
    val srcDir = files("src/main/java", "src/main/kotlin")

    classDirectories.setFrom(classesDir)
    sourceDirectories.setFrom(srcDir)
    executionData.setFrom(
        fileTree(
            mapOf(
                "dir" to project.projectDir,
                "includes" to listOf("**/*.exec", "**/*.ec")
            )
        )
    )
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
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${project.projectDir}/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
        )
        property("sonar.coverage.exclusions", "**/at/aau/serg/activities/**,**/at/aau/serg/utils/App.kt,**/at/aau/serg/fragments/**,**/at/aau/serg/placeholder/**")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.socketIoClient) {
        exclude(group = "org.json", module = "json")
    }
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockk.core)
    testImplementation(libs.okhttp.mock.webserver)
    testRuntimeOnly(libs.junit.jupiter.engine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}