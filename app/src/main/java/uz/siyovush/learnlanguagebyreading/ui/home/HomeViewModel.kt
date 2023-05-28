package uz.siyovush.learnlanguagebyreading.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
//import uz.siyovush.learnlanguagebyreading.data.database.dao.PdfFileDao
//import uz.siyovush.learnlanguagebyreading.data.database.entity.PdfFileEntity
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookDao: BookDao,
) : ViewModel() {
    fun getBooks(asc: Boolean): StateFlow<List<BookEntity>> {
        return bookDao.getAllBooksByName(asc)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
    }

    fun deleteById(id: Long) {
        bookDao.deleteById(id)
    }
}