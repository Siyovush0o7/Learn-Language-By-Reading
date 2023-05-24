package uz.siyovush.learnlanguagebyreading.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    bookDao: BookDao,
) : ViewModel() {
    val books = bookDao.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
}