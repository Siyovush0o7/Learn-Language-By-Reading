package uz.siyovush.learnlanguagebyreading.ui.favorite

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.siyovush.learnlanguagebyreading.data.database.entity.WordTranslation
import uz.siyovush.learnlanguagebyreading.databinding.FavoriteItemBinding

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<WordTranslation>() {
        override fun areItemsTheSame(oldItem: WordTranslation, newItem: WordTranslation): Boolean {
            return oldItem.original == newItem.original
        }

        override fun areContentsTheSame(oldItem: WordTranslation, newItem: WordTranslation): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    var onDelete: ((WordTranslation) -> Unit)? = null

    fun submit(list: List<WordTranslation>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(
            FavoriteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class FavoriteViewHolder(private val binding: FavoriteItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(translation: WordTranslation) {
            binding.text.text = Html.fromHtml("${translation.original} - ${translation.translation}")
            binding.deleteBtn.setOnClickListener { onDelete?.invoke(translation) }
        }
    }
}