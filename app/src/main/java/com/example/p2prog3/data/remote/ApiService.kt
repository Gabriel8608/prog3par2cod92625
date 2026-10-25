package com.example.p2prog3.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para definir las llamadas a la API.
 */
interface ApiService {

    @GET("sample/endpoint")
    suspend fun getItems(
        @Query("query") query: String? = null
    ): Response<List<SampleDto>>

    @GET("sample/endpoint/{id}")
    suspend fun getItemById(
        @Path("id") id: Int
    ): Response<SampleDto>
}
