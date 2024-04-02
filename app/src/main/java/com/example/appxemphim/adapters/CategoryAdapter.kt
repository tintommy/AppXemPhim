package com.example.appxemphim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appxemphim.databinding.DanhMucItemLayoutBinding
import com.example.appxemphim.databinding.PhimItemLayoutBinding
import com.example.appxemphim.model.Category
import com.example.appxemphim.model.Movie

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.categoryViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return  oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): categoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DanhMucItemLayoutBinding.inflate(inflater, parent, false)
        return categoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: categoryViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



    interface OnItemClickListener {
        fun onItemClick(movieId: Int)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }



    inner class categoryViewHolder(val binding: DanhMucItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(category: Category){
           binding.tvTenMuc.text=category.name
        }

    }


}