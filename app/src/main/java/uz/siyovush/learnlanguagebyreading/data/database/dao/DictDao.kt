package uz.siyovush.learnlanguagebyreading.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import uz.siyovush.learnlanguagebyreading.data.database.entity.Dict

@Dao
interface DictDao {
    @Query(
        """
        SELECT
          word.name AS word,
          word.transcription as transcription,
          GROUP_CONCAT(word2.name) AS translation
        FROM
          word
        JOIN
          translation
        ON
          word.id = translation.idWord
        JOIN
          word word2
        ON
          word2.id = translation.idTranslation
        WHERE
          word.langId = :langId
        GROUP BY
          word.name;
    """
    )
    suspend fun getTranslations(langId: Int): List<Dict>
}