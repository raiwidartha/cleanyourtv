# CleanYourTV (Aurora Edition)

> Launcher kustom dan debloat performa tinggi tanpa iklan untuk Android TV, dikustomisasi khusus untuk **Xiaomi Mi TV A2 55\"** (`MiTV-MOOQ1`, Android TV 10).

## Fitur Utama

- **0% Idle Overhead**: Dibangun dengan Java native murni (~390 KB) tanpa daemon latar belakang konstan.
- **5-Kolom Dock Aurora**: Desain dock minimalis berjejer rapi dengan navigasi D-pad presisi.
- **Rotasi Wallpaper Dinamis 1080p**: Otomatis merotasi wallpaper dari `/sdcard/Wallpapers` dengan interval waktu yang dapat diatur (1 menit s/d 1 jam) dan toggle pemati gradien gelap `SkyView`.
- **Koleksi Wallpaper 1080p Lanczos**: Dilengkapi 30 koleksi wallpaper (14 The Legend of Zelda Tears of the Kingdom / Breath of the Wild & 16 Dota 2 Arcana resmi Valve).
- **Kalkulator Rerahinan Hindu Bali Offline**: Penghitung kalender Pawukon 210 hari dan tabel Sasih/Saka untuk mendeteksi rerahinan terdekat (Purnama, Tilem, Kajeng Keliwon, Saraswati, Pagerwesi, Nyepi, dsb) tanpa koneksi internet.
- **One-Key RAM Booster Xiaomi**: Tombol cepat roket di top bar yang terhubung langsung ke pembersih memori bawaan TV Manager (`ProcessManagerActivity`).
- **Indikator Cuaca Ringan**: Menampilkan temperatur dan kondisi cuaca pasif dengan cache 1 jam (hemat daya).
- **Menu Input Hardware Terintegrasi**: Akses langsung ke TV Antena (Tuner), HDMI 1-3, dan AV.
- **Screensaver Jam Drifting**: Screensaver bawaan `ClockDream` untuk mencegah burn-in pada layar TV.

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

## Author & Kontributor

- **I Nyoman Rai Widartha Kesuma, S.Kom., M.Kom.**
  - D4 Teknologi Rekayasa Perangkat Lunak (TRPL), Politeknik Negeri Bali (PNB)
  - GitHub: [@raiwidartha](https://github.com/raiwidartha)
  - Email: widarthakesuma@gmail.com

---

## Lisensi

Proyek ini dirilis di bawah lisensi [MIT](LICENSE). Font Outfit di bawah lisensi SIL Open Font License.
