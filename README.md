# 🍳 LetMeCook - Aplikasi Resep AndroidAdd commentMore actions

**LetMeCook** adalah aplikasi resep masakan berbasis Android yang dirancang untuk membantu pengguna menemukan, mengelola, dan mendapatkan inspirasi resep. Aplikasi ini dilengkapi dengan fitur cerdas seperti rekomendasi resep harian, manajemen inventaris bahan, dan asisten AI yang didukung oleh Google Gemini untuk memberikan rekomendasi resep secara real-time.

---

## ✨ Fitur Utama

- **Daftar Resep Lengkap**  
  Menampilkan ratusan resep dari database lokal yang dimuat dari file CSV.

- **Chat dengan AI Gemini**  
  Pengguna dapat berinteraksi dengan asisten AI untuk meminta rekomendasi resep berdasarkan bahan atau nama masakan. Respons dari AI berupa kartu resep yang interaktif.

- **Manajemen Inventaris**  
  Fitur untuk mencatat bahan-bahan yang dimiliki pengguna di rumah. Pengguna dapat menambah, mengubah, dan menghapus item inventaris.

- **Resep Favorit**  
  Pengguna dapat menandai resep sebagai favorit untuk akses cepat.

- **Tema Terang/Gelap**  
  Pengguna dapat menggunakan tema sesuai selera.

---

## 🛠 Teknologi dan Arsitektur

- **Bahasa:** Java  
- **Arsitektur:** MVVM (Model-View-ViewModel)  
- **UI:** Material Design 3, RecyclerView  
- **Navigasi:** Jetpack Navigation Component  
- **Database:** Room (wrapper untuk SQLite)  
- **Asynchronous:** ExecutorService  
- **Networking:** Retrofit 2  
- **Image Loading:** Glide  
- **Parsing:** Gson & OpenCSV

---

## 📋 Prasyarat

Pastikan Anda telah menginstal:

- Android Studio (disarankan versi Giraffe atau yang lebih baru)
- JDK 11 atau lebih baru

---

## 🚀 Panduan Instalasi dan Menjalankan Proyek

### 🔹 Langkah 1: Buat Proyek Baru di Android Studio

1. Buka Android Studio, pilih **File > New > New Project...**
2. Pilih template **Empty Views Activity** dan klik **Next**.
3. Konfigurasikan:
   - **Name:** LetMeCook  
   - **Package name:** `com.example.letmecook`  
   - **Language:** Java  
   - **Minimum SDK:** API 31  
   - **Build configuration language:** Kotlin DSL  
4. Klik **Finish** dan tunggu sampai selesai.

---

### 🔹 Langkah 2: Ganti Folder `main`

1. Buka folder proyek di komputer Anda.
2. Di Android Studio, ubah tampilan **Project** dari "Android" menjadi "Project".
3. Arahkan ke `LetMeCook > app > src/` dan hapus folder `main` yang ada.
4. Salin folder `main` dari repositori LetMeCook ke dalam `app/src/`.

---

### 🔹 Langkah 3: Konfigurasi `build.gradle`

1. Buka file `app/build.gradle` dan ubah isinya menjadi seperti berikut:

```kts
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.letmecook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.letmecook"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY")}\"")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // Lottie
    implementation(libs.lottie)

    // Retrofit & Gson Converter (untuk networking)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Glide (untuk memuat gambar dari URL)
    implementation(libs.glide)
    implementation(libs.firebase.crashlytics.buildtools)
    annotationProcessor(libs.compiler)

    // Google AI (Gemini)
    implementation(libs.generativeai)

    // Coroutines untuk handle asynchronous call
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.guava)

    implementation(libs.gson)

    // Markwon untuk merender Markdown di TextView
    implementation(libs.core)

    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Material Design
    implementation(libs.material)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
```

### 🔹 Langkah 4: Siapkan Kunci API Gemini

1. Buka file `local.properties` yang ada di root, lalu tambahkan baris berikut (ganti `MASUKKAN_API_KEY_ANDA_DI_SINI` dengan kunci API Gemini Anda yang sebenarnya):

```properties
GEMINI_API_KEY="MASUKKAN_API_KEY_ANDA_DI_SINI"
```

### 🔹 Langkah 5: Sinkronisasi dan Jalankan

1. Setelah selesai mengonfigurasi file `build.gradle` dan `local.properties`, Android Studio akan menampilkan notifikasi **"Sync Now"** di bagian atas editor.  
   Klik tombol **"Sync Now"** untuk memulai proses sinkronisasi Gradle.

2. Tunggu hingga proses sinkronisasi selesai tanpa error.

3. Jalankan aplikasi dengan salah satu cara berikut:
   - Klik tombol **Run 'app'** (ikon ▶️ berwarna hijau) di toolbar atas.
   - Atau tekan **Shift + F10** (di Windows) atau **Control + R** (di macOS).

4. Pilih emulator Android atau perangkat fisik Anda untuk menjalankan aplikasi.

---

✅ Jika semua langkah berhasil, aplikasi **LetMeCook** akan tampil dan siap digunakan di perangkat Anda!
