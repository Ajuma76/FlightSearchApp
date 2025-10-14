package com.example.flightsearch.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "favorite",
    foreignKeys = [
        ForeignKey(
            entity = Airport::class,
            parentColumns = ["iata_code"],
            childColumns = ["departure_code"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Airport::class,
            parentColumns = ["iata_code"],
            childColumns = ["destination_code"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["departure_code", "destination_code"], unique = true),
        Index(value = ["departure_code"]),
        Index(value = ["destination_code"])
    ])

data class Favorite (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "departure_code")
    @NonNull
    val departureCode: String,

    @ColumnInfo(name = "destination_code")
    @NonNull
    val destinationCode: String
)

