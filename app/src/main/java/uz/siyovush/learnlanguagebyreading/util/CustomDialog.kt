package uz.siyovush.learnlanguagebyreading.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.R

class CustomDialog : DialogFragment(R.layout.custom_translate) {

    private var onSave: (() -> Unit)? = null
    private var onSpeak: (() -> Unit)? = null
    private var getText: (() -> Pair<String, String>)? = null
    private var getSentence: (() -> Pair<String, String>)? = null

    private lateinit var tvWord: TextView
    private lateinit var tvSentence: TextView
    private lateinit var yopish: MaterialButton
    private lateinit var saqlash: MaterialButton


    override fun onStart() {
        super.onStart()
        isCancelable = false
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        tvWord = view?.findViewById(R.id.tv_word) as TextView
        tvSentence = view?.findViewById(R.id.tv_sentence) as TextView
        yopish = view?.findViewById(R.id.btn_ok) as MaterialButton
        saqlash = view?.findViewById(R.id.btn_save) as MaterialButton
        saqlash.visibility = View.INVISIBLE
        yopish.visibility = View.INVISIBLE


        view?.findViewById<ImageView>(R.id.btn_speak)?.setOnClickListener {
            onSpeak?.let { it1 -> it1() }
        }

        updateText()

    }

    private fun updateText() {
        getText?.let {
            val (word, translation) = it()
            val wordTranslation = "$word - $translation"
            tvWord.text = wordTranslation
            saqlash.visibility = View.VISIBLE
            yopish.visibility = View.VISIBLE
            yopish.setOnClickListener {
                this.hide()
            }
            saqlash.setOnClickListener {
                onSave?.let { it1 -> it1() }
                this.hide()
            }
        }

        getSentence?.let {
            val (original, translate) = it()
            val sentenceTranslation = "$original\n\n$translate"
            tvSentence.text = sentenceTranslation
        }
    }

    fun onSpeak(func: (() -> Unit)) {
        this.onSpeak = func
    }

    fun onSave(save: (() -> Unit)) {
        this.onSave = save
    }

    fun textChange(getText: (() -> Pair<String, String>)) {
        this.getText = getText
        updateText()
    }

    fun sentenceChange(getSentence: (() -> Pair<String, String>)) {
        this.getSentence = getSentence
        updateText()
    }
}

