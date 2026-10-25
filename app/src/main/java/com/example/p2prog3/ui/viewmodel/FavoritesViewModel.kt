package com.example.p2prog3.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.p2prog3.data.repository.FavoriteRepository
import com.example.p2prog3.domain.FavoriteProduct
import kotlinx.coroutines.flow.StateFlow

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteProduct>> = favoriteRepository.favorites

    fun removeFavorite(id: Int) {
        favoriteRepository.removeFavorite(id)
    }
}
