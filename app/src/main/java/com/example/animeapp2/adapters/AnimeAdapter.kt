package com.example.animeapp2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animeapp2.*
import com.example.animeapp2.databinding.FragmentAnimeCharactersBinding
import com.example.animeapp2.databinding.ItemAnimePreviewBinding
import com.example.animeapp2.models.Data


class AnimeAdapter() : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    private lateinit var binding: ItemAnimePreviewBinding

    inner class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.mal_id == newItem.mal_id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        return AnimeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_anime_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {

        binding = ItemAnimePreviewBinding.bind(holder.itemView)

        val document = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(document.images).into(binding.imageViewAnimeImage)
            binding.textViewTitle.text = document.title
            binding.textViewSeasonYear.text = document.aired
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