package com.group16.mytrips.data

import android.net.Uri
import com.group16.mytrips.R


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
            sightName,
            "$latitude$longitude",
            "$latitude $longitude",
            "$latitude, $longitude"
        )
        return matchingCombinations.any { it.contains(query, ignoreCase = true) }
    }
}

data class DefaultSightFB(
    val sightId: Int = -1,
    var defualtPicture: String? = null,
    var thumbnail: String? = null,
    val sightName: String = "",
    val latitude: Double = -1.0,
    val longitude: Double = -1.0,
    var distance: Int? = null,
    var visited: Boolean = false,
    var pin : Int = R.drawable.ic_standard_pin
)

data class SightFB(
    val sightId: Int = -1,
    val userId: String = "",
    var picture: String? = null,
    var thumbnail: String? = null,
    val sightName: String = "",
    val date: String = "01.01.1900",
    val latitude: Double = -1.0,
    val longitude: Double = -1.0,
    var pin: Int =  R.drawable.ic_standard_pin
)
