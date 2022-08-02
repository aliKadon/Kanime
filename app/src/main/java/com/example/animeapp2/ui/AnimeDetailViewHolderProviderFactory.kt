package com.example.animeapp2.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.animeapp2.models.Data
import com.example.animeapp2.repositories.AnimeRepository

class AnimeDetailViewHolderProviderFactory(
    val app : Application,
    val animeRepository: AnimeRepository,
    val data : Data
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnimeDetailViewModel(app, animeRepository, data) as T
    }
}