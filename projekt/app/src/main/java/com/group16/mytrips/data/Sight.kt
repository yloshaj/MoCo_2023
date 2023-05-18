package com.group16.mytrips.data

data class Sight (val sightId: String , var picture: Int, val sightName: String, val date: String, val coordinates: String)
data class DefaultSight(val sightId: Int, var defualtPicture: Int, val sightName: String, val latitude: Double, val longitude: Double)
