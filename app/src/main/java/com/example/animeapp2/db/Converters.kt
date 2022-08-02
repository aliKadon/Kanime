package com.example.animeapp2.db

import android.util.Log
import androidx.room.TypeConverter
import com.example.animeapp2.models.Characters
import com.example.animeapp2.models.Genres

class Converters {

    @TypeConverter
    fun fromGenre(genres : MutableList<Genres>) : String {
        var list : MutableList<String>? = null
         for (genre in genres){
             val name = genre.name
             list?.add(name)
        }
        return "ali"
    }
    @TypeConverter
    fun toGenre(genre : String) : MutableList<Genres>? {
        var list = mutableListOf<Genres>()
        list?.add(Genres(1,"sd","fe","fe"))
        list.add(Genres(1,"sd","fe","fe"))
        return list
    }

    @TypeConverter
    fun fromCharacters(characters : MutableList<Characters>) : String {
        return "Ali"
    }

    @TypeConverter
    fun toCharacters(chracter : String) : MutableList<Characters> {
        var list = mutableListOf<Characters>()
        list.add(Characters("","sw","sdw"))
        list.add(Characters("","ud","ide"))
        return list
    }
}