package com.group16.mytrips.data

import android.net.Uri

data class Sight(
    val sightId: Int,
    var picture: Int,
    var pictureThumbnail: Int,
    var pictureUri: Uri? = null,
    val sightName: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
)

data class DefaultSight(
    val sightId: Int,
    var defualtPicture: Int,
    var pictureThumbnail: Int,
    val sightName: String,
    val latitude: Double,
    val longitude: Double,
    var distance: Int? = null
) {
    fun doesNatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$sightName",
            "$latitude$longitude",
            "$latitude $longitude",
            "$latitude, $longitude"
        )
        return matchingCombinations.any { it.contains(query, ignoreCase = true) }
    }
}
