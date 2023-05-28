package uz.siyovush.learnlanguagebyreading.util

import android.content.Context
import android.content.SharedPreferences

const val DB = "db"

class PrefManager {
    companion object {
        private fun getInstance(context: Context): SharedPreferences {
            return context.getSharedPreferences(DB, Context.MODE_PRIVATE)
        }

        fun getPage(context: Context, bookId: Int): Int {
            return getInstance(context).getInt(bookId.toString(), 1)
        }

        fun setPage(context: Context, bookId: Int, page: Int) {
            getInstance(context).edit().putInt(bookId.toString(), page).apply()
        }
    }
}