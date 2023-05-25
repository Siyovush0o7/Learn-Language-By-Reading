package uz.siyovush.learnlanguagebyreading.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.siyovush.learnlanguagebyreading.data.database.dao.PdfFileDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.PdfFileEntity

@Database(entities = [PdfFileEntity::class], version = 1)
abstract class PdfDatabase : RoomDatabase() {
    abstract fun pdfFileDao(): PdfFileDao

    companion object {
        private const val DATABASE_NAME = "pdf_files.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .build()
        }
    }
}