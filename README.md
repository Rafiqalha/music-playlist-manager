# 🎵 Luxury Music Player (VisionOS Style)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-Modern_UI-4285F4?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

> **"Experience music with depth."**
> Aplikasi pemutar musik desktop modern dengan antarmuka *Glassmorphism* yang terinspirasi oleh Apple Vision Pro, dibangun menggunakan JavaFX dan Struktur Data murni.

---

## ✨ Tampilan Antarmuka (Preview)

<img width="1498" height="937" alt="Screenshot 2025-12-01 082916" src="https://github.com/user-attachments/assets/133535ad-2ea0-4708-99bd-8808bacf9c85" />


---

## 🚀 Fitur Utama

### 🎨 User Interface (Premium Dark & Gold)
- **Elegant Dark Theme:** Menggunakan palet warna *Deep Charcoal* yang solid dengan aksen *Gold* (Emas) yang memberikan kesan mewah dan eksklusif.
- **Modular Grid Layout:** Tata letak berbasis kartu (*Card-based*) yang memisahkan navigasi, pemutar utama, dan antrian lagu dengan rapi dan terstruktur.
- **High Contrast Visual:** Ikon dan tipografi yang kontras memudahkan navigasi dalam mode gelap tanpa membuat mata lelah.
- **Custom Window:** Title bar kustom (Frameless) yang didesain menyatu dengan tema aplikasi, menghilangkan batas jendela Windows standar.
  
### 🎧 Fungsionalitas Player
- **Format Support:** Memutar file MP3 dengan lancar.
- **Smart Navigation:** Next, Previous, Play, Pause.
- **Shuffle Mode:** Mengacak lagu tanpa pengulangan menggunakan algoritma *Fisher-Yates*.
- **Repeat Mode:** Mengulang lagu atau playlist.
- **Volume Control:** Slider volume presisi.

### 📂 Manajemen Playlist
- **Add Music:** Menambahkan file lokal dari komputer.
- **Delete Music:** Menghapus lagu dari daftar.
- **History Log:** Melihat riwayat lagu yang baru saja diputar.
- **Auto-Save:** Daftar lagu tersimpan otomatis (Persistance) menggunakan JSON.

---

## 🧠 Implementasi Struktur Data (Backend Logic)

Proyek ini menerapkan **Struktur Data Murni** (*Manual Implementation*) tanpa bergantung pada Java Collections instan, untuk mendemonstrasikan pemahaman logika algoritma.

| Fitur | Struktur Data | Penjelasan Implementasi |
| :--- | :--- | :--- |
| **Playlist Navigation** | **Doubly Linked List** | Memungkinkan traversal dua arah (*Next/Prev*). Setiap lagu adalah `Node` yang memiliki pointer ke lagu sebelum dan sesudahnya. |
| **Playback History** | **Stack (LIFO)** | Menggunakan prinsip *Last-In First-Out*. Lagu yang terakhir didengar akan muncul paling atas saat pop-up History dibuka. |
| **Library Storage** | **Array of Objects** | Tempat penyimpanan utama data lagu sebelum diproses ke dalam playlist. |
| **Shuffle Feature** | **Fisher-Yates Algorithm** | Mengkonversi Linked List ke Array sementara, lalu mengacak indeksnya secara matematis untuk hasil acak yang sempurna. |
| **Data Persistence** | **JSON Serialization** | Menyimpan struktur data ke file fisik (`json`) agar data tidak hilang saat aplikasi ditutup. |

---

## 🛠️ Teknologi & Library

* **Bahasa:** Java (JDK 21)
* **Framework UI:** JavaFX
* **Icons:** Ikonli (Material Design Pack)
* **Audio Engine:** JLayer (Basic MP3 Support)
* **Data Handling:** GSON (Google JSON)
* **Build Tool:** VS Code Java Extension Pack

---

## 📥 Cara Menjalankan Aplikasi (User)

Tidak perlu instalasi coding yang rumit. Cukup unduh versi *Portable*.

1.  Buka tab **[Releases](https://github.com/Rafiqalha/music-playlist-manager/releases)** di repository ini.
2.  Download file **`VisionMusicPlayer.zip`**.
3.  Ekstrak (Unzip) foldernya.
4.  Klik dua kali file **`run.bat`**.
5.  Selesai! Aplikasi langsung jalan.

*(Catatan: Pastikan komputer sudah terinstall Java/JDK)*

---

## 👨‍💻 Cara Menjalankan Source Code (Developer)

Jika ingin memodifikasi kode:

1.  **Clone** repository ini:
    ```bash
    git clone [https://github.com/Rafiqalha/music-playlist-manager.git](https://github.com/Rafiqalha/music-playlist-manager.git)
    ```
2.  Buka di **VS Code**.
3.  Pastikan library di folder `lib` sudah terdeteksi di *Referenced Libraries*.
4.  Jalankan file `src/Launcher.java`.

---

## 📂 Struktur Folder

```text
VisionMusicPlayer/
├── lib/                 # Berisi library eksternal (.jar)
├── src/                 # Source code utama
│   ├── assets/          # (Sudah dihapus, diganti Vector Icon)
│   ├── MusicPlayerFX.java  # Main Class & UI Logic
│   ├── Playlist.java       # Doubly Linked List Logic
│   ├── SongStack.java      # Stack Logic
│   ├── SongLibrary.java    # Array Logic
│   └── style.css           # Styling VisionOS
├── README.md            # Dokumentasi ini
└── ...
