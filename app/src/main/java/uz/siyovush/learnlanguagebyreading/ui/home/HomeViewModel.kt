package uz.siyovush.learnlanguagebyreading.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import uz.siyovush.learnlanguagebyreading.data.database.dao.PdfFileDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.PdfFileEntity
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    bookDao: BookDao,
    private val pdfFileDao: PdfFileDao
) : ViewModel() {
    val books = bookDao.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    suspend fun getPdfById(fileName: String): PdfFileEntity {
        return pdfFileDao.getPdfById(fileName)
    }
}