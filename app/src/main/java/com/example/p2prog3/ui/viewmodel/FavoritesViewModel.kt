package com.example.p2prog3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2prog3.data.repository.FavoriteRepository
import com.example.p2prog3.domain.FavoriteProduct
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteProduct>> = favoriteRepository.favorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun removeFavorite(id: Int) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(id)
        }
    }
}
