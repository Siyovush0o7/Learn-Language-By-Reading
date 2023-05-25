package uz.siyovush.learnlanguagebyreading.util


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File


fun extractData(context: Context, filePath: String, page: Int): String {
    val reader =
        PdfReader("/data/user/0/uz.suhrob.learnlanguagebyreading/files/NDASHERALIYEVISLOM.pdf")
    val text = PdfTextExtractor.getTextFromPage(reader, page)
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    reader.close()
    return text
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