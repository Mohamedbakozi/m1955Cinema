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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Movie
import com.example.data.MovieReview
import com.example.ui.AppLanguage
import com.example.ui.Strings
import com.example.ui.components.MovieImage
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaDarkBg
import com.example.ui.theme.CinemaDarkCard
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaTextMuted
import com.example.ui.theme.CinemaTextSecondary

@Composable
fun MovieDetailSheet(
    movie: Movie,
    reviews: List<MovieReview>,
    currentLanguage: AppLanguage,
    isAdmin: Boolean = false,
    onPlay: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onClose: () -> Unit,
    onDeleteMovie: (() -> Unit)? = null,
    onSubmitReview: (String, Float, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTitle = if (currentLanguage == AppLanguage.ENGLISH) movie.titleEn else movie.titleKu
    val secondaryTitle = if (currentLanguage == AppLanguage.ENGLISH) movie.titleKu else movie.titleEn
    val overview = if (currentLanguage == AppLanguage.ENGLISH) movie.overviewEn else movie.overviewKu

    var reviewerName by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var userRating by remember { mutableFloatStateOf(5.0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBg)
            .testTag("movie_detail_sheet")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // Backdrop Header with Back button and gradient
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
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
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        CinemaDarkBg.copy(alpha = 0.8f),
                                        CinemaDarkBg
                                    )
                                )
                            )
                    )

                    // Back & Favorite buttons on top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = onClose, modifier = Modifier.testTag("detail_back_button")) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = onFavoriteToggle) {
                                Icon(
                                    imageVector = if (movie.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Watchlist",
                                    tint = if (movie.isFavorite) CinemaRed else Color.White
                                )
                            }
                        }
                    }

                    // Floating Big Play Icon in center
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(68.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onPlay)
                            .testTag("detail_center_play_btn"),
                        shape = CircleShape,
                        color = CinemaRed
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Watch Now",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                        )
                    }
                }
            }

            // Movie Info Block
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    if (secondaryTitle.isNotBlank() && secondaryTitle != displayTitle) {
                        Text(
                            text = secondaryTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = CinemaTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Metadata Row (Rating, Year, Duration, Quality, Genre)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = CinemaGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${movie.imdbRating}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(text = "•", color = CinemaTextMuted)
                        Text(text = "${movie.releaseYear}", color = CinemaTextSecondary, fontSize = 13.sp)
                        Text(text = "•", color = CinemaTextMuted)
                        Text(text = "${movie.durationMinutes} min", color = CinemaTextSecondary, fontSize = 13.sp)
                        Text(text = "•", color = CinemaTextMuted)

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF1E2433)
                        ) {
                            Text(
                                text = movie.quality,
                                color = CinemaCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onPlay,
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("detail_watch_now_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.get("watch_now", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = onFavoriteToggle,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (movie.isFavorite) CinemaRed else Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = if (movie.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (movie.isFavorite) Strings.get("in_list", currentLanguage) else Strings.get("add_to_list", currentLanguage),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (isAdmin && onDeleteMovie != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onDeleteMovie,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.ENGLISH) "🗑 Delete Movie from Cloud (Admin)" else "🗑 ژێبرنا فلمی ژ سێرڤەری (ئەدمین)",
                                fontWeight = FontWeight.Bold,
                                color = CinemaRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Synopsis
                    Text(
                        text = Strings.get("synopsis", currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Director & Cast Cards
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CinemaDarkCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (movie.director.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${Strings.get("director", currentLanguage)}: ",
                                        color = CinemaTextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = movie.director,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (movie.castMembers.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${Strings.get("cast", currentLanguage)}: ",
                                        color = CinemaTextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = movie.castMembers,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${Strings.get("languages", currentLanguage)}: ",
                                    color = CinemaTextSecondary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = movie.availableLanguages,
                                    color = CinemaCyan,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFF1E2433))
                    Spacer(modifier = Modifier.height(20.dp))

                    // User Reviews & Rating Section
                    Text(
                        text = Strings.get("reviews", currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Review submission form
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaDarkCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = Strings.get("add_review", currentLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Star selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            (1..5).forEach { star ->
                                val filled = star <= userRating
                                Icon(
                                    imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star $star",
                                    tint = if (filled) CinemaGold else Color.Gray,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { userRating = star.toFloat() }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${userRating.toInt()}/5",
                                color = CinemaGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = reviewerName,
                            onValueChange = { reviewerName = it },
                            placeholder = { Text(Strings.get("your_name", currentLanguage)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF0F141E),
                                unfocusedContainerColor = Color(0xFF0F141E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CinemaRed,
                                unfocusedBorderColor = Color(0xFF2D3748)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            placeholder = { Text(Strings.get("your_comment", currentLanguage)) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF0F141E),
                                unfocusedContainerColor = Color(0xFF0F141E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CinemaRed,
                                unfocusedBorderColor = Color(0xFF2D3748)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (reviewComment.isNotBlank()) {
                                    onSubmitReview(reviewerName, userRating, reviewComment)
                                    reviewComment = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = Strings.get("submit_review", currentLanguage))
                        }
                    }
                }
            }

            // Reviews List
            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = Strings.get("no_reviews", currentLanguage),
                        color = CinemaTextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(reviews) { review ->
                    ReviewItemCard(review = review)
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(review: MovieReview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CinemaRed.copy(alpha = 0.25f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = CinemaRed,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = review.userName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = CinemaGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${review.rating.toInt()}/5",
                        color = CinemaGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = review.comment,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.dateDisplay,
                color = CinemaTextMuted,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
