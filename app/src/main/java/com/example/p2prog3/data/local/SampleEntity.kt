package com.example.p2prog3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para la base de datos local.
 */
@Entity(tableName = "sample_table")
data class SampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val isFavorite: Boolean = false
)
