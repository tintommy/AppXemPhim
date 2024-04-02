package com.example.appxemphim.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appxemphim.databinding.DanhMucItemLayoutBinding
import com.example.appxemphim.model.Category
import com.example.appxemphim.model.Country

class CountryAdapter : RecyclerView.Adapter<CountryAdapter.countryViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.countryId == newItem.countryId
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): countryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DanhMucItemLayoutBinding.inflate(inflater, parent, false)
        return countryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: countryViewHolder, position: Int) {
        val country = differ.currentList[position]
        holder.bind(country)
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


    inner class countryViewHolder(val binding: DanhMucItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(country: Country) {
            binding.tvTenMuc.text = country.name
        }

    }


}
