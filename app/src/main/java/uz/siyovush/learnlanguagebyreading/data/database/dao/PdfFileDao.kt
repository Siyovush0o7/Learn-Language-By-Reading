package uz.siyovush.learnlanguagebyreading.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import uz.siyovush.learnlanguagebyreading.data.database.entity.PdfFileEntity

@Dao
interface PdfFileDao {
    @Insert
    suspend fun insertPdfFile(pdfFile: PdfFileEntity)

    @Update
    suspend fun updatePdfFile(pdfFile: PdfFileEntity)

    @Delete
    suspend fun deletePdfFile(pdfFile: PdfFileEntity)

    @Query("SELECT * FROM pdf_files")
    suspend fun getAllPdfFiles(): List<PdfFileEntity>

    // Add more query methods as needed
    @Query("Select * from pdf_files where fileName == :filePath")
    suspend fun getPdfById(filePath: String): PdfFileEntity
}