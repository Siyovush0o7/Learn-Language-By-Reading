package uz.siyovush.learnlanguagebyreading.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import uz.siyovush.learnlanguagebyreading.data.model.Book

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: BookEntity)

    @Query("SELECT * FROM BookEntity")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("DELETE FROM BookEntity WHERE id = :bookId")
    fun deleteById(bookId: Long)
}
