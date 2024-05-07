package com.example.appxemphim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.appxemphim.databinding.PhimItemLayoutBinding
import com.example.appxemphim.model.Movie
import kotlinx.coroutines.withContext

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.moviesViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return  oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): moviesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhimItemLayoutBinding.inflate(inflater, parent, false)
        return moviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: moviesViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



    interface OnItemClickListener {
        fun onItemClick(movie:Movie, price:Int)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }



    inner class moviesViewHolder(val binding: PhimItemLayoutBinding ):RecyclerView.ViewHolder(binding.root){
        fun bind(movie: Movie){
            binding.tvTenPhim.text=movie.name;
            Glide.with(itemView).load(movie.image.trim()).into(binding.ivAnh)
            itemView.setOnClickListener {
                click?.onItemClick(movie, movie.price)
            }

            if(movie.episodes>1){
                binding.tvSoTap.visibility= View.VISIBLE
                binding.tvSoTap.text="Tập "+movie.episodeList.size+"/"+movie.episodes

            }
            else{
                binding.tvSoTap.visibility=View.GONE
            }

            if(movie.price>0){
                binding.layoutTien.visibility=View.VISIBLE
                binding.tvTien.text= movie.price.toString()
            }
            else{
                binding.layoutTien.visibility=View.GONE
            }
        }

    }


}