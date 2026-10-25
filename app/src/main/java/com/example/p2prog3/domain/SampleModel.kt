package com.example.p2prog3.domain

/**
 * Modelo de dominio base.
 */
data class SampleModel(
    val id: Int,
    val title: String,
    val description: String,
    val isFavorite: Boolean = false
)
