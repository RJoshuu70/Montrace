package com.example.utsad.data

import android.content.Context
import android.content.SharedPreferences

/**
 * SessionManager — pembungkus SharedPreferences untuk menyimpan status login user.
 *
 * Kenapa SharedPreferences (bukan Room) untuk session?
 * Session bersifat "device-local & sementara" (siapa yang SEDANG login di HP ini),
 * berbeda dengan data User di Room yang bersifat "permanen" (akun terdaftar).
 * SharedPreferences jauh lebih ringan untuk key-value sederhana seperti ini
 * dibanding membuka koneksi database hanya untuk membaca status login.
 *
 * MODE_PRIVATE memastikan file preferensi ini hanya bisa diakses oleh aplikasi kita sendiri.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Dipanggil setelah Login/Signup sukses.
     * Menyimpan identitas user yang sedang aktif agar bisa dibaca oleh
     * HomeFragment, TransactionFragment, dan OverviewFragment (menggantikan dummy user_id).
     */
    fun saveSession(userId: Int, name: String, email: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply() // apply() = async write, tidak blocking main thread (vs commit() yang synchronous)
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    /**
     * -1 dipakai sebagai sentinel value "belum ada user login".
     * Lebih aman daripada nullable Int karena DAO query kita (getTransactionsByUser dst.)
     * menerima parameter Int non-null.
     */
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUserName(): String = prefs.getString(KEY_NAME, "") ?: ""

    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    /** Dipanggil saat logout: bersihkan seluruh data sesi. */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "montrace_session"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_NAME = "key_name"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }
}
