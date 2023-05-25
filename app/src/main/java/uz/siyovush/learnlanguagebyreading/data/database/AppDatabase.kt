package uz.siyovush.learnlanguagebyreading.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import uz.siyovush.learnlanguagebyreading.data.database.dao.WordDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import uz.siyovush.learnlanguagebyreading.data.database.entity.WordTranslation

@Database(entities = [WordTranslation::class, BookEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getWordDao(): WordDao
    abstract fun getBookDao(): BookDao
}