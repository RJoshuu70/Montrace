# Implementation Plan: Database Lokal, Autentikasi & Update UI (Montrace)

Berdasarkan desain Figma yang baru saja Anda berikan, rencana implementasi ini telah saya perbarui agar mencakup seluruh fungsionalitas dan desain antarmuka pengguna (UI) sesuai dengan kebutuhan proyek UAS Anda.

Kita akan menggunakan **Android Room (SQLite)** untuk database lokal dan menambahkan sistem **Autentikasi (Signup & Login)**.

## Arsitektur & Skema Database (Room Entities)

1.  **Table: `users`**
    - `id` (Integer, Primary Key, Auto-generate)
    - `name` (String) *(Sesuai form Full Name di Sign Up)*
    - `email` (String, Unique)
    - `password` (String)

2.  **Table: `transactions`**
    - `id` (Integer, Primary Key, Auto-generate)
    - `user_id` (Integer, Foreign Key ke `users.id`)
    - `type` (String: `"INCOME"` atau `"EXPENSE"`)
    - `category` (String) *(Sesuai pilihan kategori Figma: Food, Salary, dll)*
    - `amount` (Double/Float)
    - `source` (String) *(Contoh: Cash, E-Wallet)*
    - `date` (Long/Timestamp) *(Untuk mengelompokkan transaksi per tanggal)*

## Alur Navigasi Aplikasi
**`SplashActivity`** ➡️ **`OnboardingActivity`** ➡️ **`LoginActivity`** ➡️ **`MainActivity`** (jika login sukses).

## Pembagian Tugas (Group 5)

Proyek ini dibagi menjadi 5 tugas utama agar bisa dikerjakan secara paralel oleh setiap anggota kelompok:

1. **Torikh Abdullah Naser** 👨‍💻 *(Selesai)*
   - **Tugas:** Setup Room Database & DAO (Fase 1).
   - *Fokus:* Membuat fondasi database agar bisa dipakai oleh anggota lain.

2. **Rapolo Joshua** 🔐 *(Sedang Dikerjakan)*
   - **Tugas:** Implementasi Authentication Screen (Fase 2).
   - **Fokus:** Desain XML Sign Up & Log In, logika autentikasi, dan *Session Management* (SharedPreferences).
3. **Rafi Fauzi Alfariz**
   - **Tugas:** Update Home Screen (Fase 3).
   - **Fokus:** *Header* dinamis, *Toggle* Income/Expense, *Popup* Kategori, Form Input, dan List *Recent Transaction*.
4. **Fani Dwi Ariyanti**
   - **Tugas:** Update Transaction & Detail Screen (Fase 4).
   - **Fokus:** Menampilkan semua transaksi yang dikelompokkan per tanggal, menu Edit & Delete.
5. **Damar Kusumawardhani**
   - **Tugas:** Update Overview & About Screen (Fase 5 & 6).
   - **Fokus:** Kalkulasi *Progress Bar* pemasukan/pengeluaran dan *update* profil statis Group 5 di About Screen.

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
    - Logika: Validasi *credentials*, simpan status login menggunakan `SharedPreferences`, lalu navigasi ke `MainActivity`.

### Fase 3: Update Home Screen (Sesuai Frame 113)
1.  **Update `fragment_home.xml`:**
    - Perbarui *Header* (menampilkan Total Amount dan Tanggal saat ini).
    - Implementasi *Toggle Switch* (INCOME / EXPENSES) untuk *form* pengisian.
    - Implementasi Form Input: *Category* (menggunakan *BottomSheetDialog* / *Dialog* dengan grid *icon* kategori), *Amount*, *Source*, dan tombol *Save*.
    - Tampilkan daftar "Recent Transaction" maksimal beberapa *item* terakhir (membaca dari database).
2.  **Update Logika `HomeFragment.kt`:**
    - Hubungkan tombol *Save* untuk memasukkan (INSERT) data ke Room `TransactionDao`.

### Fase 4: Update Transaction & Detail Screen (Sesuai Frame 114)
1.  **Transaction Screen (`fragment_transaction.xml`):**
    - Baca semua data dari database dan urutkan berdasarkan `date`.
    - Kelompokkan tampilan *list* per tanggal (misal: "22 April 2026", "21 April 2026").
    - Tambahkan ikon *3-dots menu* di setiap *item* transaksi yang memunculkan opsi **Edit Transaction** dan **Delete Transaction**.
2.  **Transaction Detail Screen (`TransactionDetailActivity.kt`):**
    - Menampilkan detail (Category, Source, Amount berwarna merah/hijau).
    - Implementasikan fungsionalitas Edit/Delete yang akan meng-update tabel database.

### Fase 5: Update Overview Screen (Sesuai Frame 115)
1.  **Overview Screen (`fragment_overview.xml`):**
    - Desain ulang sesuai Figma: Menampilkan Total Amount keseluruhan.
    - Menampilkan *Summary Box* dengan Total Income & Total Expenses.
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
1. **Navigasi Login/Signup:** Memastikan *user* baru bisa mendaftar, lalu *login*, dan sesi tersimpan.
2. **Input Transaksi:** Memastikan saat "Save" ditekan di Home Screen, data langsung muncul di "Recent Transaction" dan tab "Transaction".
3. **Kalkulasi Overview:** Memastikan persentase *progress bar* dihitung tepat secara otomatis dari database.
4. **Validasi Desain:** Mengecek warna, *border radius*, tipe *font*, dan ikon agar 95% mendekati referensi Figma.
