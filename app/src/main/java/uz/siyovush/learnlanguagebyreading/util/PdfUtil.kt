package uz.siyovush.learnlanguagebyreading.util


import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File


fun extractData(pdfData: ByteArray, page: Int): String {
    val reader = PdfReader(pdfData)
    val parsedText = PdfTextExtractor.getTextFromPage(reader, page).trim { it > ' ' }
    reader.close()
    return parsedText
}


fun Uri.getFilename(contentResolver: ContentResolver): String? {
    return when (scheme) {
        ContentResolver.SCHEME_CONTENT -> {
            contentResolver.query(this, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.getString(nameIndex).substringBeforeLast('.');
            }
        }

        ContentResolver.SCHEME_FILE -> {
            path?.let { path ->
                File(path).name.substringBeforeLast('.')
            }
        }

        else -> null
    }
}