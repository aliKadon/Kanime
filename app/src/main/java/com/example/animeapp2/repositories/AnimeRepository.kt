package com.example.animeapp2.repositories

import com.example.animeapp2.api.RetrofitInstance
import com.example.animeapp2.db.AnimeDatabase
import com.example.animeapp2.models.Data

class AnimeRepository(
    val db : AnimeDatabase
) {

    suspend fun getAnimeList(page : Int) =
        RetrofitInstance.api.getAnimeList(page)

    suspend fun getSearchAnime(page: Int, title : String,status : String, genre : String, score : String) =
        RetrofitInstance.api.getSearchAnime(page, title,status, genre, score)

    suspend fun getSearchAnimeByYear(page: Int, year: String) =
        RetrofitInstance.api.getSearchByYear(page,year)

    suspend fun getAnimeDetails(animeID : Int) =
        RetrofitInstance.api.getAnimeDetail(animeID)

    suspend fun insert(data: Data) = db.getAnimeListDao().insert(data)

    fun getSavedAnime() = db.getAnimeListDao().getAnimeList()

    suspend fun deleteAnime(data : Data) = db.getAnimeListDao().delete(data)

    fun existsItem(animeID: Int) = db.getAnimeListDao().isExists(animeID)

}

