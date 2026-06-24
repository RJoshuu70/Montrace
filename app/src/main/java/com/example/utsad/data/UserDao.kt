package com.example.utsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // Menyisipkan user baru. Menggunakan IGNORE agar jika email duplikat (melanggar unique index),
    // Room mengembalikan -1 (bisa dideteksi untuk memberi tahu user bahwa email sudah terdaftar).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    // Pengecekan login dan validasi signup memanfaatkan index "email" untuk pencarian O(1) yang super cepat.
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}
