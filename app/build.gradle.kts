plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.1.10"
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)

}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
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
        viewBinding = true
    }
    androidResources {
        generateLocaleConfig = true
    }
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}


dependencies {

    implementation(libs.androidx.junit.ktx)
    implementation(libs.core.ktx)

    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation ("org.robolectric:robolectric:4.11")


    val archTestingVersion = "2.2.0"//done
    val coroutinesVersion = "1.7.3"//done
    testImplementation("junit:junit:4.13.2")//done
    // Dependencies for local unit tests
    testImplementation("androidx.arch.core:core-testing:$archTestingVersion")//done
    // hamcrest
    testImplementation("org.hamcrest:hamcrest:2.2")//done
    testImplementation("org.hamcrest:hamcrest-library:2.2")//done
    androidTestImplementation("org.hamcrest:hamcrest:2.2")//done
    androidTestImplementation("org.hamcrest:hamcrest-library:1.3")//done
    //kotlinx-coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")//done
    //MockK
    testImplementation("io.mockk:mockk-android:1.13.17")
    testImplementation("io.mockk:mockk-agent:1.13.17")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
    implementation("network.chaintech:kmp-date-time-picker:1.0.7")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("com.airbnb.android:lottie-compose:6.6.3")
    implementation("com.google.android.libraries.places:places:3.1.0")//done
    implementation("com.google.maps.android:maps-compose:4.3.3")//done
    implementation(libs.play.services.maps)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    val nav_version = "2.8.8"//done
    implementation("androidx.navigation:navigation-compose:$nav_version")//done
    //Serialization for NavArgs
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")//done
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.8.7")
    val compose_version = "1.0.0"
    implementation("androidx.compose.runtime:runtime-livedata:$compose_version")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.room:room-ktx:2.6.1")//done
    implementation("androidx.room:room-runtime:2.6.1")//done
    ksp("androidx.room:room-compiler:2.6.1")//done
    implementation("com.google.android.gms:play-services-location:21.3.0")//done
    implementation(libs.androidx.core.ktx) //done
    implementation(libs.androidx.lifecycle.runtime.ktx)//done
    implementation(libs.androidx.activity.compose)//done
    implementation(platform(libs.androidx.compose.bom))//done
    implementation(libs.androidx.ui)//done
    implementation(libs.androidx.ui.graphics)//done
    implementation(libs.androidx.ui.tooling.preview)//done
    implementation(libs.androidx.material3)//done
    testImplementation(libs.junit)//done
    androidTestImplementation(libs.androidx.junit)//done
    androidTestImplementation(libs.androidx.espresso.core)//done
    androidTestImplementation(platform(libs.androidx.compose.bom))//done
    androidTestImplementation(libs.androidx.ui.test.junit4)//done
    debugImplementation(libs.androidx.ui.tooling)//done
    debugImplementation(libs.androidx.ui.test.manifest)//done
}