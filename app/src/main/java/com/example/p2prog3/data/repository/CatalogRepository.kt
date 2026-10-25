package com.example.p2prog3.data.repository

import com.example.p2prog3.data.remote.dummyjson.DummyJsonApi
import com.example.p2prog3.data.remote.dummyjson.ProductDto
import com.example.p2prog3.domain.Product

class CatalogRepository(
    private val api: DummyJsonApi
) {
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.products.map { it.toDomain() }
                Result.success(products)
            } else {
                Result.failure(Exception("Error al obtener productos (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val response = api.searchProducts(query)
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.products.map { it.toDomain() }
                Result.success(products)
            } else {
                Result.failure(Exception("Error al buscar productos (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Int): Result<Product> {
        return try {
            val response = api.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al cargar detalle del producto ($id)"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ProductDto.toDomain() = Product(
        id = id,
        title = title,
        description = description,
        category = category,
        priceUsd = price,
        rating = rating ?: 0.0,
        stock = stock ?: 0,
        brand = brand,
        thumbnail = thumbnail
    )
}
