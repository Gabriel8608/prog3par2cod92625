package com.example.p2prog3.domain

/**
 * Modelo de dominio para Producto Favorito.
 */
data class FavoriteProduct(
    val id: Int,
    val title: String,
    val priceUsd: Double,
    val thumbnail: String,
    val note: String = ""
)
