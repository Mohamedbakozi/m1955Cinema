package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.CinemaDarkCard
import com.example.ui.theme.CinemaDarkSurfaceVariant
import com.example.ui.theme.CinemaGold

@Composable
fun MovieImage(
    posterResName: String,
    posterUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    if (posterUrl.isNotBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(posterUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            error = painterResource(id = getDrawableResByName(posterResName)),
            placeholder = painterResource(id = getDrawableResByName(posterResName))
        )
    } else {
        val resId = getDrawableResByName(posterResName)
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

fun getDrawableResByName(name: String): Int {
    return when (name) {
        "img_poster_kurdish" -> R.drawable.img_poster_kurdish
        "img_poster_action" -> R.drawable.img_poster_action
        "img_poster_animation" -> R.drawable.img_poster_animation
        "img_hero_cinematic" -> R.drawable.img_hero_cinematic
        "img_app_icon" -> R.drawable.img_app_icon
        else -> R.drawable.img_poster_kurdish
    }
}
