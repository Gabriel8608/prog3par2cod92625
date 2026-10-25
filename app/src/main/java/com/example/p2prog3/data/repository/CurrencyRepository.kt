package com.example.p2prog3.data.repository

import com.example.p2prog3.data.remote.frankfurter.FrankfurterApi
import com.example.p2prog3.domain.ExchangeRates

class CurrencyRepository(
    private val api: FrankfurterApi
) {
    suspend fun getRates(): Result<ExchangeRates> {
        return try {
            val response = api.getRates(base = "USD", quotes = "EUR,BOB")
            if (response.isSuccessful && response.body() != null) {
                val rateList = response.body()!!
                val eurRate = rateList.find { it.quote.equals("EUR", ignoreCase = true) }?.rate ?: 0.0
                val bobRate = rateList.find { it.quote.equals("BOB", ignoreCase = true) }?.rate ?: 0.0
                val date = rateList.firstOrNull()?.date ?: ""
                Result.success(
                    ExchangeRates(
                        base = "USD",
                        eur = eurRate,
                        bob = bobRate,
                        date = date
                    )
                )
            } else {
                Result.failure(Exception("Error al consultar tipos de cambio (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
