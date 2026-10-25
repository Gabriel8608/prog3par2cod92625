package com.example.p2prog3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2prog3.data.remote.RetrofitInstances
import com.example.p2prog3.data.repository.CatalogRepository
import com.example.p2prog3.data.repository.CurrencyRepository
import com.example.p2prog3.data.repository.FavoriteRepository
import com.example.p2prog3.domain.ExchangeRates
import com.example.p2prog3.domain.FavoriteProduct
import com.example.p2prog3.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductDetailUiState {
    object Loading : ProductDetailUiState
    data class Success(val product: Product) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}

sealed interface RatesUiState {
    object Idle : RatesUiState
    object Loading : RatesUiState
    data class Success(val rates: ExchangeRates) : RatesUiState
    data class Error(val message: String) : RatesUiState
}

class ProductDetailViewModel(
    private val catalogRepository: CatalogRepository = CatalogRepository(RetrofitInstances.dummyJsonApi),
    private val currencyRepository: CurrencyRepository = CurrencyRepository(RetrofitInstances.frankfurterApi),
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _ratesState = MutableStateFlow<RatesUiState>(RatesUiState.Idle)
    val ratesState: StateFlow<RatesUiState> = _ratesState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentProductId: Int = -1

    fun loadProduct(productId: Int) {
        currentProductId = productId
        _isFavorite.value = favoriteRepository.isFavorite(productId)
        _uiState.value = ProductDetailUiState.Loading
        viewModelScope.launch {
            catalogRepository.getProductById(productId).fold(
                onSuccess = { product ->
                    _uiState.value = ProductDetailUiState.Success(product)
                },
                onFailure = { error ->
                    _uiState.value = ProductDetailUiState.Error(
                        error.message ?: "No se pudo obtener la información del producto"
                    )
                }
            )
        }
    }

    fun convertPrice() {
        _ratesState.value = RatesUiState.Loading
        viewModelScope.launch {
            currencyRepository.getRates().fold(
                onSuccess = { rates ->
                    _ratesState.value = RatesUiState.Success(rates)
                },
                onFailure = { error ->
                    _ratesState.value = RatesUiState.Error(
                        error.message ?: "Error al obtener tipos de cambio"
                    )
                }
            )
        }
    }

    fun toggleFavorite(product: Product, note: String = "") {
        if (_isFavorite.value) {
            favoriteRepository.removeFavorite(product.id)
            _isFavorite.value = false
        } else {
            val fav = FavoriteProduct(
                id = product.id,
                title = product.title,
                priceUsd = product.priceUsd,
                thumbnail = product.thumbnail,
                note = note.ifBlank { "Producto favorito" }
            )
            favoriteRepository.addFavorite(fav)
            _isFavorite.value = true
        }
    }

    fun reintentar() {
        if (currentProductId != -1) {
            loadProduct(currentProductId)
        }
    }
}
