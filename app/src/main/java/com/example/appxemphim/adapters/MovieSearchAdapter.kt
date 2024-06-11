package com.example.appxemphim.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appxemphim.databinding.PhimSearchItemLayoutBinding
import com.example.appxemphim.databinding.TapPhimItemLayoutBinding
import com.example.appxemphim.model.Episode
import com.example.appxemphim.model.Movie
import com.example.appxemphim.util.CONFIG

class MovieSearchAdapter : RecyclerView.Adapter<MovieSearchAdapter.movieSearchViewHolder>() {

    private var itemSelected: Int = 0

    private val callback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): movieSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhimSearchItemLayoutBinding.inflate(inflater, parent, false)
        return movieSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: movieSearchViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class movieSearchViewHolder(val binding: PhimSearchItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.tvTenPhim.text = movie.name
            Glide.with(itemView).load(CONFIG.CLOUD_URL+movie.image).into(binding.ivAnhPhim)
            itemView.setOnClickListener {
               click!!.onItemClick(movie)
            }

            if(movie.price>0){
                binding.tvTien.visibility= View.VISIBLE
                binding.tvTien.text= movie.price.toString()
            }
            else{
                binding.tvTien.visibility= View.GONE
            }
        }

    }
}