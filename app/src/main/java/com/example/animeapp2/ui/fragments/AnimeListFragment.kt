package com.example.animeapp2.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.animeapp2.*
import com.example.animeapp2.adapters.AnimeAdapter
import com.example.animeapp2.databinding.FragmentAnimeListBinding
import com.example.animeapp2.ui.AnimeViewModel
import com.example.animeapp2.ui.MainActivity
import com.example.animeapp2.utils.Resource
import com.google.android.material.snackbar.Snackbar

class AnimeListFragment() : Fragment(R.layout.fragment_anime_list) {

    private lateinit var binding: FragmentAnimeListBinding
    lateinit var viewModel: AnimeViewModel
    lateinit var animeAdapter: AnimeAdapter
    var pageNumber : Int = 1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        binding = FragmentAnimeListBinding.bind(view)
        val lottieAnimation = binding.lottieAnimation
        val buttonNext = binding.buttonNext
        val buttonBack = binding.buttonBack


        setupRecyclerView()

        animeAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("animeSer",it)
            }
            findNavController().navigate(
                R.id.action_animeListFragment2_to_mainActivityDetail2,
                bundle
            )
        }

        viewModel.animeList.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { animeResponse ->

                        lottieAnimation.cancelAnimation()
                        lottieAnimation.visibility = View.GONE

                        animeAdapter.differ.submitList(animeResponse.data)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    getLottieAnimation()
                }
            }
        })

        buttonNext.setOnClickListener {
            pageNumber = pageNumber + 1
            viewModel.getAnimeList(pageNumber)
        }

        buttonBack.setOnClickListener {
            if (pageNumber == 1){
                Snackbar.make(
                    view,
                    "no previous page",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                pageNumber = pageNumber - 1
                viewModel.getAnimeList(pageNumber)
            }
        }
    }
    private fun setupRecyclerView(){
        animeAdapter = AnimeAdapter()
        binding.recycleAnimeList.apply {
            adapter = animeAdapter
            layoutManager = GridLayoutManager(activity,2)

        }
    }

    private fun getLottieAnimation(){
        binding.lottieAnimation.setAnimation("animation.json")
        binding.lottieAnimation.playAnimation()
        binding.lottieAnimation.loop(true)
    }
}