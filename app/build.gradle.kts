import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.openapi.generator)
}

android {
    namespace = "com.example.saborchef"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.saborchef"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Usar la misma URL en ambos lugares
        buildConfigField("String","BASE_URL","\"http://10.0.2.2:8080/\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM y UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Google Fonts para Poppins en Compose
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")

    // Accompanist Pager para carousels
    implementation("com.google.accompanist:accompanist-pager:0.32.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.32.0")

    // Compose Runtime y Lifecycle
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.compose.foundation:foundation:1.5.4")

    // ViewModel y LiveData - VERSIONES CONSISTENTES
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // NETWORKING - VERSIONES CONSISTENTES Y ACTUALIZADAS
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}

val openApiOutputDir = file("$projectDir/src/main/java")

val downloadOpenApiSpec by tasks.registering {
    val outputFile = layout.buildDirectory.file("tmp/openapi/api.json")
    outputs.file(outputFile)

    doLast {
        // CAMBIO: Usar la misma URL que en BuildConfig pero adaptada para localhost
        val uri = URI("http://localhost:8080/v3/api-docs")
        val url = uri.toURL()
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()

        println("Descargando OpenAPI spec desde $url...")

        url.openStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        println("Guardado en: ${file.absolutePath}")
    }
}

tasks.named("openApiGenerate").configure {
    dependsOn(downloadOpenApiSpec)
    (this as org.openapitools.generator.gradle.plugin.tasks.GenerateTask).apply {
        generatorName.set("kotlin")
        inputSpec.set(layout.buildDirectory.file("tmp/openapi/api.json").get().asFile.toURI().toString())
        configOptions.set(
            mapOf(
                "library" to "jvm-retrofit2",
                "serializationLibrary" to "gson",
                "packageName" to "com.example.saborchef",
                "apiPackage" to "com.example.saborchef.apis", // Cambiado de "api" a "apis"
                "modelPackage" to "com.example.saborchef.models",
                "invokerPackage" to "com.example.saborchef.infrastructure",
                // AGREGADO: Para generar suspend functions
                "useCoroutines" to "true"
            )
        )
    }
}

val generateAPI by tasks.registering(Copy::class) {
    dependsOn("openApiGenerate")

    val buildDir = "build/generate-resources/main/src/main/kotlin/com/example/saborchef/"
    val outputDir = "src/main/java/com/example/saborchef/"

    into(outputDir)

    from(buildDir + "apis") {
        into("apis")
    }

    from(buildDir + "models") {
        into("models")
        exclude("/*DeliveryDTO.kt")
    }

    from(buildDir + "infrastructure") {
        into("infrastructure")
        filter { line ->
            line
                .replace(
                    ".registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())",
                    ""
                )
                .replace(
                    ".registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())",
                    ""
                )
                .replace(
                    ".registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())",
                    ""
                )
        }

        exclude("**/DateTimeAdapter.kt")
        exclude("*/LocalDateAdapter.kt")
    }

    doFirst {
        println("Copiando OpenAPI generado a src/main/java/com/example/saborchef")
    }
}