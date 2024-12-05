plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.appchat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appchat"
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

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

}

dependencies {
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("io.github.florent37:shapeofview:1.4.7")
    implementation("androidx.fragment:fragment:1.6.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.8.7")
    // picasso para manejar imágenes
    implementation ("com.squareup.picasso:picasso:2.71828")
    // picasso para manejar imágenes
    implementation ("com.squareup.picasso:picasso:2.71828")
    //circle image view
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    // ShapeOfView
    implementation("io.github.florent37:shapeofview:1.4.7")
    implementation("com.github.parse-community.Parse-SDK-Android:bolts-tasks:4.3.0")
    implementation("com.github.parse-community.Parse-SDK-Android:parse:4.3.0")
    // For building media UIs
    implementation ("androidx.media3:media3-ui:1.4.1")

    // For building media playback UIs for Android TV using the Jetpack Leanback library
    implementation ("androidx.media3:media3-ui-leanback:1.4.1")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}