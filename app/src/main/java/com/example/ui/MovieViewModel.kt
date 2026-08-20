package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AdminManager
import com.example.data.FirebaseStorageService
import com.example.data.ImageUploadState
import com.example.data.Movie
import com.example.data.MovieRepository
import com.example.data.MovieReview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPositionSeconds: Int = 0,
    val totalDurationSeconds: Int = 7200,
    val selectedQuality: String = "4K UHD",
    val selectedAudioTrack: String = "Kurdî Dubbing",
    val selectedSubtitle: String = "Kurdî (Badîni)",
    val playbackSpeed: Float = 1.0f,
    val volume: Float = 0.8f,
    val isMuted: Boolean = false,
    val isFullscreen: Boolean = false,
    val showControls: Boolean = true
)

data class AddMovieFormState(
    val titleEn: String = "",
    val titleKu: String = "",
    val overviewEn: String = "",
    val overviewKu: String = "",
    val category: String = "Kurdish Cinema",
    val categoryKu: String = "سینەمای کوردی",
    val releaseYear: String = "2024",
    val durationMinutes: String = "120",
    val imdbRating: String = "8.5",
    val quality: String = "4K UHD",
    val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    val posterResName: String = "img_poster_kurdish",
    val posterUrl: String = "",
    val bannerResName: String = "img_hero_cinematic",
    val bannerUrl: String = "",
    val director: String = "",
    val castMembers: String = "",
    val languages: String = "Kurdî (Badîni, Soranî), English Sub"
)

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _currentLanguage = MutableStateFlow(AppLanguage.KURDISH_BADINI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val isAdmin: StateFlow<Boolean> = AdminManager.isAdmin
    val adminName: StateFlow<String> = AdminManager.adminName

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMovieForDetail = MutableStateFlow<Movie?>(null)
    val selectedMovieForDetail: StateFlow<Movie?> = _selectedMovieForDetail.asStateFlow()

    private val _activePlayingMovie = MutableStateFlow<Movie?>(null)
    val activePlayingMovie: StateFlow<Movie?> = _activePlayingMovie.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _addMovieForm = MutableStateFlow(AddMovieFormState())
    val addMovieForm: StateFlow<AddMovieFormState> = _addMovieForm.asStateFlow()

    private val storageService = FirebaseStorageService()
    private val _imageUploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Idle)
    val imageUploadState: StateFlow<ImageUploadState> = _imageUploadState.asStateFlow()

    private val _activeMovieReviews = MutableStateFlow<List<MovieReview>>(emptyList())
    val activeMovieReviews: StateFlow<List<MovieReview>> = _activeMovieReviews.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    val allMovies: StateFlow<List<Movie>> = repository.allMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredMovies: StateFlow<List<Movie>> = repository.featuredMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingMovies: StateFlow<List<Movie>> = repository.trendingMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteMovies: StateFlow<List<Movie>> = repository.favoriteMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val continueWatching: StateFlow<List<Movie>> = repository.continueWatching
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMovies: StateFlow<List<Movie>> = combine(
        allMovies,
        _selectedCategory,
        _searchQuery
    ) { movies, category, query ->
        movies.filter { movie ->
            val matchesCategory = if (category == "All" || category == "هەمی") {
                true
            } else {
                movie.category.equals(category, ignoreCase = true) ||
                movie.categoryKu.contains(category, ignoreCase = true)
            }
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                movie.titleEn.contains(query, ignoreCase = true) ||
                movie.titleKu.contains(query, ignoreCase = true) ||
                movie.castMembers.contains(query, ignoreCase = true) ||
                movie.director.contains(query, ignoreCase = true) ||
                movie.category.contains(query, ignoreCase = true) ||
                movie.categoryKu.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openMovieDetail(movie: Movie) {
        _selectedMovieForDetail.value = movie
        loadReviewsForMovie(movie.id)
    }

    fun closeMovieDetail() {
        _selectedMovieForDetail.value = null
    }

    private fun loadReviewsForMovie(movieId: Long) {
        viewModelScope.launch {
            repository.getReviews(movieId).collect {
                _activeMovieReviews.value = it
            }
        }
    }

    fun startPlayingMovie(movie: Movie) {
        _activePlayingMovie.value = movie
        _playerState.value = PlayerState(
            isPlaying = true,
            currentPositionSeconds = movie.watchProgressSeconds,
            totalDurationSeconds = if (movie.totalDurationSeconds > 0) movie.totalDurationSeconds else movie.durationMinutes * 60,
            selectedQuality = movie.quality
        )
    }

    fun closePlayer() {
        _activePlayingMovie.value?.let { movie ->
            val curPos = _playerState.value.currentPositionSeconds
            val totSec = _playerState.value.totalDurationSeconds
            viewModelScope.launch {
                repository.updateProgress(movie.id, curPos, totSec)
            }
        }
        _activePlayingMovie.value = null
        _playerState.value = _playerState.value.copy(isPlaying = false)
    }

    fun togglePlayPause() {
        _playerState.value = _playerState.value.copy(isPlaying = !_playerState.value.isPlaying)
    }

    fun seekTo(seconds: Int) {
        _playerState.value = _playerState.value.copy(
            currentPositionSeconds = seconds.coerceIn(0, _playerState.value.totalDurationSeconds)
        )
    }

    fun skipForward(seconds: Int = 10) {
        seekTo(_playerState.value.currentPositionSeconds + seconds)
    }

    fun skipBackward(seconds: Int = 10) {
        seekTo(_playerState.value.currentPositionSeconds - seconds)
    }

    fun setQuality(quality: String) {
        _playerState.value = _playerState.value.copy(selectedQuality = quality)
    }

    fun setAudioTrack(track: String) {
        _playerState.value = _playerState.value.copy(selectedAudioTrack = track)
    }

    fun setSubtitle(subtitle: String) {
        _playerState.value = _playerState.value.copy(selectedSubtitle = subtitle)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
    }

    fun toggleMute() {
        _playerState.value = _playerState.value.copy(isMuted = !_playerState.value.isMuted)
    }

    fun toggleFavorite(movie: Movie) {
        val newStatus = !movie.isFavorite
        viewModelScope.launch {
            repository.toggleFavorite(movie.id, newStatus)
            if (_selectedMovieForDetail.value?.id == movie.id) {
                _selectedMovieForDetail.value = _selectedMovieForDetail.value?.copy(isFavorite = newStatus)
            }
            val msg = if (newStatus) {
                if (_currentLanguage.value == AppLanguage.ENGLISH) "Added to your Watchlist!" else "هاتە زێدەکرن بۆ لیستا تە!"
            } else {
                if (_currentLanguage.value == AppLanguage.ENGLISH) "Removed from Watchlist" else "ژ لیستا تە هاتە لادان"
            }
            _snackBarMessage.emit(msg)
        }
    }

    fun updateAddMovieForm(update: (AddMovieFormState) -> AddMovieFormState) {
        _addMovieForm.value = update(_addMovieForm.value)
    }

    fun uploadPoster(uri: Uri, context: Context) {
        viewModelScope.launch {
            storageService.uploadPosterImage(uri, context).collect { state ->
                _imageUploadState.value = state
                when (state) {
                    is ImageUploadState.Success -> {
                        // Set uploaded download URL to posterUrl and bannerUrl
                        _addMovieForm.value = _addMovieForm.value.copy(
                            posterUrl = state.downloadUrl,
                            bannerUrl = state.downloadUrl,
                            posterResName = "" // Use custom url instead of preset
                        )
                        val msg = if (_currentLanguage.value == AppLanguage.ENGLISH)
                            "Poster uploaded successfully to Firebase Storage!"
                        else
                            "پووستەر ب سەرکەفتیانە د Firebase Storage دا هاتە بارکرن!"
                        _snackBarMessage.emit(msg)
                    }
                    is ImageUploadState.Error -> {
                        val msg = if (_currentLanguage.value == AppLanguage.ENGLISH)
                            "Upload failed: ${state.message}"
                        else
                            "بارکردن سەرکەفتوو نەبوو: ${state.message}"
                        _snackBarMessage.emit(msg)
                    }
                    else -> {}
                }
            }
        }
    }

    fun resetImageUploadState() {
        _imageUploadState.value = ImageUploadState.Idle
    }

    fun saveNewMovie(onSuccess: () -> Unit) {
        val form = _addMovieForm.value
        val titleEn = form.titleEn.trim().ifEmpty { form.titleKu.trim() }
        val titleKu = form.titleKu.trim().ifEmpty { form.titleEn.trim() }

        if (titleEn.isEmpty() && titleKu.isEmpty()) {
            viewModelScope.launch {
                val msg = if (_currentLanguage.value == AppLanguage.ENGLISH) "Please enter a movie title!" else "تکایە ناڤێ فلمی بنڤیسە!"
                _snackBarMessage.emit(msg)
            }
            return
        }

        val year = form.releaseYear.toIntOrNull() ?: 2024
        val duration = form.durationMinutes.toIntOrNull() ?: 110
        val rating = form.imdbRating.toFloatOrNull() ?: 8.0f

        val newMovie = Movie(
            titleEn = titleEn,
            titleKu = titleKu,
            overviewEn = form.overviewEn.ifEmpty { form.overviewKu },
            overviewKu = form.overviewKu.ifEmpty { form.overviewEn },
            category = form.category,
            categoryKu = form.categoryKu,
            durationMinutes = duration,
            releaseYear = year,
            imdbRating = rating,
            quality = form.quality,
            posterResName = form.posterResName,
            posterUrl = form.posterUrl,
            bannerResName = form.bannerResName,
            bannerUrl = form.bannerUrl.ifEmpty { form.posterUrl },
            videoUrl = form.videoUrl.ifEmpty { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
            director = form.director.ifEmpty { "Kurdish Director" },
            castMembers = form.castMembers.ifEmpty { "Star Cast" },
            availableLanguages = form.languages,
            isFeatured = false,
            isTrending = true,
            isFavorite = false,
            isUserAdded = true,
            totalDurationSeconds = duration * 60,
            addedTimestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.addMovie(newMovie)
            _addMovieForm.value = AddMovieFormState() // Reset
            val successMsg = if (_currentLanguage.value == AppLanguage.ENGLISH)
                "Movie published successfully! Everyone can stream and watch it now."
            else
                "فلم هاتە زێدەکرن ب سەرکەفتیانە! نوکە هەمی کەس دشێن تەماشە بکەن."
            _snackBarMessage.emit(successMsg)
            _selectedTab.value = 0 // Go to Home
            onSuccess()
        }
    }

    fun loginAdmin(pin: String): Boolean {
        val success = AdminManager.verifyAndLogin(pin)
        viewModelScope.launch {
            val msg = if (success) {
                if (_currentLanguage.value == AppLanguage.ENGLISH) "Admin Mode Activated! You can now add & manage movies."
                else "دەسەڵاتا ئەدمینی کارا بوو! نوکە دشێی فلمان زێدە بکەی و ڕێکبێخی."
            } else {
                if (_currentLanguage.value == AppLanguage.ENGLISH) "Incorrect Admin PIN! Try 1955."
                else "پینا ئەدمینی شاشە! تاقی بکە: 1955"
            }
            _snackBarMessage.emit(msg)
        }
        return success
    }

    fun logoutAdmin() {
        AdminManager.logout()
        _selectedTab.value = 0 // Go back to Home
        viewModelScope.launch {
            val msg = if (_currentLanguage.value == AppLanguage.ENGLISH) "Logged out from Admin Mode."
            else "ژ دەسەڵاتا ئەدمینی هاتە دەرکەفتن."
            _snackBarMessage.emit(msg)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
            if (_selectedMovieForDetail.value?.id == movie.id) {
                _selectedMovieForDetail.value = null
            }
            val msg = if (_currentLanguage.value == AppLanguage.ENGLISH) "Movie deleted."
            else "فلم هاتە ژێبرن."
            _snackBarMessage.emit(msg)
        }
    }

    fun submitReview(movieId: Long, userName: String, rating: Float, comment: String) {
        if (comment.isBlank()) return
        val user = userName.trim().ifEmpty { if (_currentLanguage.value == AppLanguage.ENGLISH) "Cinema Fan" else "بینەرێ فلمی" }
        val review = MovieReview(
            movieId = movieId,
            userName = user,
            rating = rating,
            comment = comment.trim(),
            dateDisplay = if (_currentLanguage.value == AppLanguage.ENGLISH) "Just now" else "نوکە"
        )
        viewModelScope.launch {
            repository.addReview(review)
            val msg = if (_currentLanguage.value == AppLanguage.ENGLISH) "Thank you for your review!" else "سوپاس بۆ بۆچوونا تە یا بەڕێز!"
            _snackBarMessage.emit(msg)
        }
    }
}

class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
