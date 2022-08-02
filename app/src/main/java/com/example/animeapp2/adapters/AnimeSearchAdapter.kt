package com.example.animeapp2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animeapp2.R
import com.example.animeapp2.databinding.ItemAnimeSearchPreviewBinding
import com.example.animeapp2.models.Data

class AnimeSearchAdapter() : RecyclerView.Adapter<AnimeSearchAdapter.AnimeSearchViewHolder>() {

    private lateinit var binding: ItemAnimeSearchPreviewBinding

    inner class AnimeSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.mal_id == newItem.mal_id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeSearchViewHolder {
        return AnimeSearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_anime_search_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnimeSearchViewHolder, position: Int) {

        binding = ItemAnimeSearchPreviewBinding.bind(holder.itemView)

        val document = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(document.images).into(binding.imageViewAnimeImageSearch)
            binding.textViewTitleSearch.text = document.title
            binding.textViewSeasonYearSearch.text = document.aired?.substringBefore("-")
            binding.textViewScoreSearch.text = document.score.toString()
            setOnClickListener {
                onItemClickListener?.let { it(document) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Data) -> Unit)? = null

    fun setOnClickListener(listener: (Data) -> Unit) {
        onItemClickListener = listener
    }
}