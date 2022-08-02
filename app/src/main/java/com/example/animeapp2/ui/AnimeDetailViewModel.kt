package com.example.animeapp2.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.example.animeapp2.AnimeApplication
import com.example.animeapp2.models.AnimeDetailResponse
import com.example.animeapp2.models.Data
import com.example.animeapp2.repositories.AnimeRepository
import com.example.animeapp2.utils.Resource
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.coroutineContext

class AnimeDetailViewModel(
    app : Application,
    val animeRepository: AnimeRepository,
    val data : Data
) : AndroidViewModel(app) {
    val animeDetail : MutableLiveData<Resource<AnimeDetailResponse>> = MutableLiveData()

    init {
        data.mal_id?.let { getAnimeDetails(it) }
    }


    fun getAnimeDetails(animeID : Int) = viewModelScope.launch {
        safeGetAnimeDetail(animeID)
    }

    private fun handleAnimeDetailsResponse(response: Response<AnimeDetailResponse>) : Resource<AnimeDetailResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultresponse ->
                return Resource.Success(resultresponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun saveAnime(data: Data) = viewModelScope.launch {
        animeRepository.insert(data)
    }

    fun getSavedAnime() = animeRepository.getSavedAnime()

    fun deleteAnime(data : Data) = viewModelScope.launch {
        animeRepository.deleteAnime(data)
    }

    fun existsItem(animeID: Int) = animeRepository.existsItem(animeID)

    private suspend fun safeGetAnimeDetail(animeID : Int) {
        animeDetail.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = animeRepository.getAnimeDetails(animeID)
                animeDetail.postValue(handleAnimeDetailsResponse(response))
            }else {
                animeDetail.postValue(Resource.Error("No internet connection"))
            }

        }catch (e : Throwable) {
            when(e) {
                is IOException -> animeDetail.postValue(Resource.Error("Sorry... No details yet"))
                else -> animeDetail.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection() : Boolean {
        val connectivityManager = getApplication<AnimeApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        }else {
            connectivityManager.activeNetworkInfo?.run {
                return  when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    ConnectivityManager.TYPE_VPN -> true
                    else -> false
                }
            }
        }
        return false
    }

}