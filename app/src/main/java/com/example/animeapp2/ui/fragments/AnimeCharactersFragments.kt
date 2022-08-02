package com.example.animeapp2.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.animeapp2.ui.MainActivityDetail
import com.example.animeapp2.R
import com.example.animeapp2.adapters.AnimeCharacterAdapter
import com.example.animeapp2.databinding.FragmentAnimeCharactersBinding
import com.example.animeapp2.ui.AnimeDetailViewModel
import com.example.animeapp2.utils.Resource
import com.google.android.material.snackbar.Snackbar

class AnimeCharactersFragments : Fragment(R.layout.fragment_anime_characters) {

    private lateinit var binding: FragmentAnimeCharactersBinding
    lateinit var viewModel: AnimeDetailViewModel
    lateinit var animeCharacterAdapter: AnimeCharacterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivityDetail).viewModel

        binding = FragmentAnimeCharactersBinding.bind(view)

        setupRecyclerView()

        viewModel.animeDetail.observe(viewLifecycleOwner, Observer { response ->
            when (response){
                is Resource.Success-> {
                    response.data?.let { animeDetailResponse ->
                        animeCharacterAdapter.differ.submitList(animeDetailResponse.data.characters)

                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show()
                    }
                }
            }

        })
    }

    private fun setupRecyclerView(){
        animeCharacterAdapter = AnimeCharacterAdapter()
        binding.recyclerViewCharacters.apply {
            adapter = animeCharacterAdapter
            layoutManager = GridLayoutManager(activity,3)

        }
    }
}