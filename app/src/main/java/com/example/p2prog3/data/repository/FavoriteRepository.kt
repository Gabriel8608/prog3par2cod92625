package com.example.p2prog3.data.repository

import com.example.p2prog3.domain.FavoriteProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio temporal en memoria para Favoritos (antes de conectar la base de datos local).
 */
class FavoriteRepository {
    private val _favorites = MutableStateFlow<List<FavoriteProduct>>(emptyList())
    val favorites: StateFlow<List<FavoriteProduct>> = _favorites.asStateFlow()

    fun addFavorite(favorite: FavoriteProduct) {
        val current = _favorites.value.toMutableList()
        if (current.none { it.id == favorite.id }) {
            current.add(favorite)
            _favorites.value = current
        }
    }

    fun removeFavorite(id: Int) {
        _favorites.value = _favorites.value.filter { it.id != id }
    }

    fun isFavorite(id: Int): Boolean {
        return _favorites.value.any { it.id == id }
    }
}
