package com.steeshock.android.streetworkout.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.databinding.CategoryItemBinding

class CategoryAdapter(val callback: (Category) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    private var items = emptyList<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bind(items[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setCategories(categories: List<Category>) {
        this.items = categories
        notifyDataSetChanged()
    }

    inner class CategoryHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setItemClickListener {
                binding.category?.let { category ->
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) callback.invoke(category)
                }
            }
        }

        fun bind(item: Category) {
            binding.apply {
                category = item
                executePendingBindings()
            }
        }
    }
}