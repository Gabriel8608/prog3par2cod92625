package com.example.p2prog3.domain

/**
 * Modelo de dominio para tipos de cambio (Frankfurter API).
 */
data class ExchangeRates(
    val base: String,   // "USD"
    val eur: Double,    // tasa USD -> EUR
    val bob: Double,    // tasa USD -> BOB
    val date: String    // fecha de la cotización
)
