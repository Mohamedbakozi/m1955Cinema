package com.example.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MovieRepository(
    private val movieDao: MovieDao,
    private val firestoreService: FirestoreService = FirestoreService()
) {

    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()
    val featuredMovies: Flow<List<Movie>> = movieDao.getFeaturedMovies()
    val trendingMovies: Flow<List<Movie>> = movieDao.getTrendingMovies()
    val favoriteMovies: Flow<List<Movie>> = movieDao.getFavoriteMovies()
    val continueWatching: Flow<List<Movie>> = movieDao.getContinueWatchingMovies()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Seed local Room database if empty
            if (movieDao.getMovieCount() == 0) {
                val initial = MovieDataSeeder.getInitialMovies()
                movieDao.insertMovies(initial)
                MovieDataSeeder.getInitialReviews().forEach {
                    movieDao.insertReview(it)
                }
                // Also attempt to seed Firestore
                firestoreService.seedFirestoreIfEmpty(initial)
            }

            // Real-time synchronization from Firebase Firestore
            try {
                firestoreService.getMoviesRealtime().collect { firestoreMovies ->
                    if (firestoreMovies.isNotEmpty()) {
                        for (remoteMovie in firestoreMovies) {
                            movieDao.insertMovie(remoteMovie)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("MovieRepository", "Realtime sync notice: ${e.message}")
            }
        }
    }

    fun getMovieById(id: Long): Flow<Movie?> = movieDao.getMovieById(id)

    fun getMoviesByCategory(category: String): Flow<List<Movie>> =
        movieDao.getMoviesByCategory(category)

    fun searchMovies(query: String): Flow<List<Movie>> =
        movieDao.searchMovies(query)

    fun getReviews(movieId: Long): Flow<List<MovieReview>> =
        movieDao.getReviewsForMovie(movieId)

    suspend fun addMovie(movie: Movie): Long {
        // Save to Firebase Firestore so all users get it in real-time
        firestoreService.saveMovieToFirestore(movie)
        // Also save to local Room for instant feedback
        return movieDao.insertMovie(movie)
    }

    suspend fun updateMovie(movie: Movie) {
        firestoreService.saveMovieToFirestore(movie)
        movieDao.updateMovie(movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        firestoreService.deleteMovie(movie.id)
        movieDao.deleteMovie(movie)
    }

    suspend fun toggleFavorite(id: Long, isFav: Boolean) =
        movieDao.updateFavoriteStatus(id, isFav)

    suspend fun updateProgress(id: Long, progressSec: Int, totalSec: Int) =
        movieDao.updateWatchProgress(id, progressSec, totalSec)

    suspend fun addReview(review: MovieReview): Long =
        movieDao.insertReview(review)
}
