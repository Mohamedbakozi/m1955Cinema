package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Movie
import com.example.ui.AppLanguage
import com.example.ui.Strings
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.MovieImage
import com.example.ui.components.MoviePosterCard
import com.example.ui.theme.CinemaDarkCard
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaTextMuted
import com.example.ui.theme.CinemaTextSecondary

@Composable
fun HomeScreen(
    featuredMovies: List<Movie>,
    trendingMovies: List<Movie>,
    continueWatching: List<Movie>,
    allMovies: List<Movie>,
    selectedCategory: String,
    currentLanguage: AppLanguage,
    isAdmin: Boolean = false,
    onCategorySelected: (String) -> Unit,
    onMovieClick: (Movie) -> Unit,
    onPlayMovie: (Movie) -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
    onNavigateToAddMovie: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "All" to Strings.get("all_categories", currentLanguage),
        "Movies" to "Movies",
        "Action" to Strings.get("genre_action", currentLanguage),
        "Drama" to Strings.get("genre_drama", currentLanguage),
        "Animation" to Strings.get("genre_animation", currentLanguage),
        "subtitle" to "subtitle",
        "Film cartoon" to "Film cartoon",
        "Drama kurdi" to "Drama kurdi"
    )

    val kurdishSpecialMovies = allMovies.filter { 
        it.category == "M1955 Cinema" || it.category == "Kurdish Cinema" || it.categoryKu.contains("کوردی") 
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Featured Banner
        if (featuredMovies.isNotEmpty()) {
            val heroMovie = featuredMovies.first()
            item {
                HeroBanner(
                    movie = heroMovie,
                    currentLanguage = currentLanguage,
                    onPlay = { onPlayMovie(heroMovie) },
                    onDetails = { onMovieClick(heroMovie) },
                    onFavoriteToggle = { onFavoriteToggle(heroMovie) }
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey || (catKey == "All" && selectedCategory == "All")
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(catKey) },
                        label = {
                            Text(
                                text = catLabel,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            containerColor = CinemaDarkCard,
                            labelColor = Color.White.copy(alpha = 0.8f),
                            selectedContainerColor = CinemaRed,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("category_chip_$catKey")
                    )
                }
            }
        }

        // Continue Watching Section (if any)
        if (continueWatching.isNotEmpty()) {
            item {
                SectionHeader(title = Strings.get("continue_watching", currentLanguage))
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(continueWatching) { movie ->
                        ContinueWatchingCard(
                            movie = movie,
                            currentLanguage = currentLanguage,
                            onClick = { onPlayMovie(movie) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Kurdish Cinema Spotlight Section
        if (kurdishSpecialMovies.isNotEmpty()) {
            item {
                SectionHeader(
                    title = Strings.get("M1955_cinema", currentLanguage),
                    subtitle = if (currentLanguage == AppLanguage.ENGLISH) "Masterpieces & Folklore" else "بەرهەمێن نایاب و دیرۆکا کوردی"
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(kurdishSpecialMovies) { movie ->
                        MoviePosterCard(
                            movie = movie,
                            currentLanguage = currentLanguage,
                            onClick = { onMovieClick(movie) },
                            onFavoriteToggle = { onFavoriteToggle(movie) },
                            cardWidth = 155
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Trending Now Section
        if (trendingMovies.isNotEmpty()) {
            item {
                SectionHeader(title = Strings.get("trending", currentLanguage))
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trendingMovies) { movie ->
                        MoviePosterCard(
                            movie = movie,
                            currentLanguage = currentLanguage,
                            onClick = { onMovieClick(movie) },
                            onFavoriteToggle = { onFavoriteToggle(movie) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Add Movie CTA Promo Card (Admin Only)
        if (isAdmin) {
            item {
                AddMoviePromoCard(
                    currentLanguage = currentLanguage,
                    onAddClick = onNavigateToAddMovie
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Top Rated Movies Section
        item {
            SectionHeader(title = Strings.get("top_rated", currentLanguage))
        }
        item {
            val topRated = allMovies.sortedByDescending { it.imdbRating }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topRated) { movie ->
                    MoviePosterCard(
                        movie = movie,
                        currentLanguage = currentLanguage,
                        onClick = { onMovieClick(movie) },
                        onFavoriteToggle = { onFavoriteToggle(movie) }
                    )
                }
            }
        }
    }
}

@Composable
fun HeroBanner(
    movie: Movie,
    currentLanguage: AppLanguage,
    onPlay: () -> Unit,
    onDetails: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val displayTitle = if (currentLanguage == AppLanguage.ENGLISH) movie.titleEn else movie.titleKu
    val overview = if (currentLanguage == AppLanguage.ENGLISH) movie.overviewEn else movie.overviewKu

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.25f)
            .testTag("hero_banner")
    ) {
        MovieImage(
            posterResName = movie.bannerResName.ifEmpty { movie.posterResName },
            posterUrl = movie.bannerUrl.ifEmpty { movie.posterUrl },
            contentDescription = displayTitle,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF090C10).copy(alpha = 0.4f),
                            Color(0xFF090C10).copy(alpha = 0.95f),
                            Color(0xFF090C10)
                        ),
                        startY = 100f
                    )
                )
        )

        // Hero Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Quality & Category Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CinemaRed
                ) {
                    Text(
                        text = "FEATURED",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = movie.quality,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = CinemaGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${movie.imdbRating}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = displayTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
                color = CinemaTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Play Now, Watchlist, Info)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("hero_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("watch_now", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onFavoriteToggle,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (movie.isFavorite) CinemaRed else Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("hero_watchlist_button")
                ) {
                    Icon(
                        imageVector = if (movie.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Watchlist",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (movie.isFavorite) Strings.get("in_list", currentLanguage) else Strings.get("add_to_list", currentLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onDetails,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("hero_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Details",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = CinemaTextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AddMoviePromoCard(
    currentLanguage: AppLanguage,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onAddClick)
            .testTag("add_movie_promo_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2433))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = CinemaRed,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Movie",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = Strings.get("add_movie_title", currentLanguage),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = Strings.get("add_movie_subtitle", currentLanguage),
                        color = CinemaTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.LocalMovies,
                contentDescription = null,
                tint = CinemaGold,
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}
