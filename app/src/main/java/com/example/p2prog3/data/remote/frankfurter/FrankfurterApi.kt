package com.example.p2prog3.data.remote.frankfurter

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FrankfurterApi {
    @GET("v2/rates")
    suspend fun getRates(
        @Query("base") base: String = "USD",
        @Query("quotes") quotes: String = "EUR,BOB"
    ): Response<List<RateDto>>
}
