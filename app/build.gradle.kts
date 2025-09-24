plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.avmessanger"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.avmessanger"
        minSdk = 24
        targetSdk = 36
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
}



    dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.firebase.database)
        implementation(libs.firebase.storage)
        implementation(libs.firebase.auth)
        implementation(libs.credentials)
        implementation(libs.credentials.play.services.auth)
        implementation(libs.googleid)
        implementation(libs.recyclerview)
        implementation(libs.androidx.recyclerview)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation("de.hdodenhof:circleimageview:3.1.0")

        implementation("com.squareup.picasso:picasso:2.71828")
        implementation("com.google.firebase:firebase-auth:21.0.1")


        implementation("com.google.android.gms:play-services-recaptcha:16.0.0")
        implementation("com.google.android.gms:play-services-safetynet:18.0.1")
// Use the latest version
    }
