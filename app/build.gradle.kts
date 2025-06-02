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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Google Fonts para Poppins en Compose
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("androidx.navigation:navigation-compose:2.6.0")

    // Accompanist Pager para carousels (HorizontalPager)
    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    // Indicators (dots) para el Pager
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")

    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    implementation("androidx.compose.material3:material3:1.0.1")

    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("io.coil-kt:coil-compose:2.2.2")

    implementation ("androidx.compose.foundation:foundation:1.5.0")

    //DEPENDENCIAS PARA EMPEZAR A CONECTAR EL BACKEND
    
    // Retrofit y Gson converter
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines para llamadas asíncronas
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // ViewModel y LiveData (o StateFlow si usás)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Para cargar imágenes desde URL en Compose
    implementation ("io.coil-kt:coil-compose:2.2.2")
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.scalars)

    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)

    implementation("androidx.datastore:datastore-preferences:1.0.0")



}

val openApiOutputDir = file("$projectDir/src/main/java")

val downloadOpenApiSpec by tasks.registering {
    val outputFile = layout.buildDirectory.file("tmp/openapi/api.json")

    outputs.file(outputFile)

    doLast {
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
                "apiPackage" to "com.example.saborchef.api",
                "modelPackage" to "com.example.saborchef.models",
                "invokerPackage" to "com.example.saborchef.infrastructure"
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
                ) // o lo que prefieras
                .replace(
                    ".registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())",
                    ""
                ) // o lo que prefieras
                .replace(
                    ".registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())",
                    ""
                ) // o lo que prefieras
        }

        exclude("**/DateTimeAdapter.kt")
        exclude("*/LocalDateAdapter.kt")
    }

    doFirst {
        println("Copiando OpenAPI generado a src/main/java/com/example/saborchef")
    }
}