package uz.siyovush.learnlanguagebyreading.ui.add_book

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.siyovush.learnlanguagebyreading.R
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import uz.siyovush.learnlanguagebyreading.databinding.FragmentAddBookBinding
import uz.siyovush.learnlanguagebyreading.util.getFilename
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class AddBookFragment : Fragment(R.layout.fragment_add_book) {

    private val binding by viewBinding(FragmentAddBookBinding::bind)
    private val viewModel by viewModels<AddBookViewModel>()


    private lateinit var bitmap: Bitmap
    private val externalStoragePermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            galleryRequest.launch("image/*")
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private val galleryRequest =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                getBitmapFromUri(uri)?.let {
                    bitmap = it
                }
                binding.image.setImageBitmap(bitmap)
            }
        }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let { selectedUri ->
                requireActivity().contentResolver.takePersistableUriPermission(
                    selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )


                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sample.pdf")

                val contentUri: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "uz.siyovush.learnlanguagebyreading.fileprovider",
                    file
                )

                val book = BookEntity(
                    binding.titleField.text.toString(),
                    contentUri.toString(),
                    bitmap
                )
                Toast.makeText(requireContext(), "added", Toast.LENGTH_SHORT).show()
                viewModel.addBook(book)
                findNavController().popBackStack()
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        val drawable = resources.getDrawable(R.drawable.book_placeholder)
        bitmap = drawable.toBitmap()
        binding.image.setImageBitmap(bitmap)
    }

    private fun setupUi() {
        binding.apply {
            image.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    externalStoragePermissionRequest.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    externalStoragePermissionRequest.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            addBtn.setOnClickListener {
                getContent.launch(arrayOf("application/pdf"))
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            null
        }
    }


    fun getPathFromUri(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver

        // Check if the URI scheme is "content"
        if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val documentFile = DocumentFile.fromSingleUri(context, uri)
            val isVirtualFile = documentFile?.isVirtual ?: false

            // Handle virtual files (e.g., Google Drive files)
            if (isVirtualFile) {
                val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
                val cursor = contentResolver.query(uri, projection, null, null, null)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex =
                            it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                        val fileName = it.getString(columnIndex)
                        val cacheDir = context.cacheDir
                        val tempFile = File(cacheDir, fileName)

                        // Copy the content to a temporary file
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(tempFile).use { outputStream ->
                                val buffer = ByteArray(4 * 1024) // 4KB buffer
                                var bytesRead: Int
                                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                    outputStream.write(buffer, 0, bytesRead)
                                }
                                outputStream.flush()
                            }
                        }

                        return tempFile.absolutePath
                    }
                }
            } else {
                // Handle regular content URIs
                val projection = arrayOf(MediaStore.MediaColumns.DATA)
                val cursor = contentResolver.query(uri, projection, null, null, null)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                        return it.getString(columnIndex)
                    }
                }
            }
        } else if (ContentResolver.SCHEME_FILE == uri.scheme) {
            // Handle file:// URIs
            return uri.path
        }

        return null
    }


    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    fun getFilePath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(
                uri, projection, null, null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

}