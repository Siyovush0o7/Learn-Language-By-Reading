package uz.siyovush.learnlanguagebyreading.ui.add_book


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import uz.siyovush.learnlanguagebyreading.data.database.dao.PdfFileDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import uz.siyovush.learnlanguagebyreading.data.database.entity.PdfFileEntity
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val bookDao: BookDao,
    private val pdfFileDao: PdfFileDao
) : ViewModel() {

    fun addBook(book: BookEntity) = viewModelScope.launch {
        bookDao.insert(book)
    }

    fun addPdfFile(pdf: PdfFileEntity) = viewModelScope.launch {
        pdfFileDao.insertPdfFile(pdf)
    }
}