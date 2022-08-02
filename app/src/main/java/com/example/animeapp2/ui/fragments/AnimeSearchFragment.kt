package com.example.animeapp2.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.Preference
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.size
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animeapp2.*
import com.example.animeapp2.adapters.AnimeAdapter
import com.example.animeapp2.adapters.AnimeSearchAdapter
import com.example.animeapp2.databinding.FragmentAnimeListBinding
import com.example.animeapp2.databinding.FragmentAnimeSearchBinding
import com.example.animeapp2.models.AnimeResponse
import com.example.animeapp2.ui.AnimeViewModel
import com.example.animeapp2.ui.MainActivity
import com.example.animeapp2.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll
import java.util.prefs.PreferenceChangeEvent
import kotlin.properties.Delegates

class AnimeSearchFragment() : Fragment(R.layout.fragment_anime_search) {

    private lateinit var binding: FragmentAnimeSearchBinding
    lateinit var viewModel: AnimeViewModel
    lateinit var animeSearchAdapter: AnimeSearchAdapter
    var page : Int = 1



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        binding = FragmentAnimeSearchBinding.bind(view)

        getFilteringInfo()
        setupRecyclerView()

        animeSearchAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("animeSer",it)
            }
            findNavController().navigate(
                R.id.action_animeSearchFragment2_to_mainActivityDetail2,
                bundle
            )
        }




        var job: Job? = null
        binding.editTextSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(1000L)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        var title = editable.toString()
                        viewModel.getSearchAnime(page, title, "","","")
                    }
                }
            }
        }


        viewModel.animeSearch.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { animeResponse ->
                        binding.lottieAnimationSearch.cancelAnimation()
                        binding.lottieAnimationSearch.visibility = View.GONE
                        animeSearchAdapter.differ.submitList(animeResponse.data)

                    }
                }
                is Resource.Error -> {
                    response.message.let { message ->
                        message?.let { Snackbar.make(view, it,Snackbar.LENGTH_LONG).show() }
                    }
                }
                is Resource.Loading -> {
                    getLottieAnimation()
                }
            }
        })

        binding.floatingActionButton.setOnClickListener{
            showDialogFilter()
        }

    }


    private fun setupRecyclerView(){
        animeSearchAdapter = AnimeSearchAdapter()
        binding.recycleAnimeSearch.apply {
            adapter = animeSearchAdapter
            layoutManager = GridLayoutManager(activity,3)
                addOnScrollListener(this@AnimeSearchFragment.scrollListener)
        }

    }
    private fun showDialogFilter(){
        FilterDialogFragment().show(childFragmentManager,"dialog_fragment_filter")
    }

    private fun getFilteringInfo(){
        binding.nextButton.visibility = View.GONE
        binding.backButton.visibility = View.GONE

        val pref : SharedPreferences = this.activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var editor = pref.edit()
        val args = this.arguments
        if (args != null) {

            //Remove the other Fragment
            parentFragmentManager?.beginTransaction()?.remove(this)?.commit()

            val inputGenre = args?.get("genre")
            val genre = (inputGenre ?: "") as String

            val inputScore = args?.get("score")
            val score = (inputScore ?: "") as String

            val inputStatus = args?.get("status")
            val status = (inputStatus ?: "") as String

            val inputYear = args?.get("year")
            val year = (inputYear ?: "") as String


            editor.putString("genre", genre)
            editor.putString("score", score)
            editor.putString("status", status)
            editor.putString("year", year)


            editor.commit()

            if (year != "-01-01"){
                viewModel.getSearchAnimeByYear(page,year)
            }else {
                viewModel.getSearchAnime(page,"",status, genre, score)
            }
        }
    }


    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            binding.nextButton.visibility = View.GONE
            binding.backButton.visibility = View.GONE
            val pref : SharedPreferences = activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)

            // get values from sharedPreferences
            val genrepref = pref.getString("genre","").toString()
            val scorePref = pref.getString("score","").toString()
            val titlePref = pref.getString("title", "").toString()
            val statusPref = pref.getString("status", "").toString()
            val yearPref = pref.getString("year","").toString()

            if (!recyclerView.canScrollVertically(1)) {
                if (viewModel.has_next_page) {

                    binding.nextButton.visibility = View.VISIBLE
                    binding.backButton.visibility = View.VISIBLE

                    binding.nextButton.setOnClickListener {
                        page += 1
                        if (yearPref != "-01-01"){
                            viewModel.getSearchAnimeByYear(page,yearPref)
                        }else {
                            viewModel.getSearchAnime(page, titlePref,statusPref,genrepref,scorePref)
                        }

                    }
                    binding.backButton.setOnClickListener {
                        if (page > 1) {
                            page -= 1
                            if (yearPref != "-01-01"){
                                viewModel.getSearchAnimeByYear(page,yearPref)
                            }else {
                                viewModel.getSearchAnime(page, titlePref,statusPref,genrepref,scorePref)
                            }
                        }else {
                            view?.let { it1 -> Snackbar.make(it1,"no previous page",Snackbar.LENGTH_SHORT).show() }
                        }
                    }
                    isScrolling = false
                }

                if (!viewModel.has_next_page) {
                    view?.let { Snackbar.make(it,"This is Last page",Snackbar.LENGTH_LONG).show() }
                    binding.backButton.visibility = View.VISIBLE
                    binding.nextButton.visibility = View.GONE
                    binding.backButton.setOnClickListener {
                        page -= 1
                        if (yearPref != "-01-01"){
                            viewModel.getSearchAnimeByYear(page,yearPref)
                        }else {
                            viewModel.getSearchAnime(page, titlePref,statusPref,genrepref,scorePref)
                        }

                    }
                }

            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true

            }
        }
    }

    private fun getLottieAnimation() {
        binding.lottieAnimationSearch.setAnimation("animation.json")
        binding.lottieAnimationSearch.playAnimation()
        binding.lottieAnimationSearch.loop(true)
    }
}

