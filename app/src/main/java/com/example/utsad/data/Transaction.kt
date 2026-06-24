package com.example.utsad.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE // Jika user dihapus, semua transaksinya ikut terhapus
        )
    ],
    indices = [
        // Index pada user_id untuk mempercepat pencarian semua transaksi milik user tertentu.
        Index(value = ["user_id"]),
        // Index pada date untuk mempercepat query pengurutan/grouping transaksi per tanggal.
        Index(value = ["date"])
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val user_id: Int,
    val type: String, // "INCOME" atau "EXPENSE"
    val category: String, // e.g., "Food", "Salary"
    val amount: Double,
    val source: String, // e.g., "Cash", "E-Wallet"
    val date: Long // Menyimpan tanggal dalam bentuk timestamp epoch millisecond
)
