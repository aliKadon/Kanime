package com.example.animeapp2.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.animeapp2.repositories.AnimeRepository

class AnimeViewHolderProviderFactory(
    val app : Application,
    val animeRepository : AnimeRepository

) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnimeViewModel(app, animeRepository) as T
    }
}