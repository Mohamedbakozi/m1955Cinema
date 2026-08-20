package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ImageUploadState
import com.example.ui.AddMovieFormState
import com.example.ui.AppLanguage
import com.example.ui.Strings
import com.example.ui.components.getDrawableResByName
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaDarkCard
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    formState: AddMovieFormState,
    currentLanguage: AppLanguage,
    uploadState: ImageUploadState = ImageUploadState.Idle,
    onFormChange: ((AddMovieFormState) -> AddMovieFormState) -> Unit,
    onPickPosterImage: (Uri) -> Unit = {},
    onSaveMovie: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val genreOptions = listOf(
        Pair("Movies", "Movies (فیلم)"),
        Pair("Action", "Action (ئەکشن)"),
        Pair("Drama", "Drama (دراما)"),
        Pair("Animation", "Animation (ئەنیمەیشن)"),
        Pair("subtitle", "subtitle (ژێرنووس)"),
        Pair("Film cartoon", "Film cartoon (کارتۆن)"),
        Pair("Drama kurdi", "Drama kurdi (درامای کوردی)")
    )

    val posterPresets = listOf(
        Pair("img_poster_kurdish", "Kurdish Mountain Cinema"),
        Pair("img_poster_action", "Action & Cyberpunk"),
        Pair("img_poster_animation", "Animation Forest"),
        Pair("img_hero_cinematic", "Epic Cinematic Banner")
    )

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("add_movie_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaDarkCard)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CinemaRed,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = Strings.get("add_movie_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH)
                                "Publish to Firebase Firestore & Storage for all viewers"
                            else
                                "فلمێ خۆ ڕاستەوخۆ د Firebase دا بەلاڤ بکە بۆ هەموو بینەران",
                            style = MaterialTheme.typography.bodySmall,
                            color = CinemaGold
                        )
                    }
                }
            }
        }

        // ==========================================
        // 🖼️ POSTER IMAGE URL & LIVE PREVIEW
        // ==========================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF191F2D)),
                border = BorderStroke(1.dp, if (formState.posterUrl.isNotBlank()) CinemaGold else Color(0xFF2D3748))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = CinemaGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Strings.get("poster_url_title", currentLanguage),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF263248)
                        ) {
                            Text(
                                text = "Web / Google Image",
                                color = CinemaCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = Strings.get("poster_url_desc", currentLanguage),
                        fontSize = 12.sp,
                        color = CinemaTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Poster URL Input TextField
                    OutlinedTextField(
                        value = formState.posterUrl,
                        onValueChange = { newVal ->
                            onFormChange {
                                it.copy(
                                    posterUrl = newVal.trim(),
                                    bannerUrl = if (it.bannerUrl.isBlank() || it.bannerUrl == it.posterUrl) newVal.trim() else it.bannerUrl,
                                    posterResName = if (newVal.isNotBlank()) "" else it.posterResName
                                )
                            }
                        },
                        label = { Text(Strings.get("poster_url_title", currentLanguage), fontSize = 13.sp) },
                        placeholder = { Text(Strings.get("poster_url_hint", currentLanguage), fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "URL",
                                tint = CinemaGold
                            )
                        },
                        trailingIcon = {
                            if (formState.posterUrl.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        onFormChange { it.copy(posterUrl = "") }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_poster_url"),
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColors(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Image Preview Section
                    Text(
                        text = Strings.get("live_preview_title", currentLanguage),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF111622))
                            .border(
                                width = if (formState.posterUrl.isNotBlank()) 1.5.dp else 1.dp,
                                color = if (formState.posterUrl.isNotBlank()) CinemaGold else Color(0xFF283244),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (formState.posterUrl.isNotBlank()) {
                            AsyncImage(
                                model = formState.posterUrl,
                                contentDescription = "Live Poster Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ImageSearch,
                                    contentDescription = null,
                                    tint = CinemaTextSecondary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.ENGLISH)
                                        "Paste image link above to preview poster"
                                    else
                                        "لینکا وێنەیێ پۆستەری ل سەری دابنێ دا ل ڤێرێ ب دروستی دیار ببیت",
                                    color = CinemaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Kurdish Title
        item {
            OutlinedTextField(
                value = formState.titleKu,
                onValueChange = { newVal -> onFormChange { it.copy(titleKu = newVal) } },
                label = { Text(Strings.get("movie_title_ku", currentLanguage)) },
                placeholder = { Text("بۆ نموونە: بیرهاتنێن دهۆکێ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_title_ku"),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors()
            )
        }

        // English Title
        item {
            OutlinedTextField(
                value = formState.titleEn,
                onValueChange = { newVal -> onFormChange { it.copy(titleEn = newVal) } },
                label = { Text(Strings.get("movie_title_en", currentLanguage)) },
                placeholder = { Text("e.g. Memories of Duhok") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_title_en"),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors()
            )
        }

        // Category Dropdown
        item {
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (currentLanguage == AppLanguage.ENGLISH) formState.category else formState.categoryKu,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(Strings.get("movie_category", currentLanguage)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("dropdown_category"),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    genreOptions.forEach { (catEn, catKu) ->
                        DropdownMenuItem(
                            text = { Text("$catKu ($catEn)") },
                            onClick = {
                                onFormChange {
                                    it.copy(
                                        category = catEn,
                                        categoryKu = catKu
                                    )
                                }
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Release Year & Duration Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = formState.releaseYear,
                    onValueChange = { newVal -> onFormChange { it.copy(releaseYear = newVal) } },
                    label = { Text(Strings.get("movie_release_year", currentLanguage)) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_release_year"),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors()
                )

                OutlinedTextField(
                    value = formState.durationMinutes,
                    onValueChange = { newVal -> onFormChange { it.copy(durationMinutes = newVal) } },
                    label = { Text(Strings.get("movie_duration", currentLanguage)) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_duration"),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors()
                )
            }
        }

        // Video URL / Stream Link
        item {
            Column {
                OutlinedTextField(
                    value = formState.videoUrl,
                    onValueChange = { newVal -> onFormChange { it.copy(videoUrl = newVal) } },
                    label = { Text(Strings.get("movie_video_url", currentLanguage)) },
                    placeholder = { Text("https://ok.ru/videoembed/... or https://example.com/video.mp4") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_video_url"),
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors()
                )

                // Quick preset URL buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clickable {
                                onFormChange {
                                    it.copy(videoUrl = "https://ok.ru/videoembed/2884920347203")
                                }
                            },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E2433)
                    ) {
                        Text(
                            text = "🎬 OK.ru Kurdish Stream",
                            color = CinemaGold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clickable {
                                onFormChange {
                                    it.copy(videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                                }
                            },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E2433)
                    ) {
                        Text(
                            text = "🎬 4K HD Stream",
                            color = CinemaCyan,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Built-in Poster Presets (Fallback or Alternative)
        item {
            Text(
                text = Strings.get("select_preset_poster", currentLanguage),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(posterPresets) { (resName, label) ->
                    val isSelected = formState.posterResName == resName && formState.posterUrl.isEmpty()
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .aspectRatio(0.72f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                BorderStroke(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) CinemaRed else Color.White.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onFormChange {
                                    it.copy(
                                        posterResName = resName,
                                        posterUrl = "",
                                        bannerResName = resName
                                    )
                                }
                            }
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableResByName(resName)),
                            contentDescription = label,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(bottomStart = 8.dp),
                                color = CinemaRed,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Kurdish Synopsis
        item {
            OutlinedTextField(
                value = formState.overviewKu,
                onValueChange = { newVal -> onFormChange { it.copy(overviewKu = newVal) } },
                label = { Text(Strings.get("movie_synopsis_ku", currentLanguage)) },
                placeholder = { Text("کورتە چیرۆکەکێ ل دۆر فلمی بنڤیسە...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_synopsis_ku"),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors()
            )
        }

        // Director & Cast
        item {
            OutlinedTextField(
                value = formState.director,
                onValueChange = { newVal -> onFormChange { it.copy(director = newVal) } },
                label = { Text(Strings.get("director", currentLanguage)) },
                placeholder = { Text("شوان ئەحمەد / Christopher Nolan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_director"),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = formState.castMembers,
                onValueChange = { newVal -> onFormChange { it.copy(castMembers = newVal) } },
                label = { Text(Strings.get("cast", currentLanguage)) },
                placeholder = { Text("هۆزان جان، زانا، بەیان") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_cast"),
                shape = RoundedCornerShape(10.dp),
                colors = textFieldColors()
            )
        }

        // Save & Publish Button
        item {
            Button(
                onClick = onSaveMovie,
                colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_add_movie_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Publish,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.get("save_movie", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CinemaDarkCard,
    unfocusedContainerColor = CinemaDarkCard,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = CinemaRed,
    unfocusedBorderColor = Color(0xFF2D3748),
    focusedLabelColor = CinemaRed,
    unfocusedLabelColor = CinemaTextSecondary,
    cursorColor = CinemaRed
)
