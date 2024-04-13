package com.example.appxemphim.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appxemphim.databinding.PageNumberItemLayoutBinding
import com.example.appxemphim.databinding.TapPhimItemLayoutBinding
import com.example.appxemphim.model.Episode

class PageAdapter: RecyclerView.Adapter<PageAdapter.pageViewHolder>() {

    private var itemSelected: Int = 0

    private val callback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageNumberItemLayoutBinding.inflate(inflater, parent, false)
        return pageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: pageViewHolder, position: Int) {
        val page = differ.currentList[position]
        holder.bind(page)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(page: Int)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class pageViewHolder(val binding: PageNumberItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: Int) {
            binding.tvNumber.text = (page + 1).toString()

            itemView.setOnClickListener {

                itemSelected = page
                click?.onItemClick(page)
                notifyDataSetChanged()
            }

            if (page == itemSelected) {
                binding.tvNumber.setTextColor(Color.RED)
                binding.layout.setBackgroundColor(Color.WHITE)
            } else {
                binding.tvNumber.setTextColor(Color.WHITE)
                binding.layout.setBackgroundColor(Color.GRAY)
            }

        }

    }
}