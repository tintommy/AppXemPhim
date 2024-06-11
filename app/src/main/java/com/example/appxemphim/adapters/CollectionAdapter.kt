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
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.CONFIG

class CollectionAdapter: RecyclerView.Adapter<CollectionAdapter.collectionViewHolder>() {
    private val callback = object : DiffUtil.ItemCallback<Collection>() {
        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return  (oldItem.movieId == newItem.movieId) && (oldItem.username == newItem.username)
        }

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): collectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhimItemLayoutBinding.inflate(inflater, parent, false)
        return collectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: collectionViewHolder, position: Int) {
        val collection = differ.currentList[position]
        holder.bind(collection)
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



    inner class collectionViewHolder(val binding: PhimItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(collection: Collection){
            binding.tvTenPhim.text=collection.movieName;
            Glide.with(itemView).load(CONFIG.CLOUD_URL+collection.imageMovie.trim()).into(binding.ivAnh)
            itemView.setOnClickListener {
                click?.onItemClick(collection.movieId)
            }
                binding.tvSoTap.visibility= View.GONE

        }

    }
}