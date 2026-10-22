# Xiaomi Mi TV A2 55" Debloat & CleanYourTV Setup

Dokumentasi modifikasi Xiaomi Mi TV A2 55" (`MiTV-MOOQ1`) Android TV 10.

## 1. Arsitektur Launcher
- Basis: [CleanYourTV](https://github.com/Gathin23/cleanyourtv) (`com.gathin.tvhome`).
- Tema: **Aurora** (latar belakang malam gradien teal-magenta `#070A14` + `wallpaper.jpg`).
- Default Home: `cmd package set-home-activity com.gathin.tvhome/.MainActivity`.

## 2. Fitur Utama Launcher
- **Top Header Pills**:
  - `Input`: Dropdown port hardware (HDMI 1, HDMI 2, HDMI 3, AV, TV Antena).
  - Status koneksi live: Indikator hijau `Tersambung` saat HDMI aktif / perangkat CEC terhubung.
  - `Wi-Fi`: Nama SSID live (`Rai_Home 2_5G`) & status sinyal.
  - `Reorder`: Tombol swap/reorder urutan kartu aplikasi.
  - `Setelan`: Akses instan pengaturan TV dan launcher.
- **Info Bar**:
  - Greeting kontekstual (`SELAMAT PAGI / SIANG / SORE / MALAM`).
  - Jam & Tanggal (`04:44 · RABU 21 OKTOBER`).
  - Hardware Stats: `RAM bebas ... MB · Penyimpanan bebas ... GB`.
- **Dock Apps Utama**:
  - SmartTube (`org.smarttube.stable`)
  - Netflix (`com.netflix.ninja`)
  - TV Browser (`com.internet.tvbrowser`)
  - File Manager + (`com.alphainventor.filemanager`)
  - Xiaomi TV Manager (`com.xiaomi.mitv.tvmanager`)
- **App Drawer**: Tombol pill `Semua Aplikasi` untuk daftar lengkap seluruh aplikasi terpasang.

## 3. Optimasi Hardware & TV Tuner
- **Live TV Antena Direct Routing**:
  - Intent langsung mengarah ke `content://android.media.tv/channel/1` via `com.mediatek.wwtv.tvcenter` (hardware tuner `HW0`). Tidak ada fallback ke HDMI 2.
- **TV Manager Xiaomi (Pembersih RAM)**:
  - Paket `com.xiaomi.mitv.tvmanager` aktif mandiri untuk fitur "Pengoptimalan Memori" (RAM Clean) dan "Pembersih Sampah".
  - Bloatware PatchWall (`com.mitv.tvhome.atv`) tetap dinonaktifkan permanen (`pm disable-user --user 0`) agar tombol remote Mi tidak membajak launcher.

## 4. Paket Dinonaktifkan (Total 32 Paket)
- Telemetry & Analytics: `com.miui.tv.analytics`, `com.xiaomi.statistic`, `com.google.android.feedback`, dll.
- PatchWall & Iklan: `com.mitv.tvhome.atv`, `com.mitv.tvhome.michannel`, `com.xm.webcontent`, `com.xiaomi.floatingframe`, `com.xiaomo.tv.milegal`.
- Media Mi internal: `com.mitv.videoplayer`, `com.xiaomi.mimusic2`, `com.mitv.gallery`, `com.mitv.dream`, dll.
- Layanan tak terpakai: `com.xiaomi.mitv.updateservice`, `com.google.android.katniss`, `com.amazon.amazonvideo.livingroom`, `com.google.android.youtube.tv`.
