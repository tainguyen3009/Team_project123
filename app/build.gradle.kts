plugins {
    id("com.android.application")
}

android {
    namespace = "algonquin.cst2335.nguy1041"
    compileSdk = 34

    buildFeatures {
        viewBinding = true

    }


    defaultConfig {
        applicationId = "algonquin.cst2335.nguy1041"
        minSdk = 22
        targetSdk = 33
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//    implementation ("com.android.support:support-v4:33.0.0")
//    implementation ("com.android.support:design:33.0.0")
//
//     implementation ("com.android.support:cardview-v7:33.0.0")
//    implementation ("com.android.support:recyclerview-v7:33.0.0")
}