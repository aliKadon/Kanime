package com.example.animeapp2.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeapp2.AnimeApplication
import com.example.animeapp2.models.AnimeResponse
import com.example.animeapp2.models.AnimeDetailResponse
import com.example.animeapp2.models.Data
import com.example.animeapp2.repositories.AnimeRepository
import com.example.animeapp2.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class AnimeViewModel(
    app : Application,
    val animeRepository : AnimeRepository

) : AndroidViewModel(app) {

    val animeList : MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()
    var animeListPage = 1

    val animeSearch : MutableLiveData<Resource<AnimeResponse>> = MutableLiveData()
    var has_next_page : Boolean = true

    init {
        getAnimeList(animeListPage)
    }

    fun getAnimeList(page : Int) = viewModelScope.launch {
        safeGetAnimeList(page)

    }
    fun getSearchAnime(page : Int, title: String,status : String, genre : String, score : String) = viewModelScope.launch {
        safeGetAnimeSearch(page,title,status,genre,score)
    }

    fun getSearchAnimeByYear(page: Int, year : String) = viewModelScope.launch {
        safeGetAnimeSearchByYear(page, year)
    }


    private fun handleAnimeListResponse(response: Response<AnimeResponse>) : Resource<AnimeResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                    return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchAnimeResponse(response: Response<AnimeResponse>) : Resource<AnimeResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                has_next_page = resultResponse.has_next_page
                return Resource.Success(resultResponse)
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

    private suspend fun safeGetAnimeList(page : Int) {
        animeList.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = animeRepository.getAnimeList(page)
                animeList.postValue(handleAnimeListResponse(response))
            }else {
                animeList.postValue(Resource.Error("No internet connection"))
            }

        }catch (e : Throwable) {
            when(e) {
                is IOException -> animeList.postValue(Resource.Error("Network failure"))
                else -> animeList.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeGetAnimeSearch(page : Int, title: String,status : String, genre : String, score : String) {
        animeSearch.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response : Response<AnimeResponse> = animeRepository.getSearchAnime(page,title,status, genre, score)
                animeSearch.postValue(handleSearchAnimeResponse(response))

            }else {
                animeSearch.postValue(Resource.Error("No internet connection"))
            }

        }catch (e : Throwable) {
            when(e) {
                is IOException -> animeSearch.postValue(Resource.Error("Network failure"))
                else -> animeSearch.postValue(Resource.Error("No more pages"))
            }
        }
    }

    private suspend fun safeGetAnimeSearchByYear(page: Int, year: String){
        animeSearch.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response : Response<AnimeResponse> = animeRepository.getSearchAnimeByYear(page,year)
                animeSearch.postValue(handleSearchAnimeResponse(response))
            }else {
                animeSearch.postValue(Resource.Error("No internet connection"))
            }
        }catch (e : Throwable) {
            when(e) {
                is IOException -> animeSearch.postValue(Resource.Error("Network failure"))
                else -> animeSearch.postValue(Resource.Error("No more pages"))
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
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(TRANSPORT_VPN) -> true
                else -> false
            }
        }else {
            connectivityManager.activeNetworkInfo?.run {
                return  when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    TYPE_VPN -> true
                    else -> false
                }
            }
        }
        return true
    }
}