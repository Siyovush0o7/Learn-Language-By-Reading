package uz.siyovush.learnlanguagebyreading.ui.dictionary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.data.database.dao.DictDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.Dict
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val dictDao: DictDao,
) : ViewModel() {

    private val _currentState = MutableStateFlow(DictState("Ingliz", "O'zbek", emptyList()))
    val currentState = _currentState.asStateFlow()

    private var enToUz = emptyList<Dict>()
    private var uzToEn = emptyList<Dict>()

    private var query: String = ""

    init {
        loadDict()
    }

    fun toggleState() {
        query = ""
        if (_currentState.value.first == "Ingliz") {
            _currentState.value = DictState("O'zbek", "Ingliz", uzToEn)
        } else {
            _currentState.value = DictState("Ingliz", "O'zbek", enToUz)
        }
    }

    fun updateQuery(query: String) {
        this.query = query
        if (_currentState.value.first == "Ingliz") {
            _currentState.value = DictState("Ingliz", "O'zbek", filter(query, enToUz))
        } else {
            _currentState.value = DictState("O'zbek", "Ingliz", filter(query, uzToEn))
        }
    }

    private fun filter(query: String, dict: List<Dict>): List<Dict> {
        if (query.isEmpty()) return dict
        return dict.filter { it.word.startsWith(query) }
    }

    private fun loadDict() = viewModelScope.launch {
        val enUz = async { dictDao.getTranslations(1).filterNot { it.word.startsWith(" ") || it.word.startsWith("'") } }
        val uzEn = async { dictDao.getTranslations(0) }
        enToUz = enUz.await()
        uzToEn = uzEn.await()
        if (_currentState.value.first == "Ingliz") {
            _currentState.value = _currentState.value.copy(dict = enToUz)
        } else {
            _currentState.value = _currentState.value.copy(dict = uzToEn)
        }
    }
}

data class DictState(
    val first: String,
    val second: String,
    val dict: List<Dict>,
)