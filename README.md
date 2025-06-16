# 🍳 LetMeCook - Aplikasi Resep Android

**LetMeCook** adalah aplikasi resep masakan berbasis Android yang dirancang untuk membantu pengguna menemukan, mengelola, dan mendapatkan inspirasi resep. Aplikasi ini dilengkapi dengan fitur cerdas seperti rekomendasi resep harian, manajemen inventaris bahan, dan asisten AI yang didukung oleh Google Gemini untuk memberikan rekomendasi resep secara real-time.

---

## ✨ Fitur Utama

- **Daftar Resep Lengkap**  
  Menampilkan ratusan resep dari database lokal yang dimuat dari file CSV.

- **Rekomendasi Harian**  
  Halaman utama menampilkan kartu resep yang direkomendasikan secara acak setiap hari.

- **Chat dengan AI Gemini**  
  Pengguna dapat berinteraksi dengan asisten AI untuk meminta rekomendasi resep berdasarkan bahan atau nama masakan. Respons dari AI berupa kartu resep yang interaktif.

- **Manajemen Inventaris**  
  Fitur untuk mencatat bahan-bahan yang dimiliki pengguna di rumah. Pengguna dapat menambah, mengubah, dan menghapus item inventaris.

- **Status Ketersediaan Bahan**  
  Setiap resep secara otomatis menampilkan status ketersediaan bahan ("Tersedia", "Kurang Bahan", "Tidak Ada Bahan") berdasarkan data inventaris pengguna.

- **Resep Favorit**  
  Pengguna dapat menandai resep sebagai favorit untuk akses cepat.

---

## 🛠 Teknologi dan Arsitektur

- **Bahasa:** Java  
- **Arsitektur:** MVVM (Model-View-ViewModel)  
- **UI:** Material Design 3, View Binding, RecyclerView  
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
   - **Build configuration language:** Groovy DSL  
4. Klik **Finish** dan tunggu sampai selesai.

---

### 🔹 Langkah 2: Ganti Folder `main`

1. Buka folder proyek di komputer Anda.
2. Di Android Studio, ubah tampilan **Project** dari "Android" menjadi "Project".
3. Arahkan ke `LetMeCook > app > src/` dan hapus folder `main` yang ada.
4. Salin folder `main` dari repositori LetMeCook ke dalam `app/src/`.

---

### 🔹 Langkah 3: Konfigurasi `build.gradle`

1. Buka file `app/build.gradle` dan tambahkan konfigurasi berikut:

```groovy
android {
    compileSdk 34

    defaultConfig {

        def properties = new Properties()
        if (rootProject.file("local.properties").exists()) {
            properties.load(rootProject.file("local.properties").newDataInputStream())
        }
        buildConfigField "String", "GEMINI_API_KEY", "\"${properties.getProperty("GEMINI_API_KEY", "")}\""
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    buildFeatures {
        viewBinding true
        buildConfig true
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // Navigation Component
    implementation 'androidx.navigation:navigation-fragment:2.7.7'
    implementation 'androidx.navigation.ui:2.7.7'

    // ViewModel and LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-common-java8:2.7.0'

    // Room (SQLite)
    def room_version = "2.6.1"
    implementation "androidx.room:room-runtime:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"

    // Retrofit (for API calls)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // Glide (for image loading)
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'

    // CSV Reader
    implementation 'com.opencsv:opencsv:5.7.1'

    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
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
