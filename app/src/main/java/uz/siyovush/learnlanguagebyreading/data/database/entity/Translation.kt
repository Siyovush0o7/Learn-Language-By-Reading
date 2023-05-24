package uz.siyovush.learnlanguagebyreading.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "translation",
    indices = [
        Index(name = "IX_003", unique = false, value = ["idWord"], orders = [Index.Order.ASC]),
    ])
data class Translation(
    @PrimaryKey val id: Int,
    val idWord: Int,
    val idTranslation: Int,
    val idCategory: Int,
)