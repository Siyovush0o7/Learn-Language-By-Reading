package uz.siyovush.learnlanguagebyreading.util


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.io.RandomAccessBufferedFileInputStream
import com.tom_roush.pdfbox.io.RandomAccessFile
import com.tom_roush.pdfbox.pdfparser.PDFParser
import com.tom_roush.pdfbox.pdfwriter.COSWriter
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.File
import com.tom_roush.pdfbox.pdmodel.PDDocument
import java.io.BufferedOutputStream


fun extractData(context: Context, filePath: String, page: Int): String {
    PDFBoxResourceLoader.init(context)

    val file = RandomAccessBufferedFileInputStream(filePath)
    val parser = PDFParser(file)
    parser.parse()

    val document = parser.pdDocument
    val stripper = PDFTextStripper()

    stripper.startPage = page
    stripper.endPage = page + 1

    val text = stripper.getText(document)

    document.close()
    file.close()

    return text
}


fun getPageCount(filePath: String): Int {
    val file = RandomAccessBufferedFileInputStream(filePath)
    val parser = PDFParser(file)
    parser.parse()

    val document = parser.pdDocument
    return document.numberOfPages

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