package uz.siyovush.learnlanguagebyreading.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.R
import uz.siyovush.learnlanguagebyreading.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val adapter by lazy { BookAdapter() }
    private val viewModel by viewModels<HomeViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter.onClick = { book ->
                lifecycleScope.launch {
//                    val pdf = viewModel.getPdfById(book.file)
                    findNavController().navigate(
                        R.id.action_homeFragment_to_readFragment,
                        bundleOf("book" to book, ) //"pdf" to pdf
                    )
                }
            }
            setupObservers()
            addBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_addBookFragment)
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.books.collect {
                    adapter.submit(it)
                }
            }
        }
    }
}