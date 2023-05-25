package uz.siyovush.learnlanguagebyreading.ui.add_book


import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.siyovush.learnlanguagebyreading.data.database.dao.BookDao
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
//import uz.siyovush.learnlanguagebyreading.data.database.entity.PdfFileEntity
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val bookDao: BookDao,
) : ViewModel() {

    fun addBook(path: String?, title: String, image: Bitmap) = viewModelScope.launch {
        path?.let {
            val entry = BookEntity(title, path, image)
            bookDao.insert(entry)
        }
    }
}