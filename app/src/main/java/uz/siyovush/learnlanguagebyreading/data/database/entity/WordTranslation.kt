package uz.siyovush.learnlanguagebyreading.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordTranslation(
    @PrimaryKey val original: String,
    val language: String,
    val translation: String,
)