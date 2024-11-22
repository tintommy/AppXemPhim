package com.example.appxemphim.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.appxemphim.R
import com.example.appxemphim.databinding.CommentItemLayoutBinding
import com.example.appxemphim.databinding.DanhMucItemLayoutBinding
import com.example.appxemphim.model.Category
import com.example.appxemphim.model.Comment
import com.example.appxemphim.util.CONFIG

class CommentAdapter: RecyclerView.Adapter<CommentAdapter.commentViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return  oldItem.idComment == newItem.idComment
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): commentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CommentItemLayoutBinding.inflate(inflater, parent, false)
        return commentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: commentViewHolder, position: Int) {
        val comment = differ.currentList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



    interface OnItemClickListener {
        fun onItemClick(commenId: Int)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }



    inner class commentViewHolder(val binding: CommentItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(comment: Comment){
            binding.apply {
                Glide.with(itemView).load(CONFIG.CLOUD_URL+comment.avatar).apply(RequestOptions().error(R.drawable.no_avatar)).into(ivAnhCmt)
                tvTenCmt.text= comment.username
                tvNgayCmt.text=comment.date
                tvCmt.text=comment.comment
                tvSao.text= comment.value.toString()
            }
        }

    }


}