package com.example.p2prog3.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object) para mapear las respuestas de la API.
 */
data class SampleDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?
)
