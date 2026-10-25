package com.example.p2prog3.domain

/**
 * Modelo de dominio para Producto (Catálogo y Detalle).
 */
data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val priceUsd: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val thumbnail: String
)
