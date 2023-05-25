package uz.siyovush.learnlanguagebyreading.ui.read

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.BackgroundColorSpan
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.R
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import uz.siyovush.learnlanguagebyreading.data.model.Language
import uz.siyovush.learnlanguagebyreading.databinding.FragmentReadBinding
import uz.siyovush.learnlanguagebyreading.util.extractData
import java.io.File
import java.text.BreakIterator
import java.util.Locale

@AndroidEntryPoint
class ReadFragment : Fragment(R.layout.fragment_read) {

    private val binding by viewBinding(FragmentReadBinding::bind)
    private val viewModel by viewModels<ReadViewModel>()

    private lateinit var tts: TextToSpeech
    private var ttsInit = false
    private var currentPage = 0

    private lateinit var book: BookEntity

    private var gestureDetector: GestureDetector? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                ttsInit = true
            }
        }
        book = requireArguments().getParcelable<BookEntity>("book")!!

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()

        val extractedText =
            extractData(requireActivity(), book.file, currentPage)
        binding.textView.text = extractedText

        gestureDetector = GestureDetector(requireContext(), object : SimpleOnGestureListener() {
            override fun onDoubleTap(event: MotionEvent): Boolean {
                val offset: Int = binding.textView.getOffsetForPosition(event.x, event.y)
                val text = binding.textView.text.toString()
                if (offset > 0 && offset < text.length) {
                    val start = text.lastIndexOf(" ", offset - 1) + 1
                    var end = text.indexOf(" ", offset)
                    if (end < start) {
                        end = text.length
                    }
                    val word = text.substring(start, end).trim()
                        .removeSuffix(".")
                        .removeSuffix(",")
                    val breakIterator = BreakIterator.getSentenceInstance()
                    breakIterator.setText(text)
                    val sentence = text.substring(
                        breakIterator.preceding(offset),
                        breakIterator.following(offset)
                    )
                    val span = BackgroundColorSpan(Color.YELLOW)
                    val spannable = SpannableString(text)
                    spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    binding.textView.text = spannable
                    viewModel.onClickWord(word, sentence)
                }
                return true
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setupUI() {
        binding.apply {
            val menu = toolbar.menu
            val subMenu = menu.findItem(R.id.language_menu).subMenu
            Language.values().forEachIndexed { index, lang ->
                subMenu?.add(0, lang.id, index, lang.title)
            }
            toolbar.setOnMenuItemClickListener { menuItem ->
                Language.values().find { it.id == menuItem.itemId }
                    ?.let { viewModel.changeLanguage(it) }
                true
            }
            toolbar.title = book.title
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            textView.movementMethod = ScrollingMovementMethod()
            textView.setOnTouchListener { v, event ->
                gestureDetector?.onTouchEvent(event)
                v.performClick()
            }
            speakBtn.setOnClickListener {
                if (ttsInit) {
                    val word = viewModel.word.value?.original ?: return@setOnClickListener
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, word)
                }
            }
            starBtn.setOnClickListener {
                viewModel.updateFavorite()
            }
            deleteBtn.setOnClickListener {
                viewModel.deleteBook(book.id.toLong())
                findNavController().popBackStack()
            }


        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.word.collect { word ->
                        if (word == null) return@collect
                        binding.translationRow.isVisible = true
                        binding.translation.text =
                            Html.fromHtml("${word.original} - ${word.translation}")
                        binding.starBtn.setImageResource(
                            if (word.isFavorite) R.drawable.ic_star else R.drawable.ic_star_outlined
                        )
                    }
                }
                launch {
                    viewModel.language.collect { selectedLang ->
                        binding.toolbar.menu.findItem(R.id.language_menu).subMenu?.apply {
                            Language.values().forEach { lang ->
                                findItem(lang.id)?.setIcon(
                                    if (selectedLang.id == lang.id) R.drawable.ic_check else R.drawable.transparent_icon
                                )
                            }
                        }
                    }
                }
                launch {
                    viewModel.sentence.collect { sentence ->
                        binding.sentenceTranslation.isVisible = true
                        binding.sentenceTranslation.text = Html.fromHtml(sentence)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gestureDetector = null
        tts.stop()
        tts.shutdown()
    }
}
