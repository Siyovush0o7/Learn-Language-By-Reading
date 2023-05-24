package uz.siyovush.learnlanguagebyreading.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "word",
    indices = [
        Index(name = "IX_002", unique = false, value = ["id"], orders = [Index.Order.ASC]),
        Index(name = "IX_001", unique = false, value = ["name", "langId"], orders = [Index.Order.ASC, Index.Order.ASC]),
    ]
)
data class Word(
    @PrimaryKey val id: Int,
    val name: String,
    val langId: Int,
    @ColumnInfo(defaultValue = "") val transcription: String,
)
