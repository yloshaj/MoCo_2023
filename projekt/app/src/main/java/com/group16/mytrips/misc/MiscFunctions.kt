package com.group16.mytrips.misc

import android.location.Location
import com.group16.mytrips.data.SightFB
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.roundToInt

fun sortByDate(list: List<SightFB>) = list.sortedByDescending {
    (it.date.substring(6, 10).toInt() * 10000) + (it.date.substring(3, 5)
        .toInt() * 100) + it.date.substring(0, 2).toInt()
}

fun getDate(): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"))
    cal.time = Date()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1 //starts at zero
    val day = cal.get(Calendar.DAY_OF_MONTH)
    var dayString = day.toString()
    var monthString = month.toString()
    if (day < 10) dayString = "0$day"
    if (month < 10) monthString = "0$monthString"
    return "$dayString.$monthString.$year"
}

fun distanceInMeter(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double
): Int {
    var results = FloatArray(1)
    Location.distanceBetween(startLat, startLon, endLat, endLon, results)
    return results[0].roundToInt()
}