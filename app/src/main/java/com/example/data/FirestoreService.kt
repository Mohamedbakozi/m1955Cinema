package com.example.data

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private val firestore: FirebaseFirestore?
        get() {
            return try {
                val db = FirebaseFirestore.getInstance()
                db
            } catch (e: Exception) {
                Log.w("FirestoreService", "Firebase Firestore init status: ${e.message}")
                null
            }
        }

    private val moviesCollection = "movies"
    private val reviewsCollection = "reviews"

    /**
     * Real-time stream of all movies from Firebase Firestore.
     * Whenever any Admin adds or updates a movie, it emits the new list immediately to all users.
     */
    fun getMoviesRealtime(): Flow<List<Movie>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection(moviesCollection)
            .orderBy("addedTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreService", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val movies = snapshot.documents.mapNotNull { doc ->
                        try {
                            Movie(
                                id = doc.getLong("id") ?: doc.id.hashCode().toLong(),
                                titleEn = doc.getString("titleEn") ?: "",
                                titleKu = doc.getString("titleKu") ?: "",
                                overviewEn = doc.getString("overviewEn") ?: "",
                                overviewKu = doc.getString("overviewKu") ?: "",
                                category = doc.getString("category") ?: "M1955 Cinema",
                                categoryKu = doc.getString("categoryKu") ?: "سینەمای کوردی",
                                durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 120,
                                releaseYear = doc.getLong("releaseYear")?.toInt() ?: 2024,
                                imdbRating = (doc.getDouble("imdbRating") ?: 8.5).toFloat(),
                                quality = doc.getString("quality") ?: "4K UHD",
                                posterResName = doc.getString("posterResName") ?: "img_poster_kurdish",
                                posterUrl = doc.getString("posterUrl") ?: "",
                                bannerResName = doc.getString("bannerResName") ?: "img_hero_cinematic",
                                bannerUrl = doc.getString("bannerUrl") ?: "",
                                videoUrl = doc.getString("videoUrl") ?: "",
                                director = doc.getString("director") ?: "",
                                castMembers = doc.getString("castMembers") ?: "",
                                availableLanguages = doc.getString("availableLanguages") ?: "Kurdî (Badîni, Soranî), English",
                                isFeatured = doc.getBoolean("isFeatured") ?: false,
                                isTrending = doc.getBoolean("isTrending") ?: true,
                                isFavorite = false,
                                isUserAdded = doc.getBoolean("isUserAdded") ?: false,
                                addedTimestamp = doc.getLong("addedTimestamp") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            Log.e("FirestoreService", "Error mapping doc: ${e.message}")
                            null
                        }
                    }
                    trySend(movies)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Saves a new movie to Firebase Firestore.
     */
    suspend fun saveMovieToFirestore(movie: Movie): Boolean {
        val db = firestore ?: return false
        return try {
            val docId = if (movie.id > 0) movie.id.toString() else System.currentTimeMillis().toString()
            val movieMap = hashMapOf(
                "id" to (if (movie.id > 0) movie.id else System.currentTimeMillis()),
                "titleEn" to movie.titleEn,
                "titleKu" to movie.titleKu,
                "overviewEn" to movie.overviewEn,
                "overviewKu" to movie.overviewKu,
                "category" to movie.category,
                "categoryKu" to movie.categoryKu,
                "durationMinutes" to movie.durationMinutes,
                "releaseYear" to movie.releaseYear,
                "imdbRating" to movie.imdbRating.toDouble(),
                "quality" to movie.quality,
                "posterResName" to movie.posterResName,
                "posterUrl" to movie.posterUrl,
                "bannerResName" to movie.bannerResName,
                "bannerUrl" to movie.bannerUrl,
                "videoUrl" to movie.videoUrl,
                "director" to movie.director,
                "castMembers" to movie.castMembers,
                "availableLanguages" to movie.availableLanguages,
                "isFeatured" to movie.isFeatured,
                "isTrending" to movie.isTrending,
                "isUserAdded" to true,
                "addedTimestamp" to System.currentTimeMillis()
            )
            db.collection(moviesCollection).document(docId).set(movieMap).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error saving movie to Firestore: ${e.message}")
            false
        }
    }

    /**
     * Seeds initial movies to Firestore if collection is empty.
     */
    suspend fun seedFirestoreIfEmpty(initialMovies: List<Movie>) {
        val db = firestore ?: return
        try {
            val snapshot = db.collection(moviesCollection).limit(1).get().await()
            if (snapshot.isEmpty) {
                for (movie in initialMovies) {
                    saveMovieToFirestore(movie)
                }
            }
        } catch (e: Exception) {
            Log.w("FirestoreService", "Firestore seeding skipped: ${e.message}")
        }
    }

    /**
     * Delete movie from Firestore (Admin only)
     */
    suspend fun deleteMovie(movieId: Long): Boolean {
        val db = firestore ?: return false
        return try {
            db.collection(moviesCollection).document(movieId.toString()).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error deleting movie: ${e.message}")
            false
        }
    }
}
