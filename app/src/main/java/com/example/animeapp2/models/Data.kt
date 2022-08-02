package com.example.animeapp2.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    tableName = "databaseAnimeTable"
)
data class Data(
    val title : String?,
    val year : String?,
    val images : String?,
    val trailer : String?,
    val episodes : Int?,
    val status : String?,
    val aired : String?,
    val duration : String?,
    val rating : String?,
    val synopsis : String?,
    val genres : MutableList<Genres>?,
    val name : String?,
    val type : String?,
    val role : String?,
    @PrimaryKey(autoGenerate = false)
    val mal_id : Int?,
    val score : Float?,
    val characters : MutableList<Characters>?
) : Serializable
