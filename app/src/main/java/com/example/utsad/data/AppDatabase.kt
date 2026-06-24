package com.example.utsad.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        // Volatile memastikan bahwa variabel INSTANCE akan selalu ter-update secara atomik
        // dan terlihat oleh semua thread (mencegah kondisi race-condition).
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Jika INSTANCE tidak null, kembalikan. Jika null, inisialisasi database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "montrace_database"
                )
                // FallbackToDestructiveMigration artinya jika kita mengubah skema (misal tambah kolom)
                // dan versinya naik, data lama akan dihapus dan tabel dibuat ulang.
                // Untuk produksi nyata, sebaiknya gunakan Migration Strategy.
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
