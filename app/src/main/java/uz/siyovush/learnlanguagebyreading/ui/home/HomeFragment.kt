package uz.siyovush.learnlanguagebyreading.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.R
import uz.siyovush.learnlanguagebyreading.data.database.AppDatabase
import uz.siyovush.learnlanguagebyreading.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val adapter by lazy { BookAdapter() }
    private val viewModel by viewModels<HomeViewModel>()
    private var sortByAscending = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUi() {
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            adapter.onClick = { book ->
                lifecycleScope.launch {
//                    val pdf = viewModel.getPdfById(book.file)
                    findNavController().navigate(
                        R.id.action_homeFragment_to_readFragment,
                        bundleOf("book" to book) //"pdf" to pdf
                    )
                }
            }

            adapter.onLongClick = { book ->
                Snackbar.make(
                    binding.root,
                    "Xaqiqatdan ham o'chirmoqchimisiz?",
                    Snackbar.LENGTH_LONG
                ).setAction("OK") {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteById(book.id.toLong())
                    }
                }.show()
            }


            setupObservers(sortByAscending)

            addBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_addBookFragment)
            }

            srt.setOnClickListener {
                if (sortByAscending) sortImage.scaleY =
                    1f.also { type.text = "Z-A"; sortByAscending = false;setupObservers(false) }
                else sortImage.scaleY =
                    (-1f).also { type.text = "A-Z"; sortByAscending = true; setupObservers(true) }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers(asc: Boolean) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBooks(asc).collect { books ->
                    adapter.submit(books)
                    binding.count.text = "Barchasi: ${books.size}"
                }
            }
        }
    }
}
