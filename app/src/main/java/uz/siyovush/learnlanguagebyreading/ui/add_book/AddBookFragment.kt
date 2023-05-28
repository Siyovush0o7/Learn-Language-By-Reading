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
import androidx.navigation.fragment.findNavController
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
                findNavController().popBackStack()
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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUi() {
        binding.apply {
            image.setOnClickListener {
                galleryRequest.launch("image/*") // Pass the desired MIME type here, e.g., "image/*"
            }
            addBtn.setOnClickListener {
                if (binding.titleField.text.toString().isNotEmpty())
                    getContent.launch(arrayOf("application/pdf"))
                else binding.titleField.error = "Empty !"
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

}