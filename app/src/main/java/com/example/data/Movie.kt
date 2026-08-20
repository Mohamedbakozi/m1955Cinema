package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleEn: String,
    val titleKu: String,
    val overviewEn: String,
    val overviewKu: String,
    val category: String, // "Movies, "Action", "Drama", "Animation", "Subtitle", "film cartoon", "Drama kurdi", "Thriller"
    val categoryKu: String,
    val durationMinutes: Int = 115,
    val releaseYear: Int = 2024,
    val imdbRating: Float = 8.5f,
    val quality: String = "4K UHD", // "4K UHD", "1080p FHD", "HDR10"
    val posterResName: String = "img_poster_kurdish", // drawable name or preset
    val posterUrl: String = "",
    val bannerResName: String = "img_hero_cinematic",
    val bannerUrl: String = "",
    val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    val director: String = "",
    val castMembers: String = "",
    val availableLanguages: String = "Kurdî (Badîni, Soranî), English Sub",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isFavorite: Boolean = false,
    val watchProgressSeconds: Int = 0,
    val totalDurationSeconds: Int = 7200,
    val isUserAdded: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis()
)
