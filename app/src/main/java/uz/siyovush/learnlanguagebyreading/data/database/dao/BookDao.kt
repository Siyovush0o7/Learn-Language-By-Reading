package uz.siyovush.learnlanguagebyreading.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: BookEntity)

    @Query("SELECT * FROM BookEntity ORDER BY CASE WHEN :asc THEN title END ASC, CASE WHEN NOT :asc THEN title END DESC")
    fun getAllBooksByName(asc: Boolean): Flow<List<BookEntity>>

    @Query("DELETE FROM BookEntity WHERE id = :bookId")
    fun deleteById(bookId: Long)
}
