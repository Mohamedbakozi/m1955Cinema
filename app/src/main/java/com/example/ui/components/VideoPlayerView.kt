package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.Movie
import com.example.ui.AppLanguage
import com.example.ui.PlayerState
import com.example.ui.theme.CinemaCyan
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerView(
    movie: Movie,
    playerState: PlayerState,
    currentLanguage: AppLanguage,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit,
    onSetQuality: (String) -> Unit,
    onSetAudioTrack: (String) -> Unit,
    onSetSubtitle: (String) -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var customView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    val displayTitle = if (currentLanguage == AppLanguage.ENGLISH) movie.titleEn else movie.titleKu
    val videoUrl = movie.videoUrl.trim()

    // Handle back button for fullscreen video in WebView
    BackHandler(enabled = customView != null) {
        customViewCallback?.onCustomViewHidden()
        customView = null
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewInstance?.let { wv ->
                wv.stopLoading()
                wv.loadUrl("about:blank")
                wv.onPause()
                wv.destroy()
            }
            webViewInstance = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("video_player_container")
    ) {
        // Main WebView for Video Streaming
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(android.graphics.Color.BLACK)
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        allowFileAccess = true
                        allowContentAccess = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = false
                        displayZoomControls = false
                        safeBrowsingEnabled = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                    }

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress >= 80) {
                                isLoading = false
                            }
                        }

                        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                            super.onShowCustomView(view, callback)
                            customView = view
                            customViewCallback = callback
                        }

                        override fun onHideCustomView() {
                            super.onHideCustomView()
                            customView = null
                            customViewCallback = null
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

                        override fun onRenderProcessGone(
                            view: WebView?,
                            detail: RenderProcessGoneDetail?
                        ): Boolean {
                            // Prevent app crash on renderer process termination in emulator
                            isLoading = false
                            return true
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false
                            if (url.startsWith("http://") || url.startsWith("https://")) {
                                return false // Let WebView load it
                            }
                            return try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                ctx.startActivity(intent)
                                true
                            } catch (e: Exception) {
                                true
                            }
                        }
                    }

                    loadMovieVideo(this, videoUrl)
                    webViewInstance = this
                }
            },
            update = { wv ->
                // Called when recomposed
            },
            modifier = Modifier.fillMaxSize()
        )

        // If in Fullscreen HTML5 Video Mode, overlay the custom view
        if (customView != null) {
            AndroidView(
                factory = {
                    FrameLayout(it).apply {
                        addView(
                            customView,
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Bar Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("player_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Player",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = displayTitle,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = CinemaRed
                            ) {
                                Text(
                                    text = "WEBVIEW PLAYER",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${movie.quality} • ${movie.category}",
                                color = CinemaGold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Action icons (Refresh, Open in Browser)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            webViewInstance?.let { loadMovieVideo(it, videoUrl) }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload Video",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(extractCleanUrl(videoUrl)))
                                context.startActivity(browserIntent)
                            } catch (_: Exception) {}
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open in Browser",
                            tint = CinemaCyan
                        )
                    }
                }
            }
        }

        // Loading Indicator Overlay
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        color = CinemaRed,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Loading video stream..." else "پەخشێ ڤیدیۆیێ دهێتە بارکرن...",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Loads the video into WebView. If it is an iframe, direct video, or embed URL (like ok.ru, youtube, etc.),
 * it wraps it in an optimal responsive HTML container or loads directly.
 */
private fun loadMovieVideo(webView: WebView, rawUrl: String) {
    val cleanUrl = rawUrl.trim()

    if (cleanUrl.startsWith("<iframe") || cleanUrl.contains("<iframe")) {
        // Embed code provided directly
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    * { margin:0; padding:0; box-sizing:border-box; background-color:#000000; }
                    html, body { width:100%; height:100%; overflow:hidden; display:flex; justify-content:center; align-items:center; background:#000000; }
                    iframe { width:100%; height:100%; border:none; }
                </style>
            </head>
            <body>
                $cleanUrl
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL("https://ok.ru", htmlContent, "text/html", "utf-8", null)
    } else if (cleanUrl.endsWith(".mp4") || cleanUrl.endsWith(".mkv") || cleanUrl.endsWith(".webm") || cleanUrl.endsWith(".m3u8")) {
        // Direct video stream file
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    * { margin:0; padding:0; box-sizing:border-box; background-color:#000000; }
                    html, body { width:100%; height:100%; overflow:hidden; display:flex; justify-content:center; align-items:center; background:#000000; }
                    video { width:100%; height:100%; object-fit:contain; }
                </style>
            </head>
            <body>
                <video controls autoplay playsinline webkit-playsinline>
                    <source src="$cleanUrl" type="video/mp4">
                    Your browser does not support HTML5 video.
                </video>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    } else {
        // URL like https://ok.ru/videoembed/2884920347203
        val targetUrl = if (cleanUrl.startsWith("//")) "https:$cleanUrl" else cleanUrl
        val embedHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    * { margin:0; padding:0; box-sizing:border-box; background-color:#000000; }
                    html, body { width:100%; height:100%; overflow:hidden; display:flex; justify-content:center; align-items:center; background:#000000; }
                    iframe { width:100vw; height:100vh; border:none; }
                </style>
            </head>
            <body>
                <iframe src="$targetUrl" frameborder="0" allow="autoplay; fullscreen; encrypted-media; picture-in-picture" allowfullscreen></iframe>
            </body>
            </html>
        """.trimIndent()
        webView.loadDataWithBaseURL(targetUrl, embedHtml, "text/html", "utf-8", null)
    }
}

private fun extractCleanUrl(raw: String): String {
    if (raw.startsWith("<iframe")) {
        val srcRegex = """src=["']([^"']+)["']""".toRegex()
        val match = srcRegex.find(raw)
        if (match != null) {
            val url = match.groupValues[1]
            return if (url.startsWith("//")) "https:$url" else url
        }
    }
    return if (raw.startsWith("//")) "https:$raw" else raw
}
