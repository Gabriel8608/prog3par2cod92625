package com.example.p2prog3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones en la base de datos local.
 */
@Dao
interface SampleDao {

    @Query("SELECT * FROM sample_table")
    fun getAllItems(): Flow<List<SampleEntity>>

    @Query("SELECT * FROM sample_table WHERE id = :id")
    suspend fun getItemById(id: Int): SampleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SampleEntity)

    @Update
    suspend fun updateItem(item: SampleEntity)

    @Delete
    suspend fun deleteItem(item: SampleEntity)

    @Query("DELETE FROM sample_table")
    suspend fun clearAll()
}
