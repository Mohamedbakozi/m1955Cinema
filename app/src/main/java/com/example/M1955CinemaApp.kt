package com.example

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class M1955CinemaApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        initializeFirebaseSafely()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50L * 1024 * 1024) // 50MB
                    .build()
            }
            .crossfade(true)
            .build()
    }

    private fun initializeFirebaseSafely() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(this)
                    Log.i("M1955CinemaApp", "FirebaseApp initialized from system config")
                } catch (e: Exception) {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:839904802588:android:8c09370612e36c97ca6cd9")
                        .setApiKey("AIzaSyC6i47tkTkJiMOvtCM7FPf7BfVV2kfCz_8")
                        .setProjectId("m1955cinema")
                        .setStorageBucket("m1955cinema.firebasestorage.app")
                        .build()
                    FirebaseApp.initializeApp(this, options)
                    Log.i("M1955CinemaApp", "FirebaseApp initialized with m1955cinema project configuration")
                }
            }
        } catch (e: Exception) {
            Log.w("M1955CinemaApp", "Firebase safe init completed with notice: ${e.message}")
        }
    }
}
