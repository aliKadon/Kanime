package com.example.animeapp2.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.animeapp2.ui.MainActivityDetail
import com.example.animeapp2.R
import com.example.animeapp2.utils.Resource
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.animeapp2.databinding.FragmentAnimeDetailBinding
import com.example.animeapp2.models.AnimeDetailResponse
import com.example.animeapp2.models.Data
import com.example.animeapp2.ui.AnimeDetailViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.combine
import okhttp3.internal.wait


class AnimeDetailFragment: Fragment(R.layout.fragment_anime_detail) {

    private lateinit var binding: FragmentAnimeDetailBinding
    lateinit var viewModel: AnimeDetailViewModel
    var bit : Bitmap? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivityDetail).viewModel

        binding = FragmentAnimeDetailBinding.bind(view)

        val pref : SharedPreferences = this.activity!!.getSharedPreferences("prefID",Context.MODE_PRIVATE)
        val dataString = pref.getString("dataString","")

        val gson = Gson()
        val data = gson.fromJson(dataString,Data::class.java)

        if (data.mal_id?.let { viewModel.existsItem(it) } == true){
            binding.nonFavoriteButton.visibility = View.GONE
            binding.favoriteButton.visibility = View.VISIBLE
        }


        viewModel.animeDetail.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { animeDetailResponse ->


                        lifecycleScope.launch {
                            bit = getBitmap(animeDetailResponse.data.images)
                            val drawableBitmap : BitmapDrawable = BitmapDrawable(resources,bit)
                            binding.ll.background = drawableBitmap
                        }

                        runBlocking {
                            getDescriptionItem(animeDetailResponse)
                            getYoutubeTrailer(animeDetailResponse.data)
                        }

                        for (genre in animeDetailResponse.data.genres!!){
                            addChip(genre.name,binding.chipsGroupGenreDetail,true,false,false)
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                }
            }
        })

        binding.nonFavoriteButton.setOnClickListener {
            binding.nonFavoriteButton.visibility = View.GONE
            binding.favoriteButton.visibility = View.VISIBLE

            viewModel.saveAnime(data)
        }

        binding.favoriteButton.setOnClickListener {
            binding.favoriteButton.visibility = View.GONE
            binding.nonFavoriteButton.visibility = View.VISIBLE

            viewModel.deleteAnime(data)

        }


    }

    private fun addChip(lable : String, view: ChipGroup, isHorizontal : Boolean, isSengleSelection : Boolean, isClickable : Boolean) {
        var chip = LayoutInflater.from(context).inflate(R.layout.chips_genre,null) as Chip
        chip.id = View.generateViewId()
        chip.text = lable
        chip.isClickable = isClickable
        chip.isFocusable = true
        view.isSingleSelection = isSengleSelection
        view.isSingleLine = isHorizontal
        view.addView(chip)

    }

    @SuppressLint("SetTextI18n")
    private suspend fun getDescriptionItem(animeDetailResponse : AnimeDetailResponse){
        coroutineScope {
            Glide.with(context!!.applicationContext).load(animeDetailResponse.data.images).into(binding.imageViewImageDetail)
            binding.textViewTitleDetail.text = animeDetailResponse.data.title
            if (animeDetailResponse.data.synopsis != null) {
                binding.expandTextView.setText(animeDetailResponse.data.synopsis)
            }else {
                binding.expandTextView.setText("Sorry...There is no description")
            }
            binding.textViewSeasonYearDetail.text = animeDetailResponse.data.aired
            binding.ratingBarDetail.rating = animeDetailResponse.data.score?.div(2) ?: 0f
            if (animeDetailResponse.data.score != null) {
                binding.ratingTextDetail.text = animeDetailResponse.data.score.toString()
            }else {
                binding.ratingTextDetail.text = "0.0"
            }
            if (animeDetailResponse.data.episodes != null){
                binding.textViewEpisodeNumber.text = "Episode : ${animeDetailResponse.data.episodes}"
            }else {
                binding.textViewEpisodeNumber.text = "Episode : 0"
            }
            binding.textViewDuration.text = animeDetailResponse.data.duration
            binding.textViewType.text = "Type : "+ animeDetailResponse.data.type
            binding.textViewStatus.text = animeDetailResponse.data.status
        }



    }

    private suspend fun getYoutubeTrailer(data : Data){
        coroutineScope {
            async {
                lifecycle.addObserver(binding.youtubeView)
                binding.youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
                    override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                        super.onReady(youTubePlayer)
                        if (data.trailer != null){
                            if (binding.youtubeView != null) {
                            val defaultPlayerUiController = DefaultPlayerUiController(binding.youtubeView,youTubePlayer)
                                binding.youtubeView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                            }else {
                                view?.let {
                                    Snackbar.make(
                                        it,"please reload the video",Snackbar.LENGTH_LONG
                                    ).setAction("reload",View.OnClickListener {
                                        val defaultPlayerUiController = DefaultPlayerUiController(binding.youtubeView,youTubePlayer)
                                        binding.youtubeView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                                    }).show()
                                }
                            }
//                            val videoId : String = data.trailer.substringAfter("embed/")
//                            val videoIDBefore : String = videoId.substringBefore("?")
//                            Log.e("AnimeDetailFragment", "Error : $videoIDBefore")
                            youTubePlayer.loadVideo(data.trailer,0f)
                            youTubePlayer.pause()
                        }else {
                            if (binding.textViewTrailerStatus != null) {
                                binding.textViewTrailerStatus.text = "Sorry... No Trailer"
                                binding.textViewTrailerStatus.visibility = View.VISIBLE
                            }
                        }
                    }
                })
            }

        }


    }

    private suspend fun getBitmap(url : String?) : Bitmap? {
        val loader : ImageLoader = ImageLoader(requireContext())
        val request : ImageRequest = ImageRequest.Builder(requireContext())
            .data(url)
            .build()

        val result: Drawable = (loader.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap

    }
}
