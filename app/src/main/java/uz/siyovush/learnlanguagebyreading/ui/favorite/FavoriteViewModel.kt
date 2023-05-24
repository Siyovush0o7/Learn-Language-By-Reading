package uz.siyovush.learnlanguagebyreading.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.data.database.dao.WordDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.WordTranslation
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val wordDao: WordDao,
) : ViewModel() {

    val favorites = wordDao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    fun delete(translation: WordTranslation) = viewModelScope.launch {
        wordDao.delete(translation.original)
    }
}