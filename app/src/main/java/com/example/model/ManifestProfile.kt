package com.example.model

/**
 * Saved profile / preset of AndroidManifest configuration
 */
data class ManifestProfile(
    val id: String,
    val name: String,
    val description: String,
    val timestamp: Long,
    val manifest: ManifestData
)
