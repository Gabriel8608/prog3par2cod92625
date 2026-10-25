package com.example.p2prog3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones de favoritos en Room.
 */
@Dao
interface FavoriteProductDao {

    @Query("SELECT * FROM favorite_products")
    fun getAllFavorites(): Flow<List<FavoriteProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteProductEntity)

    @Query("DELETE FROM favorite_products WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :id)")
    fun isFavoriteFlow(id: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}
