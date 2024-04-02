package com.example.appxemphim.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appxemphim.databinding.PhimItemLayoutBinding
import com.example.appxemphim.databinding.TapPhimItemLayoutBinding
import com.example.appxemphim.model.Episode
import com.example.appxemphim.model.Movie

class EpisodeAdapter : RecyclerView.Adapter<EpisodeAdapter.episodeViewHolder>() {

    private var itemSelected: Int = 0

    private val callback = object : DiffUtil.ItemCallback<Episode>() {
        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem.episodeId == newItem.episodeId
        }

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): episodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TapPhimItemLayoutBinding.inflate(inflater, parent, false)
        return episodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: episodeViewHolder, position: Int) {
        val episode = differ.currentList[position]
        holder.bind(episode, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(link: String)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class episodeViewHolder(val binding: TapPhimItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(episode: Episode, position: Int) {
            binding.tvTap.text = "Tập " + (position + 1).toString()

            itemView.setOnClickListener {

                itemSelected = position
                click?.onItemClick(episode.link)
                notifyDataSetChanged()
            }

            if (position == itemSelected) {
                binding.tvTap.setTextColor(Color.RED)
                binding.cvTap.setCardBackgroundColor(Color.WHITE)
            } else {
                binding.tvTap.setTextColor(Color.WHITE)
                binding.cvTap.setCardBackgroundColor(Color.GRAY)
            }

        }

    }
}