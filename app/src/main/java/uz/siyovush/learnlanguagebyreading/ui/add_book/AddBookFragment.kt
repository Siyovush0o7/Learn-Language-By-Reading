package uz.siyovush.learnlanguagebyreading.ui.add_book

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.siyovush.learnlanguagebyreading.R
import uz.siyovush.learnlanguagebyreading.databinding.FragmentAddBookBinding
import uz.siyovush.learnlanguagebyreading.util.getFilename
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class AddBookFragment : Fragment(R.layout.fragment_add_book) {

    private val binding by viewBinding(FragmentAddBookBinding::bind)
    private val viewModel by viewModels<AddBookViewModel>()


    private lateinit var bitmap: Bitmap
    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                Log.d("AddBookFragment", "copied")

                val inputStream = requireActivity().contentResolver.openInputStream(it)
                val fileName = it.getFilename(requireActivity().contentResolver).toString()
                val outputFile = File(
                    requireActivity().filesDir,
                    fileName
                )
                val outputStream = FileOutputStream(outputFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                Log.d("AddBookFragment", fileName)


                val path = outputFile.path
                viewModel.addBook(path, binding.titleField.text.toString(), bitmap)
                // Use the extracted text as needed
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
                    galleryRequest.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    galleryRequest.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
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

    private fun sendApkFile(context: Context) {
        try {
            val pm = context.packageManager
            val ai = pm.getApplicationInfo(context.packageName, 0)
            val srcFile = File(ai.publicSourceDir)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "*/*"
            val uri: Uri =
                FileProvider.getUriForFile(requireContext(), context.packageName, srcFile)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.grantUriPermission(
                context.packageManager.toString(),
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}