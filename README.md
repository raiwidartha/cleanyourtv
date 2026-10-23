# Xiaomi Mi TV Debloat & Clean Launcher Suite

> Paket terpadu otomasi debloat, pemulihan sistem, dan launcher performa tinggi tanpa iklan untuk Android TV, dikustomisasi khusus untuk **Xiaomi Mi TV A2 55\"** (`MiTV-MOOQ1`, Android TV 10).

## Fitur Utama

- **0% Idle Overhead**: Dibangun dengan Java native murni (~390 KB) tanpa daemon latar belakang konstan, hemat RAM & responsif.
- **Deteksi Luminansi & Keterbacaan Adaptif**: Otomatis menganalisis kecerahan latar belakang wallpaper (algoritma BT.709) dan mengaktifkan lapisan *vignette* halus serta penyesuaian kontras tipografi secara cerdas saat wallpaper cerah dimuat.
- **5-Kolom Dock Aurora**: Desain dock minimalis berjejer rapi 5 kartu sejajar dengan navigasi D-pad TV presisi.
- **Kontrol Opasitas Latar Dock**: Tingkat transparansi latar belakang dock dapat diatur langsung di Setelan Launcher (100% Solid, 75% Tegas, 50% Standar, 25% Tipis, 0% Transparan Penuh).
- **Rotasi Wallpaper Dinamis 1080p**: Otomatis merotasi wallpaper dari `/sdcard/Wallpapers` dengan interval waktu yang dapat diatur (1 menit s/d 1 jam) dan toggle pemati gradien gelap `SkyView`.
- **Koleksi Wallpaper 1080p Lanczos**: Dilengkapi 30 koleksi wallpaper (14 The Legend of Zelda Tears of the Kingdom & 16 Dota 2 Arcana resmi Valve).
- **Kalkulator Rerahinan Hindu Bali Offline**:
  - Algoritma Pawukon 210 hari siklus Wuku dan kalender Saka/Sasih untuk mendeteksi rerahinan (Purnama, Tilem, Kajeng Keliwon, Saraswati, Pagerwesi, Nyepi, Siwaratri, dsb) 100% luring tanpa koneksi internet.
  - Dapat diaktifkan/dinonaktifkan (On/Off) serta diatur batas tampilan event terdekat (1 hingga 4 event) di menu setelan launcher.
- **One-Key RAM Booster Xiaomi**: Tombol cepat roket di top bar yang terhubung langsung ke pembersih memori bawaan TV Manager (`com.xiaomi.mitv.tvmanager/.boost.ProcessManagerActivity`).
- **Indikator Cuaca Ringan**: Menampilkan temperatur dan kondisi cuaca pasif dengan cache 1 jam via Open-Meteo API.
- **Menu Input Hardware Terintegrasi**: Akses langsung ke TV Antena (Tuner DVB-T2 `HW0`), HDMI 1-3, dan AV dengan status sinyal live.
- **Screensaver Jam Drifting**: Screensaver bawaan `ClockDream` untuk mencegah burn-in pada layar TV.

---

## Debloat Suite (`debloat/`)

Repositori ini menyatukan skrip debloat, data pencadangan sistem (backup), dan skrip pemulihan (restore) untuk Xiaomi Mi TV A2 55":

- **32 Paket Bloatware Dinonaktifkan**:
  - Telemetri & analitik: `com.miui.tv.analytics`, `com.xiaomi.statistic`, `com.google.android.feedback`, `com.google.android.tv.bugreportsender`.
  - Iklan PatchWall: `com.mitv.tvhome.atv`, `com.mitv.tvhome.michannel`, `com.xm.webcontent`, `com.xiaomi.floatingframe`, `com.xiaomo.tv.milegal`.
  - Media & tools bawaan yang boros RAM: `com.mitv.videoplayer`, `com.xiaomi.mimusic2`, `com.mitv.gallery`, `com.mitv.dream`, `com.mitv.tvlock`, `com.xiaomi.mitv.smartshare`, `com.mitv.milinkservice`, `com.duokan.factorytest`, `mediatek.factorymenu.ui`.
  - Layanan streaming bawaan: `com.google.android.katniss`, `com.amazon.amazonvideo.livingroom`, `com.google.android.youtube.tv`, `com.google.android.youtube.tvmusic`, `com.google.android.videos`, `com.google.android.play.games`.
- **Skrip Pemulihan Cepat**:
  - `debloat/restore.bat` (Windows) & `debloat/restore.sh` (Linux/macOS) untuk mengaktifkan kembali semua paket sistem bila diperlukan.
- **Pencadangan Penuh (`debloat/backup/`)**:
  - Daftar paket lengkap, system properties (`getprop`), dumpsys package, dan konfigurasi settings global/secure/system.

---

## Profil Perangkat

| Spesifikasi | Nilai |
|---|---|
| **Model Perangkat** | Xiaomi Mi TV A2 55\" (`MiTV-MOOQ1`) |
| **Sistem Operasi** | Android TV 10 |
| **SoC / Chipset** | MediaTek Quad Core (Cortex-A55) / Mali G52 MP2 |
| **RAM / Penyimpanan** | 2.0 GB RAM / 16 GB Internal Storage |
| **Default Launcher** | `com.gathin.tvhome/.MainActivity` |

---

## Kompilasi & Pemasangan

### Kebutuhan
- JDK 17 atau lebih baru (misal: JDK 19)
- Android SDK Platform 34 & Build-Tools 34.0.0
- Git & ADB

### Build Release APK
```bash
cd launcher
export JAVA_HOME="C:/Program Files/Java/jdk-19" # Sesuaikan path JDK
export ANDROID_HOME="D:/Dev/Android"            # Sesuaikan path Android SDK
./gradlew assembleRelease --console=plain
```

Berkas APK akan dihasilkan di:
`launcher/app/build/outputs/apk/release/app-release.apk`

### Pasang ke TV via ADB
```bash
adb install -r launcher/app/build/outputs/apk/release/app-release.apk
adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
```

---

## Lisensi

Proyek ini dirilis di bawah lisensi [MIT](LICENSE). Font Outfit di bawah lisensi SIL Open Font License.
