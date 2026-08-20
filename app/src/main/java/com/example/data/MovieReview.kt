package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_reviews")
data class MovieReview(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: Long,
    val userName: String,
    val rating: Float,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateDisplay: String = "Today"
)
