package com.example.utsad.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        // Index unik pada email agar pengecekan saat login dan signup (mencegah duplikat) berjalan sangat cepat.
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String // Dalam aplikasi production nyata, password HARUS di-hash (misal menggunakan BCrypt). Untuk keperluan tugas UTS/UAS, plain text biasanya cukup, tapi harap diingat.
)
