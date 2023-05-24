package uz.siyovush.learnlanguagebyreading.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.siyovush.learnlanguagebyreading.data.database.entity.Dict
import uz.siyovush.learnlanguagebyreading.databinding.DictItemBinding

class DictAdapter : RecyclerView.Adapter<DictAdapter.DictViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<Dict>() {
        override fun areItemsTheSame(oldItem: Dict, newItem: Dict): Boolean {
            return oldItem.word == newItem.word
        }

        override fun areContentsTheSame(oldItem: Dict, newItem: Dict): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    var onClick: ((Dict) -> Unit)? = null

    fun submit(list: List<Dict>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictViewHolder {
        return DictViewHolder(
            DictItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: DictViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class DictViewHolder(private val binding: DictItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Dict) {
            binding.root.text = item.word
            binding.root.setOnClickListener { onClick?.invoke(item) }
        }
    }
}