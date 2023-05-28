package uz.siyovush.learnlanguagebyreading.ui.read

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.siyovush.learnlanguagebyreading.R
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import uz.siyovush.learnlanguagebyreading.data.model.Language
import uz.siyovush.learnlanguagebyreading.databinding.FragmentReadBinding
import uz.siyovush.learnlanguagebyreading.util.CustomDialog
import uz.siyovush.learnlanguagebyreading.util.DialogJump
import uz.siyovush.learnlanguagebyreading.util.PrefManager
import uz.siyovush.learnlanguagebyreading.util.ProgressDialog
import uz.siyovush.learnlanguagebyreading.util.extractData
import uz.siyovush.learnlanguagebyreading.util.getPageCount
import uz.siyovush.learnlanguagebyreading.util.hide
import uz.siyovush.learnlanguagebyreading.util.show
import java.text.BreakIterator
import java.util.Locale
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReadFragment : Fragment(R.layout.fragment_read) {

    private val binding by viewBinding(FragmentReadBinding::bind)
    private val viewModel by viewModels<ReadViewModel>()

    private lateinit var tts: TextToSpeech
    private var ttsInit = false

    private lateinit var book: BookEntity

    private var gestureDetector: GestureDetector? = null

    private lateinit var word: String
    private lateinit var sentence: String


    private var sharedPref = PrefManager.Companion
    private var currentPage by Delegates.notNull<Int>()
    private var allPage by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        book = requireArguments().getParcelable<BookEntity>("book")!!
        currentPage = sharedPref.getPage(requireContext(), book.id)
        allPage = getPageCount(book.file)
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.US
                ttsInit = true
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        val dialog = CustomDialog()
        setupObservers(dialog)

        val extractedText =
            extractData(requireActivity(), book.file, currentPage)
        binding.textView.text = extractedText

        gestureDetector = GestureDetector(requireContext(), object : SimpleOnGestureListener() {
            override fun onDoubleTap(event: MotionEvent): Boolean {


                dialog.show(parentFragmentManager)
                dialog.onSave {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateFavorite()
                    }
                }

                dialog.onSpeak {
                    if (ttsInit) {
                        val word = viewModel.word.value?.original
                        tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, word)
                    }
                }


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
                    this@ReadFragment.word = word
                    this@ReadFragment.sentence = sentence
                }
                return true
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "ResourceAsColor")
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

            current.setOnClickListener {
                val dialog = DialogJump()
                dialog.show(parentFragmentManager)
                dialog.onStep {
                    if (it in 1..allPage) {
                        textView.text = extractData(requireContext(), book.file, it)
                        currentPage = it
                        val textPage = "$currentPage/$allPage"
                        current.text = textPage
                        sharedPref.setPage(requireContext(), book.id, currentPage)
                    } else Toast.makeText(
                        requireContext(),
                        "Sahifa mavjud emas",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.hide()
                }
            }
            tts = TextToSpeech(requireContext()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts.language = Locale.US
                    ttsInit = true
                }
            }


            val title = SpannableString(book.title)
            title.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.teal_700
                    )
                ), 0, title.length, 0
            )
            toolbar.title = title

            // Set the back button with custom color
            val backDrawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
            val backColor = ContextCompat.getColor(requireContext(), R.color.teal_700)
            backDrawable?.let { DrawableCompat.setTint(it, backColor) }
            toolbar.navigationIcon = backDrawable
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            textView.movementMethod = ScrollingMovementMethod()
            textView.setOnTouchListener { v, event ->
                gestureDetector?.onTouchEvent(event)
                v.performClick()
            }
            current.text = "$currentPage/$allPage"
            next.setOnClickListener {
                if (currentPage in 1 until allPage) {
                    val dialog = ProgressDialog()
                    dialog.show(childFragmentManager)
                    currentPage++
                    sharedPref.setPage(requireContext(), book.id, currentPage)
                    val textPage = "$currentPage/$allPage"
                    current.text = textPage

                    CoroutineScope(Dispatchers.IO).launch {
                        val extractedText =
                            extractData(requireActivity(), book.file, currentPage)

                        withContext(Dispatchers.Main) {
                            textView.text = extractedText
                            dialog.dismiss()
                        }
                    }
                } else Toast.makeText(requireContext(), "last page !", Toast.LENGTH_SHORT)
                    .show()
            }

            previous.setOnClickListener {
                if (currentPage in 2..allPage) {
                    val dialog = ProgressDialog()
                    dialog.show(childFragmentManager)
                    currentPage--
                    sharedPref.setPage(requireContext(), book.id, currentPage)

                    val textPage = "$currentPage/$allPage"
                    current.text = textPage

                    CoroutineScope(Dispatchers.IO).launch {
                        val extractedText =
                            extractData(requireActivity(), book.file, currentPage)

                        withContext(Dispatchers.Main) {
                            textView.text = extractedText
                            dialog.dismiss()
                        }
                    }
                } else Toast.makeText(requireContext(), "first page !", Toast.LENGTH_SHORT)
                    .show()

            }
        }
    }

    private fun setupObservers(dialog: CustomDialog) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.word.collect { word ->
                        if (word == null) return@collect
                        dialog.textChange {
                            return@textChange Pair(
                                this@ReadFragment.word,
                                Html.fromHtml(word.translation).toString()
                            )
                        }
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
                        if (sentence == "") return@collect
                        dialog.sentenceChange {
                            return@sentenceChange Pair(
                                this@ReadFragment.sentence,
                                Html.fromHtml(sentence).toString()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        gestureDetector = null
        tts.stop()
        tts.shutdown()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        gestureDetector = null
        tts.stop()
        tts.shutdown()
    }
}
