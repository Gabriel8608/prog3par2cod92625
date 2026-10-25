package com.example.p2prog3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p2prog3.data.remote.RetrofitInstances
import com.example.p2prog3.data.repository.CatalogRepository
import com.example.p2prog3.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CatalogUiState {
    object Loading : CatalogUiState
    data class Success(val products: List<Product>) : CatalogUiState
    data class Error(val message: String) : CatalogUiState
}

class CatalogViewModel(
    private val repository: CatalogRepository = CatalogRepository(RetrofitInstances.dummyJsonApi)
) : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = CatalogUiState.Loading
        viewModelScope.launch {
            repository.getProducts().fold(
                onSuccess = { products ->
                    allProducts = products
                    filterProducts(_searchQuery.value)
                },
                onFailure = { error ->
                    _uiState.value = CatalogUiState.Error(
                        error.message ?: "Ocurrió un error al cargar el catálogo"
                    )
                }
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterProducts(query)
    }

    private fun filterProducts(query: String) {
        if (query.isBlank()) {
            _uiState.value = CatalogUiState.Success(allProducts)
        } else {
            val filtered = allProducts.filter { product ->
                product.title.contains(query, ignoreCase = true) ||
                        product.category.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }
            _uiState.value = CatalogUiState.Success(filtered)
        }
    }

    fun reintentar() {
        loadProducts()
    }
}
