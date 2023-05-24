package uz.siyovush.learnlanguagebyreading.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.siyovush.learnlanguagebyreading.data.database.entity.WordTranslation

@Dao
interface WordDao {
    @Insert
    suspend fun insert(translation: WordTranslation)

    @Query("DELETE FROM WordTranslation WHERE original = :word")
    suspend fun delete(word: String)

    @Query("SELECT EXISTS(SELECT 1 FROM WordTranslation WHERE original = :word LIMIT 1)")
    suspend fun isFavorite(word: String): Boolean

    @Query("SELECT * FROM WordTranslation")
    fun getAll(): Flow<List<WordTranslation>>
}