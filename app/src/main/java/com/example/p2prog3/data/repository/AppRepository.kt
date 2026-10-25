package com.example.p2prog3.data.repository

import com.example.p2prog3.data.local.SampleDao
import com.example.p2prog3.data.local.SampleEntity
import com.example.p2prog3.data.remote.ApiService
import com.example.p2prog3.data.remote.SampleDto
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio central para orquestar la obtención de datos desde la API y la Base de Datos local.
 */
class AppRepository(
    private val apiService: ApiService,
    private val sampleDao: SampleDao
) {
    // Operaciones con la Base de Datos Local
    val localItems: Flow<List<SampleEntity>> = sampleDao.getAllItems()

    suspend fun saveToLocal(entity: SampleEntity) {
        sampleDao.insertItem(entity)
    }

    suspend fun removeFromLocal(entity: SampleEntity) {
        sampleDao.deleteItem(entity)
    }

    // Operaciones con la API Remota
    suspend fun fetchFromApi(): Result<List<SampleDto>> {
        return try {
            val response = apiService.getItems()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error de red: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
