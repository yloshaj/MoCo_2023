package com.group16.mytrips.misc

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.location.Location
import com.group16.mytrips.data.SightFB
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.roundToInt


fun sortByDate(list: List<SightFB>) = list.sortedByDescending {
    (it.date.substring(6, 10).toDouble() * 10000) + (it.date.substring(3, 5)
        .toDouble() * 100) + it.date.substring(0, 2).toDouble() + (it.date.substring(11, 13)
        .toDouble() / 100) + it.date.substring(14, 16).toDouble() / 10000 + it.date.substring(17, 19)
        .toDouble() / 1000000
}


fun getDate(): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"))
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1 //starts at zero
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    var secondString = second.toString()
    var minuteString = minute.toString()
    var hourString = hour.toString()
    var dayString = day.toString()
    var monthString = month.toString()
    if (second < 10) secondString = "0$second"
    if (minute < 10) minuteString = "0$minute"
    if (hour < 10) hourString = "0$hour"
    if (day < 10) dayString = "0$day"
    if (month < 10) monthString = "0$monthString"
    return "$dayString.$monthString.$year $hourString:$minuteString:$secondString"
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


fun makeTransparent(src: Bitmap, value: Int): Bitmap {
    val width = src.width
    val height = src.height
    val transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(transBitmap)
    canvas.drawARGB(0, 0, 0, 0)
    val paint = Paint()
    paint.setAlpha(value)
    canvas.drawBitmap(src, 0f, 0f, paint)
    return transBitmap
}
