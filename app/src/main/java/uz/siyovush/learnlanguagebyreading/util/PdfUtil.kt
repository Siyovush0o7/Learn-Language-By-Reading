package uz.siyovush.learnlanguagebyreading.util


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun extractData(context: Context, uri: Uri, page: Int): String {
    var text = ""
    val inputStream = context.contentResolver.openInputStream(uri)
    inputStream?.use { stream ->
        val reader = PdfReader(stream)
        text = PdfTextExtractor.getTextFromPage(reader, page)
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        reader.close()
    }
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