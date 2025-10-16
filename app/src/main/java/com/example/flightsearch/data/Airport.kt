package com.example.flightsearch.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "airport",
    indices = [Index(value = ["iata_code"], unique = true)]
    )
data class Airport(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "iata_code")
    @NonNull
    val iataCode: String,

    @NonNull
    val name: String,

    val passengers: Int,
)