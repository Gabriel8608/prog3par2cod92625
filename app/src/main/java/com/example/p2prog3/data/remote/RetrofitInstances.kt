package com.example.p2prog3.data.remote

import com.example.p2prog3.data.remote.dummyjson.DummyJsonApi
import com.example.p2prog3.data.remote.frankfurter.FrankfurterApi
import com.example.p2prog3.data.remote.jsonplaceholder.JsonPlaceholderApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Instancias de Retrofit configuradas individualmente por baseUrl para cada una de las 3 APIs.
 */
object RetrofitInstances {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val dummyJsonApi: DummyJsonApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DummyJsonApi::class.java)
    }

    val frankfurterApi: FrankfurterApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.frankfurter.dev/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FrankfurterApi::class.java)
    }

    val jsonPlaceholderApi: JsonPlaceholderApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonPlaceholderApi::class.java)
    }
}
