package uz.siyovush.learnlanguagebyreading.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.siyovush.learnlanguagebyreading.data.database.entity.BookEntity
import uz.siyovush.learnlanguagebyreading.databinding.BookItemBinding

class BookAdapter : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<BookEntity>() {
        override fun areItemsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    var onClick: ((BookEntity) -> Unit)? = null

    fun submit(list: List<BookEntity>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(
            BookItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class BookViewHolder(private val binding: BookItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookEntity) {
            binding.root.setOnClickListener { onClick?.invoke(book) }
            binding.bookName.text = book.title
            binding.bookImage.setImageBitmap(book.image)
        }
    }
}