package com.example.animeapp2.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.animeapp2.models.Data

@Database(
    entities = [Data::class],
    version = 8
 )
@TypeConverters(Converters::class)
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun getAnimeListDao(): AnimeDoa



    companion object {
        @Volatile
        private var instance: AnimeDatabase? = null
        private val LOCk = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCk) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AnimeDatabase::class.java,
                "anime_db.db"
            ).fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build().also { instance = it }
        }
    }
 }