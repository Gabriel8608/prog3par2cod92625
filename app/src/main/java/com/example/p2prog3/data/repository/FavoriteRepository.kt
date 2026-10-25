package com.example.p2prog3.data.repository

import com.example.p2prog3.data.local.FavoriteProductDao
import com.example.p2prog3.data.local.FavoriteProductEntity
import com.example.p2prog3.domain.FavoriteProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio de Favoritos respaldado por Room (FavoriteProductDao).
 */
class FavoriteRepository(
    private val dao: FavoriteProductDao
) {
    val favorites: Flow<List<FavoriteProduct>> = dao.getAllFavorites().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addFavorite(favorite: FavoriteProduct) {
        dao.insertFavorite(favorite.toEntity())
    }

    suspend fun removeFavorite(id: Int) {
        dao.deleteFavoriteById(id)
    }

    suspend fun isFavorite(id: Int): Boolean {
        return dao.isFavorite(id)
    }

    fun isFavoriteFlow(id: Int): Flow<Boolean> {
        return dao.isFavoriteFlow(id)
    }

    private fun FavoriteProductEntity.toDomain() = FavoriteProduct(
        id = id,
        title = title,
        priceUsd = priceUsd,
        thumbnail = thumbnail,
        note = note
    )

    private fun FavoriteProduct.toEntity() = FavoriteProductEntity(
        id = id,
        title = title,
        priceUsd = priceUsd,
        thumbnail = thumbnail,
        note = note
    )
}
