package com.example.utsad.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // Menggunakan Flow agar UI selalu ter-update secara real-time saat ada perubahan data di tabel.
    // Query ini memanfaatkan index pada user_id dan akan diurutkan berdasarkan tanggal terbaru.
    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC")
    fun getTransactionsByUser(userId: Int): Flow<List<Transaction>>

    // Tambahan fungsi agregat untuk performa halaman Overview (Fase 5) 
    // agar kita tidak perlu melooping seluruh data di memori aplikasi (mengurangi N+1 atau heavy logic di UI).
    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'INCOME'")
    fun getTotalIncomeByUser(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE'")
    fun getTotalExpenseByUser(userId: Int): Flow<Double?>
}
