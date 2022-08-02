package com.example.animeapp2.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.animeapp2.R
import com.example.animeapp2.adapters.AnimeFavoriteAdapter
import com.example.animeapp2.databinding.FragmentAnimeFavoriteBinding
import com.example.animeapp2.ui.AnimeViewModel
import com.example.animeapp2.ui.MainActivity

class AnimeFavoriteFragment : Fragment(R.layout.fragment_anime_favorite) {
    lateinit var viewModel: AnimeViewModel
    lateinit var animeFavoriteAdapter: AnimeFavoriteAdapter
    private lateinit var binding: FragmentAnimeFavoriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        binding = FragmentAnimeFavoriteBinding.bind(view)

        setupRecycleView()

        animeFavoriteAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("animeSer",it)
            }
            findNavController().navigate(
                R.id.action_animeFavoriteFragment_to_mainActivityDetail2,
                bundle
            )
        }

        viewModel.getSavedAnime().observe(viewLifecycleOwner, Observer {
            animeFavoriteAdapter.differ.submitList(it)
        })
    }

    private fun setupRecycleView() {
        animeFavoriteAdapter = AnimeFavoriteAdapter()
        binding.recycleFavorite.apply {
            adapter = animeFavoriteAdapter
            layoutManager = GridLayoutManager(activity,3)
        }
    }
}