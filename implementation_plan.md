# Implementation Plan: Database Lokal, Autentikasi & Update UI (Montrace)

Berdasarkan desain Figma yang baru saja Anda berikan, rencana implementasi ini telah saya perbarui agar mencakup seluruh fungsionalitas dan desain antarmuka pengguna (UI) sesuai dengan kebutuhan proyek UAS Anda.

Kita akan menggunakan **Android Room (SQLite)** untuk database lokal dan menambahkan sistem **Autentikasi (Signup & Login)**.

## Arsitektur & Skema Database (Room Entities)

1.  **Table: `users`**
    - `id` (Integer, Primary Key, Auto-generate)
    - `name` (String) _(Sesuai form Full Name di Sign Up)_
    - `email` (String, Unique)
    - `password` (String)

2.  **Table: `transactions`**
    - `id` (Integer, Primary Key, Auto-generate)
    - `user_id` (Integer, Foreign Key ke `users.id`)
    - `type` (String: `"INCOME"` atau `"EXPENSE"`)
    - `category` (String) _(Sesuai pilihan kategori Figma: Food, Salary, dll)_
    - `amount` (Double/Float)
    - `source` (String) _(Contoh: Cash, E-Wallet)_
    - `date` (Long/Timestamp) _(Untuk mengelompokkan transaksi per tanggal)_

## Alur Navigasi Aplikasi

**`SplashActivity`** ➡️ **`OnboardingActivity`** ➡️ **`LoginActivity`** ➡️ **`MainActivity`** (jika login sukses).

## Pembagian Tugas (Group 5)

Proyek ini dibagi menjadi 5 tugas utama agar bisa dikerjakan secara paralel oleh setiap anggota kelompok:

1. **Torikh Abdullah Naser** 👨‍💻 _(Selesai)_
   - **Tugas:** Setup Room Database & DAO (Fase 1).
   - _Fokus:_ Membuat fondasi database agar bisa dipakai oleh anggota lain, serta penyesuaian kategori pengeluaran (Shopping).

2. **Rapolo Joshua** 🔐 _(Sedang Dikerjakan)_
   - **Tugas:** Implementasi Authentication Screen (Fase 2).
   - **Fokus:** Desain XML Sign Up & Log In, logika autentikasi, dan _Session Management_ (SharedPreferences).
3. **Rafi Fauzi Alfariz** 📱 _(Selesai)_
   - **Tugas:** Update Home Screen (Fase 3).
   - **Fokus:** _Header_ dinamis, _Toggle_ Income/Expense, _Popup_ Kategori, Form Input, dan List _Recent Transaction_ (dibatasi 3 item).
4. **Fani Dwi Ariyanti** 🧾 _(Selesai)_
   - **Tugas:** Update Transaction & Detail Screen (Fase 4).
   - **Fokus:** Menampilkan semua transaksi yang dikelompokkan per tanggal, menu Edit & Delete.
5. **Damar Kusumawardhani** 📊 _(Selesai)_
   - **Tugas:** Update Overview & About Screen (Fase 5 & 6).
   - _Fokus:_ Kalkulasi _Progress Bar_ dinamis & Filter Bulan di Overview (Selesai), serta profil Group 5 di About Screen (Selesai).

## Langkah-Langkah Implementasi Berdasarkan Figma

### Fase 1: Setup Room Database & DAO

1. Konfigurasi Entity `User` dan `Transaction`.
2. Buat `UserDao` (untuk login/signup) dan `TransactionDao` (untuk operasi CRUD transaksi).
3. Inisialisasi `AppDatabase`.

### Fase 2: Implementasi Authentication Screen (Sesuai Frame 117)

1.  **Sign Up Screen (`activity_signup.xml` & `SignupActivity.kt`):**
    - Desain: Menampilkan logo Montrace, teks "Sign Up", input fields (Full Name, Email, Password), tombol "Sign Up", dan tautan "Log In".
    - Logika: Menyimpan akun ke database Room.
2.  **Log In Screen (`activity_login.xml` & `LoginActivity.kt`):**
    - Desain: Menampilkan logo, teks "Log In", input fields (Email, Password), tombol "Log In", tautan "Sign Up".
    - Logika: Validasi _credentials_, simpan status login menggunakan `SharedPreferences`, lalu navigasi ke `MainActivity`.

### Fase 3: Update Home Screen (Sesuai Frame 113)

1.  **Update `fragment_home.xml`:**
    - Perbarui _Header_ (menampilkan Total Amount dan Tanggal saat ini).
    - Implementasi _Toggle Switch_ (INCOME / EXPENSES) untuk _form_ pengisian.
    - Implementasi Form Input: _Category_ (menggunakan _BottomSheetDialog_ / _Dialog_ dengan grid _icon_ kategori), _Amount_, _Source_, dan tombol _Save_.
    - Tampilkan daftar "Recent Transaction" maksimal beberapa _item_ terakhir (membaca dari database).
2.  **Update Logika `HomeFragment.kt`:**
    - Hubungkan tombol _Save_ untuk memasukkan (INSERT) data ke Room `TransactionDao`.

### Fase 4: Update Transaction & Detail Screen (Sesuai Frame 114)

1.  **Transaction Screen (`fragment_transaction.xml`):**
    - Baca semua data dari database dan urutkan berdasarkan `date`.
    - Kelompokkan tampilan _list_ per tanggal (misal: "22 April 2026", "21 April 2026").
    - Tambahkan ikon _3-dots menu_ di setiap _item_ transaksi yang memunculkan opsi **Edit Transaction** dan **Delete Transaction**.
2.  **Transaction Detail Screen (`TransactionDetailActivity.kt`):**
    - Menampilkan detail (Category, Source, Amount berwarna merah/hijau).
    - Implementasikan fungsionalitas Edit/Delete yang akan meng-update tabel database.

### Fase 5: Update Overview Screen (Sesuai Frame 115)

1.  **Overview Screen (`fragment_overview.xml`):**
    - Desain ulang sesuai Figma: Menampilkan Total Amount keseluruhan.
    - Menampilkan _Summary Box_ dengan Total Income & Total Expenses.
    - **Progress Bars:** Hitung persentase pengeluaran/pemasukan per kategori (dari database Room) dan tampilkan grafiknya (contoh: Food 87.50%, Salary 44.44%).

### Fase 6: Update About Screen (Sesuai About Screen Frame)

1.  **Update `fragment_about.xml` & `AboutFragment.kt`:**
    - Ubah data statis agar menampilkan identitas anggota **Group 5**:
      - Rapolo Joshua Napitupulu (2410512001)
      - Rafi Fauzi Alfariz (2410512015)
      - Torikh Abdullah Naser (2410512031)
      - Fani Dwi Ariyanti (2410512053)
      - Damar Kusumawardhani (2410512069)
    - Tambahkan tombol/ikon Instagram dan LinkedIn untuk setiap anggota kelompok.

## Verification Plan

1. **Navigasi Login/Signup:** Memastikan _user_ baru bisa mendaftar, lalu _login_, dan sesi tersimpan.
2. **Input Transaksi:** Memastikan saat "Save" ditekan di Home Screen, data langsung muncul di "Recent Transaction" dan tab "Transaction".
3. **Kalkulasi Overview:** Memastikan persentase _progress bar_ dihitung tepat secara otomatis dari database.
4. **Validasi Desain:** Mengecek warna, _border radius_, tipe _font_, dan ikon agar 95% mendekati referensi Figma.
