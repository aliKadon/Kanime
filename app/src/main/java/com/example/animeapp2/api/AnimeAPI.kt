package com.example.animeapp2.api

import com.example.animeapp2.models.AnimeResponse
import com.example.animeapp2.models.AnimeDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeAPI {

    @GET("/v1/anime-list/")
    suspend fun getAnimeList(
        @Query("page")
        animePage : Int,
    ): Response<AnimeResponse>

    @GET("/v1/anime_search/")
    suspend fun getSearchAnime(
        @Query("page")
        animePage: Int,
        @Query("title")
        animeTitle: String,
        @Query("status")
        animeStatus :String,
        @Query("genres")
        animeGenre : String,
//        @Query("year")
//        animeYear : String,
        @Query("score")
        animeScore : String
    ): Response<AnimeResponse>

    @GET("/v1/anime_detail/{id}")
    suspend fun getAnimeDetail(
        @Path("id")
        animeID : Int
    ) : Response<AnimeDetailResponse>

    @GET("/v1/anime_search_by_year")
    suspend fun getSearchByYear(
        @Query("page")
        animePage: Int,
        @Query("year")
        animeYear : String
    ) : Response<AnimeResponse>
}