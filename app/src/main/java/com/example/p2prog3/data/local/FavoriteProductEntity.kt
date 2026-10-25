package com.example.p2prog3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para productos favoritos.
 * Clave primaria 'id' NO autogenerada (reutiliza el id del producto original).
 */
@Entity(tableName = "favorite_products")
data class FavoriteProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val priceUsd: Double,
    val thumbnail: String,
    val note: String = ""
)
