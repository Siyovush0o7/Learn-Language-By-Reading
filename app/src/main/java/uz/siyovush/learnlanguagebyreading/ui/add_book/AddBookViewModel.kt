package uz.siyovush.learnlanguagebyreading.ui.add_book


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val bookDao: BookDao,
) : ViewModel() {

    fun addBook(book: BookEntity) = viewModelScope.launch {
        bookDao.insert(book)
    }
}