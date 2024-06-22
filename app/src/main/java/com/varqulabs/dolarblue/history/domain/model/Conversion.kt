package com.varqulabs.dolarblue.history.domain.model

import kotlinx.datetime.LocalDateTime

data class Conversion(
    val id: Int,
    val pesosBob: String,
    val pesosArg: String,
    val dollar: String,
    val date: LocalDateTime,
    val currentExchangeId: Int
)
