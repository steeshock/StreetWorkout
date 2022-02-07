package com.steeshock.android.streetworkout.presentation.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.CategoryItemBinding
import com.steeshock.android.streetworkout.databinding.PlaceItemBinding

class CategoryAdapter(val callback: (Category) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    private var items = emptyList<Category>()

    private lateinit var resources: Resources

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        resources = parent.context.resources
        val inflater = LayoutInflater.from(parent.context)
        return CategoryHolder(CategoryItemBinding.inflate(inflater, parent, false))
    }

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
            binding.categoryCardView.setOnClickListener {
                callback.invoke(items[layoutPosition])
            }
        }

        fun bind(item: Category) {
            binding.apply {
                categoryTextView.text = item.category_name
                setupSelectedState(this, item)
            }
        }
    }

    private fun setupSelectedState(binding: CategoryItemBinding, item: Category) {
        if (item.isSelected == true) {
            binding.categoryCardView.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(android.R.color.black, null))
        } else {
            binding.categoryCardView.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.chipsColor, null))
        }
    }
}