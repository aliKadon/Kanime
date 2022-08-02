package com.example.animeapp2.api

import com.example.animeapp2.utils.Constants.Companion.ACCESS_API_KEY
import com.example.animeapp2.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.internal.addHeaderLenient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .connectTimeout(1,TimeUnit.MINUTES)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(15,TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor{ chain ->  
                    val request = chain.request().newBuilder()
                        .addHeader("access_API_KEY", ACCESS_API_KEY)
                    chain.proceed(request.build())
                }
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }

        val api by lazy {
            retrofit.create(AnimeAPI::class.java)
        }
    }
}