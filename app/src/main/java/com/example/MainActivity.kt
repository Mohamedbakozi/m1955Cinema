package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MovieDatabase
import com.example.data.MovieRepository
import com.example.ui.AppLanguage
import com.example.ui.MovieViewModel
import com.example.ui.MovieViewModelFactory
import com.example.ui.Strings
import com.example.ui.components.VideoPlayerView
import com.example.ui.screens.AddMovieScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MovieDetailSheet
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.CinemaDarkBg
import com.example.ui.theme.CinemaDarkCard
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaTextSecondary
import com.example.ui.theme.M1955CinemaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val database = remember { MovieDatabase.getDatabase(context) }
            val repository = remember { MovieRepository(database.movieDao()) }
            val viewModel: MovieViewModel = viewModel(
                factory = MovieViewModelFactory(repository)
            )

            M1955CinemaTheme(darkTheme = true) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavTabItem(
    val routeIndex: Int,
    val title: String,
    val icon: ImageVector,
    val isAddAction: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MovieViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val allMovies by viewModel.allMovies.collectAsStateWithLifecycle()
    val featuredMovies by viewModel.featuredMovies.collectAsStateWithLifecycle()
    val trendingMovies by viewModel.trendingMovies.collectAsStateWithLifecycle()
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()
    val continueWatching by viewModel.continueWatching.collectAsStateWithLifecycle()
    val filteredMovies by viewModel.filteredMovies.collectAsStateWithLifecycle()

    val selectedMovieForDetail by viewModel.selectedMovieForDetail.collectAsStateWithLifecycle()
    val activePlayingMovie by viewModel.activePlayingMovie.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val addMovieForm by viewModel.addMovieForm.collectAsStateWithLifecycle()
    val uploadState by viewModel.imageUploadState.collectAsStateWithLifecycle()
    val activeReviews by viewModel.activeMovieReviews.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var dialogPinInput by remember { mutableStateOf("") }
    var dialogPinError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.snackBarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle back button when player or detail is open
    BackHandler(enabled = activePlayingMovie != null || selectedMovieForDetail != null) {
        if (activePlayingMovie != null) {
            viewModel.closePlayer()
        } else if (selectedMovieForDetail != null) {
            viewModel.closeMovieDetail()
        }
    }

    // Admin Quick Login Dialog
    if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminLoginDialog = false
                dialogPinInput = ""
                dialogPinError = false
            },
            containerColor = CinemaDarkCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = CinemaGold,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Admin Authorization" else "دەسەڵاتا ئەدمینی (Admin)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH)
                            "Enter Admin PIN (Default: ******) to manage & publish movies to Firebase Firestore:"
                        else
                            "پینا ئەدمینی بنڤیسە (******) دا بشێی فلمان د فایەربەیس دا زێدە بکەی:",
                        color = CinemaTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dialogPinInput,
                        onValueChange = {
                            dialogPinInput = it
                            dialogPinError = false
                        },
                        placeholder = { Text("PIN (******)", color = Color.Gray) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            val ok = viewModel.loginAdmin(dialogPinInput)
                            if (ok) {
                                showAdminLoginDialog = false
                                dialogPinInput = ""
                            } else {
                                dialogPinError = true
                            }
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CinemaGold,
                            unfocusedBorderColor = Color(0xFF374151),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (dialogPinError) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "Incorrect PIN! Use ******" else "پین شاشە! ****** بکاربینە",
                            color = CinemaRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = viewModel.loginAdmin(dialogPinInput)
                        if (ok) {
                            showAdminLoginDialog = false
                            dialogPinInput = ""
                        } else {
                            dialogPinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CinemaGold)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Unlock Admin" else "ڤەکرن",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdminLoginDialog = false
                    dialogPinInput = ""
                    dialogPinError = false
                }) {
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Cancel" else "پاشگەزبوونەوە",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CinemaDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (activePlayingMovie == null && selectedMovieForDetail == null) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CinemaRed,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Strings.get("app_title", currentLanguage),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.ENGLISH) "M1955 Cinema • Firebase Cloud" else "سینەمایا کوردی • پەخشێ فایەربەیس",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CinemaGold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // Admin Mode Badge / Login Trigger
                        if (isAdmin) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CinemaGold,
                                modifier = Modifier
                                    .clickable { viewModel.setSelectedTab(2) }
                                    .padding(end = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "Admin Active",
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "ADMIN",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { showAdminLoginDialog = true },
                                modifier = Modifier.testTag("admin_login_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Admin Mode Login",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Quick Search Action
                        IconButton(
                            onClick = { viewModel.setSelectedTab(1) },
                            modifier = Modifier.testTag("top_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }

                        // Language Switcher Dropdown
                        Box {
                            IconButton(
                                onClick = { languageMenuExpanded = true },
                                modifier = Modifier.testTag("top_language_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = CinemaGold
                                )
                            }
                            DropdownMenu(
                                expanded = languageMenuExpanded,
                                onDismissRequest = { languageMenuExpanded = false }
                            ) {
                                AppLanguage.values().forEach { lang ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = lang.nativeName,
                                                fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal,
                                                color = if (lang == currentLanguage) CinemaRed else Color.Unspecified
                                            )
                                        },
                                        onClick = {
                                            viewModel.setLanguage(lang)
                                            languageMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CinemaDarkBg
                    ),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            }
        },
        bottomBar = {
            if (activePlayingMovie == null && selectedMovieForDetail == null) {
                NavigationBar(
                    containerColor = CinemaDarkCard,
                    contentColor = Color.White,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_navigation_bar")
                ) {
                    // If Admin: Home, Categories, (+) Add, Watchlist, Settings
                    // If Viewer: Home, Categories, Watchlist, Settings
                    val navItems = if (isAdmin) {
                        listOf(
                            NavTabItem(0, Strings.get("tab_home", currentLanguage), Icons.Default.Home),
                            NavTabItem(1, Strings.get("tab_categories", currentLanguage), Icons.Default.Explore),
                            NavTabItem(2, Strings.get("tab_add", currentLanguage), Icons.Default.AddCircle, isAddAction = true),
                            NavTabItem(3, Strings.get("tab_watchlist", currentLanguage), Icons.Default.Bookmark),
                            NavTabItem(4, Strings.get("tab_settings", currentLanguage), Icons.Default.Settings)
                        )
                    } else {
                        listOf(
                            NavTabItem(0, Strings.get("tab_home", currentLanguage), Icons.Default.Home),
                            NavTabItem(1, Strings.get("tab_categories", currentLanguage), Icons.Default.Explore),
                            NavTabItem(3, Strings.get("tab_watchlist", currentLanguage), Icons.Default.Bookmark),
                            NavTabItem(4, Strings.get("tab_settings", currentLanguage), Icons.Default.Settings)
                        )
                    }

                    navItems.forEach { item ->
                        val isSelected = selectedTab == item.routeIndex
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedTab(item.routeIndex) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (item.isAddAction && !isSelected) CinemaGold
                                           else if (isSelected) Color.White
                                           else CinemaTextSecondary,
                                    modifier = Modifier.size(if (item.isAddAction) 28.dp else 24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = if (item.isAddAction) CinemaGold else CinemaRed.copy(alpha = 0.8f),
                                selectedTextColor = Color.White,
                                unselectedTextColor = CinemaTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.routeIndex}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Tab Content
            when (selectedTab) {
                0 -> HomeScreen(
                    featuredMovies = featuredMovies,
                    trendingMovies = trendingMovies,
                    continueWatching = continueWatching,
                    allMovies = allMovies,
                    selectedCategory = selectedCategory,
                    currentLanguage = currentLanguage,
                    isAdmin = isAdmin,
                    onCategorySelected = { cat ->
                        viewModel.setSelectedCategory(cat)
                        if (cat != "All") {
                            viewModel.setSelectedTab(1)
                        }
                    },
                    onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                    onPlayMovie = { movie -> viewModel.startPlayingMovie(movie) },
                    onFavoriteToggle = { movie -> viewModel.toggleFavorite(movie) },
                    onNavigateToAddMovie = { viewModel.setSelectedTab(2) }
                )
                1 -> ExploreScreen(
                    movies = filteredMovies,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    currentLanguage = currentLanguage,
                    onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                    onCategorySelected = { cat -> viewModel.setSelectedCategory(cat) },
                    onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                    onFavoriteToggle = { movie -> viewModel.toggleFavorite(movie) }
                )
                2 -> {
                    if (isAdmin) {
                        AddMovieScreen(
                            formState = addMovieForm,
                            currentLanguage = currentLanguage,
                            uploadState = uploadState,
                            onFormChange = { update -> viewModel.updateAddMovieForm(update) },
                            onPickPosterImage = { uri -> viewModel.uploadPoster(uri, context) },
                            onSaveMovie = { viewModel.saveNewMovie {} }
                        )
                    } else {
                        // Fallback to Home if accessed without admin
                        HomeScreen(
                            featuredMovies = featuredMovies,
                            trendingMovies = trendingMovies,
                            continueWatching = continueWatching,
                            allMovies = allMovies,
                            selectedCategory = selectedCategory,
                            currentLanguage = currentLanguage,
                            isAdmin = false,
                            onCategorySelected = { cat -> viewModel.setSelectedCategory(cat) },
                            onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                            onPlayMovie = { movie -> viewModel.startPlayingMovie(movie) },
                            onFavoriteToggle = { movie -> viewModel.toggleFavorite(movie) },
                            onNavigateToAddMovie = { showAdminLoginDialog = true }
                        )
                    }
                }
                3 -> WatchlistScreen(
                    favoriteMovies = favoriteMovies,
                    continueWatchingMovies = continueWatching,
                    currentLanguage = currentLanguage,
                    onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                    onPlayMovie = { movie -> viewModel.startPlayingMovie(movie) },
                    onFavoriteToggle = { movie -> viewModel.toggleFavorite(movie) }
                )
                4 -> SettingsScreen(
                    currentLanguage = currentLanguage,
                    allMovies = allMovies,
                    isAdmin = isAdmin,
                    onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                    onAdminLogin = { pin -> viewModel.loginAdmin(pin) },
                    onAdminLogout = { viewModel.logoutAdmin() }
                )
            }

            // Movie Details Sheet Overlay
            AnimatedVisibility(
                visible = selectedMovieForDetail != null && activePlayingMovie == null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                selectedMovieForDetail?.let { movie ->
                    MovieDetailSheet(
                        movie = movie,
                        reviews = activeReviews,
                        currentLanguage = currentLanguage,
                        isAdmin = isAdmin,
                        onPlay = {
                            viewModel.startPlayingMovie(movie)
                        },
                        onFavoriteToggle = {
                            viewModel.toggleFavorite(movie)
                        },
                        onClose = {
                            viewModel.closeMovieDetail()
                        },
                        onDeleteMovie = {
                            viewModel.deleteMovie(movie)
                        },
                        onSubmitReview = { name, rating, comment ->
                            viewModel.submitReview(movie.id, name, rating, comment)
                        }
                    )
                }
            }

            // Fullscreen / Embedded Video Player Overlay
            AnimatedVisibility(
                visible = activePlayingMovie != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                activePlayingMovie?.let { movie ->
                    VideoPlayerView(
                        movie = movie,
                        playerState = playerState,
                        currentLanguage = currentLanguage,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSeekTo = { pos -> viewModel.seekTo(pos) },
                        onSkipForward = { viewModel.skipForward(10) },
                        onSkipBackward = { viewModel.skipBackward(10) },
                        onSetQuality = { q -> viewModel.setQuality(q) },
                        onSetAudioTrack = { track -> viewModel.setAudioTrack(track) },
                        onSetSubtitle = { sub -> viewModel.setSubtitle(sub) },
                        onSetPlaybackSpeed = { spd -> viewModel.setPlaybackSpeed(spd) },
                        onToggleMute = { viewModel.toggleMute() },
                        onClose = { viewModel.closePlayer() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
