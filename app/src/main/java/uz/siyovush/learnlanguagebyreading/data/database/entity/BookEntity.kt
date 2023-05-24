package uz.siyovush.learnlanguagebyreading.data.database.entity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import uz.siyovush.learnlanguagebyreading.data.database.BitmapConverter

@Parcelize
@Entity
@TypeConverters(value = [BitmapConverter::class])
data class BookEntity(
    val title: String,
    val file: String,
    val image: Bitmap,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
) : Parcelable