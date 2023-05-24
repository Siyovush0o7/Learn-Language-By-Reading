package uz.siyovush.learnlanguagebyreading.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.siyovush.learnlanguagebyreading.data.database.dao.DictDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.Translation
import uz.siyovush.learnlanguagebyreading.data.database.entity.Word

@Database(entities = [Word::class, Translation::class], version = 1)
abstract class DictDatabase: RoomDatabase() {
    abstract fun dictDao(): DictDao
}