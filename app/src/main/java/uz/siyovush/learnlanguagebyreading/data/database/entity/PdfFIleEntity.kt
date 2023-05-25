package uz.siyovush.learnlanguagebyreading.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_files")
data class PdfFileEntity(
    val fileName: String,
    val fileData: ByteArray,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)