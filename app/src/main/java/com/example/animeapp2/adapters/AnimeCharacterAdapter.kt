package com.example.animeapp2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animeapp2.R
import com.example.animeapp2.databinding.ItemAnimeCharactersPreviewBinding
import com.example.animeapp2.databinding.ItemAnimeFavoritePreviewBinding
import com.example.animeapp2.models.Characters
import com.example.animeapp2.models.Data

class AnimeCharacterAdapter : RecyclerView.Adapter<AnimeCharacterAdapter.AnimeCharacterViewHolder>() {

    private lateinit var binding: ItemAnimeCharactersPreviewBinding

    inner class AnimeCharacterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<Characters>() {
        override fun areItemsTheSame(oldItem: Characters, newItem: Characters): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Characters, newItem: Characters): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeCharacterViewHolder {
        return AnimeCharacterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_anime_characters_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AnimeCharacterViewHolder, position: Int) {

        binding = ItemAnimeCharactersPreviewBinding.bind(holder.itemView)

        val document = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(document.images).into(binding.imageViewAnimeImageCharacter)
            binding.textViewNameCharacter.text = document.name
            binding.textViewRoleCharacter.text = document.role
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}