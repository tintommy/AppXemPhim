package com.example.appxemphim.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appxemphim.databinding.DienvienItemLayoutBinding
import com.example.appxemphim.databinding.PhimItemLayoutBinding
import com.example.appxemphim.model.Movie
import com.example.appxemphim.model.Person

class PersonAdapter:RecyclerView.Adapter<PersonAdapter.personViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return  oldItem.personId == newItem.personId
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): personViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DienvienItemLayoutBinding.inflate(inflater, parent, false)
        return personViewHolder(binding)
    }

    override fun onBindViewHolder(holder: personViewHolder, position: Int) {
        val person = differ.currentList[position]
        holder.bind(person)
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



    inner class personViewHolder(val binding: DienvienItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(person: Person) {
            binding.tvTenDv.text = person.name
            Glide.with(itemView).load(person.image.trim()).into(binding.ivHinhDv)
//            itemView.setOnClickListener {
//                click?.onItemClick(movie.movieId)
//            }

        }

    }
}