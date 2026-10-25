package com.example.p2prog3.domain

/**
 * Modelo de dominio para Publicación (JSONPlaceholder API).
 */
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
