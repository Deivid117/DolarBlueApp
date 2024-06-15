package com.varqulabs.dolarblue.core.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.varqulabs.dolarblue.core.data.local.database.converters.LocalDateTimeConverter
import com.varqulabs.dolarblue.history.data.local.database.dao.ConversionDao
import com.varqulabs.dolarblue.history.data.local.database.dao.ConversionHistoryDao
import com.varqulabs.dolarblue.history.data.local.database.dao.CurrentExchangeRateDao
import com.varqulabs.dolarblue.history.data.local.database.entities.ConversionEntity
import com.varqulabs.dolarblue.history.data.local.database.entities.CurrentExchangeRateEntity

@Database(
    entities = [
        ConversionEntity::class,
        CurrentExchangeRateEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class DBDollarBlueApp : RoomDatabase() {

    abstract fun conversionDao(): ConversionDao
    abstract fun currentExchangeRateDao(): CurrentExchangeRateDao
    abstract fun conversionHistoryDao(): ConversionHistoryDao

    companion object {
        @JvmStatic
        fun newInstance(context: Context): DBDollarBlueApp {
            return Room
                .databaseBuilder(context, DBDollarBlueApp::class.java, "DBDollarBlue")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
