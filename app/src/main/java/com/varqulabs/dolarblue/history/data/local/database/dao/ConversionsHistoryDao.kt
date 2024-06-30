package com.varqulabs.dolarblue.history.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.varqulabs.dolarblue.calculator.data.local.database.entities.ConversionEntity
import com.varqulabs.dolarblue.history.data.local.database.entities.relations.ConversionsHistoryRelation
import com.varqulabs.dolarblue.history.data.local.database.entities.relations.ConversionsWithCurrentExchangeRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionsHistoryDao {

    @Transaction
    @Query("SELECT * FROM current_exchange_rate_table")
    fun getConversionsHistoryFlow(): Flow<List<ConversionsHistoryRelation>>

    @Transaction
    @Query("SELECT * FROM current_exchange_rate_table")
    fun getConversionsHistory(): List<ConversionsHistoryRelation>

    @Update
    suspend fun updateConversion(conversionEntity: ConversionEntity)

    @Transaction
    @Query("""
        SELECT conversion_table.* FROM current_exchange_rate_table 
        JOIN conversion_table ON current_exchange_rate_table.id = conversion_table.currentExchangeId 
        WHERE conversion_table.isFavorite = 1
    """)
    fun getFavoriteConversionsHistory(): Flow<List<ConversionsWithCurrentExchangeRelation>>

    @Transaction
    @Query("""
        SELECT conversion_table.*
        FROM current_exchange_rate_table
        JOIN conversion_table ON current_exchange_rate_table.id = conversion_table.currentExchangeId
        WHERE conversion_table.name LIKE '%' || :querySearch || '%'
        OR conversion_table.date LIKE '%' || :querySearch || '%'
        OR conversion_table.pesosBob LIKE '%' || :querySearch || '%'
     """)
    fun searchConversionsHistoryByQuery(querySearch: String): Flow<List<ConversionsWithCurrentExchangeRelation>>
}