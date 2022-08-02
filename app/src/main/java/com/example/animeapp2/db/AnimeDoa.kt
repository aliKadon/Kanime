package com.example.animeapp2.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.animeapp2.models.Data


@Dao
interface AnimeDoa {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: Data): Long

    @Query("SELECT * FROM databaseAnimeTable ")
    fun getAnimeList(): LiveData<List<Data>>

    @Query("SELECT EXISTS (SELECT 1 FROM databaseAnimeTable WHERE mal_id = :id)")
    fun isExists(id : Int) : Boolean

    @Delete
    suspend fun delete(document: Data)

}