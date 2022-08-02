package com.example.animeapp2.models

data class AnimeResponse(
    val message : String,
    val data : MutableList<Data>,
    val has_next_page : Boolean
)