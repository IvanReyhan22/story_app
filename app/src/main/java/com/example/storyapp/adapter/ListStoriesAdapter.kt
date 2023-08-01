package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.entity.StoriesEntity
import com.example.storyapp.databinding.ItemPostBinding
import com.example.storyapp.utils.withDateFormat

class ListStoriesAdapter() :
    PagingDataAdapter<StoriesEntity, ListStoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener { onItemClickCallback.onItemClick(data) }
        }
    }

    class ViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoriesEntity) {
            binding.tvItemName.text = data.name
            binding.tvItemCreatedAt.text = data.createdAt?.withDateFormat()
            binding.tvItemPostDesc.text = data.description
            Glide.with(binding.ivItemPostImage.context).load(data.photoUrl)
                .into(binding.ivItemPostImage)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesEntity>() {
            override fun areItemsTheSame(oldItem: StoriesEntity, newItem: StoriesEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoriesEntity,
                newItem: StoriesEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClick(data: StoriesEntity)
    }
}