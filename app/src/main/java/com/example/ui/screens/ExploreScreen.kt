package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Movie
import com.example.ui.AppLanguage
import com.example.ui.Strings
import com.example.ui.components.MoviePosterCard
import com.example.ui.theme.CinemaDarkCard
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaTextMuted
import com.example.ui.theme.CinemaTextSecondary

@Composable
fun ExploreScreen(
    movies: List<Movie>,
    searchQuery: String,
    selectedCategory: String,
    currentLanguage: AppLanguage,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onMovieClick: (Movie) -> Unit,
    onFavoriteToggle: (Movie) -> Unit,
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen")
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text(Strings.get("search_hint", currentLanguage), fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CinemaTextSecondary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.White
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CinemaDarkCard,
                unfocusedContainerColor = CinemaDarkCard,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = CinemaRed,
                unfocusedBorderColor = Color(0xFF2D3748)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("explore_search_field")
        )

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (catKey, catLabel) ->
                val isSelected = selectedCategory == catKey
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
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid of Movies
        if (movies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = null,
                        tint = CinemaTextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "No movies found" else "چ فلم نەهاتنە دیتن",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Try searching for different keywords or categories" else "تکایە پەیڤەکا دی بنڤیسە یان پۆلینەکا دی هەڵبژێرە",
                        color = CinemaTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies, key = { it.id }) { movie ->
                    MoviePosterCard(
                        movie = movie,
                        currentLanguage = currentLanguage,
                        onClick = { onMovieClick(movie) },
                        onFavoriteToggle = { onFavoriteToggle(movie) },
                        cardWidth = 160
                    )
                }
            }
        }
    }
}
