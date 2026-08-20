package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY id DESC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isFeatured = 1 ORDER BY id DESC")
    fun getFeaturedMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isTrending = 1 ORDER BY imdbRating DESC")
    fun getTrendingMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE watchProgressSeconds > 0 ORDER BY addedTimestamp DESC")
    fun getContinueWatchingMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE category = :category OR categoryKu = :category ORDER BY imdbRating DESC")
    fun getMoviesByCategory(category: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: Long): Flow<Movie?>

    @Query("SELECT * FROM movies WHERE titleEn LIKE '%' || :query || '%' OR titleKu LIKE '%' || :query || '%' OR castMembers LIKE '%' || :query || '%' OR director LIKE '%' || :query || '%'")
    fun searchMovies(query: String): Flow<List<Movie>>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("UPDATE movies SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFav: Boolean)

    @Query("UPDATE movies SET watchProgressSeconds = :progressSec, totalDurationSeconds = :totalSec WHERE id = :id")
    suspend fun updateWatchProgress(id: Long, progressSec: Int, totalSec: Int)

    // Reviews
    @Query("SELECT * FROM movie_reviews WHERE movieId = :movieId ORDER BY timestamp DESC")
    fun getReviewsForMovie(movieId: Long): Flow<List<MovieReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: MovieReview): Long
}
