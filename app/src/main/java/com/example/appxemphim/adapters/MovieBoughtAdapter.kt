package com.example.appxemphim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appxemphim.databinding.PhimItemLayoutBinding
import com.example.appxemphim.model.Collection
import com.example.appxemphim.model.MovieBuy

class MovieBoughtAdapter: RecyclerView.Adapter<MovieBoughtAdapter.movieBoughtViewHolder>() {
    private val callback = object : DiffUtil.ItemCallback<MovieBuy>() {
        override fun areItemsTheSame(oldItem: MovieBuy, newItem: MovieBuy): Boolean {
            return (oldItem.movieId == newItem.movieId) && (oldItem.username == newItem.username)
        }

        override fun areContentsTheSame(oldItem: MovieBuy, newItem: MovieBuy): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): movieBoughtViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhimItemLayoutBinding.inflate(inflater, parent, false)
        return movieBoughtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: movieBoughtViewHolder, position: Int) {
        val movieBuy = differ.currentList[position]
        holder.bind(movieBuy)
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


    inner class movieBoughtViewHolder(val binding: PhimItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movieBuy: MovieBuy) {
            binding.tvTenPhim.text = movieBuy.movieName;
            Glide.with(itemView).load(movieBuy.imageMovie.trim()).into(binding.ivAnh)
            itemView.setOnClickListener {
                click?.onItemClick(movieBuy.movieId)
            }
            binding.tvSoTap.visibility = View.GONE

        }

    }

}