package uz.siyovush.learnlanguagebyreading.ui.read

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import uz.siyovush.learnlanguagebyreading.data.database.dao.WordDao
import uz.siyovush.learnlanguagebyreading.data.model.Book
import uz.siyovush.learnlanguagebyreading.data.model.Language
import uz.siyovush.learnlanguagebyreading.data.model.TranslationResult
import uz.siyovush.learnlanguagebyreading.data.repository.TranslateRepository
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    private val repository: TranslateRepository,
    private val wordDao: WordDao,
    private val book: BookDao
) : ViewModel() {

    private val _word = MutableStateFlow<TranslationResult?>(null)
    val word = _word.asStateFlow()

    private val _sentence = MutableStateFlow("")
    val sentence = _sentence.asStateFlow()

    private val _language = MutableStateFlow(Language.values().first())
    val language = _language.asStateFlow()

    fun onClickWord(text: String, sentence: String) = viewModelScope.launch {
        val wordReq = async { repository.translate(text, language.value.code) }
        val sentenceReq = async { repository.translate(sentence, language.value.code) }
        val isFavoriteReq = async { wordDao.isFavorite(text) }
        _word.value =
            TranslationResult(text, wordReq.await(), language.value.code, isFavoriteReq.await())
        _sentence.value = sentenceReq.await()
    }

    fun changeLanguage(language: Language) {
        _language.value = language
        if (_word.value != null) {
            onClickWord(_word.value!!.original, _sentence.value)
        }
    }

    fun updateFavorite() = viewModelScope.launch {
        if (word.value?.isFavorite == true) {
            wordDao.delete(word.value!!.original)
            _word.value = _word.value!!.copy(
                isFavorite = false
            )
        } else {
            wordDao.insert(word.value!!.toWordTranslation())
            _word.value = _word.value!!.copy(
                isFavorite = true
            )
        }
    }

    fun deleteBook(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            book.deleteById(id)
        }
    }
}