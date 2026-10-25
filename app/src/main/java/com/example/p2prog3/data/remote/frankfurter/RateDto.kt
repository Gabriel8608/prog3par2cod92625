package com.example.p2prog3.data.remote.frankfurter

import com.google.gson.annotations.SerializedName

data class RateDto(
    @SerializedName("date") val date: String,
    @SerializedName("base") val base: String,
    @SerializedName("quote") val quote: String,
    @SerializedName("rate") val rate: Double
)
