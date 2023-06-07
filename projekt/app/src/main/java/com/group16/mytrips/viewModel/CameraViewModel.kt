package com.group16.mytrips.viewModel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.group16.mytrips.data.DefaultSight
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.data.ModelClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class CameraViewModel(application: Application): AndroidViewModel(application) {
    private val diameter = 50

    private val locationLiveData = LocationLiveData(application)
    private val modelClass = ModelClass()
    private val firestore = FirebaseFirestore.getInstance()
    private val userRef = firestore.collection("User")
    private val userId = "7T9VijHoW9vQTKnLlDhp"

    private var _defaultSightList = MutableStateFlow(modelClass.defaultSightList)
    val defaultSightList = _defaultSightList.asStateFlow()

    fun getLocationLiveData() = locationLiveData

    private fun distanceInMeter(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Int {
        var results = FloatArray(1)
        Location.distanceBetween(startLat, startLon, endLat, endLon, results)
        return results[0].roundToInt()
    }

    fun updateXP(increment: Int) {
        val xp = "overallxp"
        val documentRef = userRef.document(userId)
        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentValue = documentSnapshot.getLong(xp)
                    if (currentValue != null) {
                        val newValue = currentValue + increment
                        val updatedData = hashMapOf(
                            xp to newValue
                        )

                        documentRef.set(updatedData, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d("Firebase", "XP Value Updated")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firebase", "Following Exception occured $exception")
                            }
                    }
                } else {
                    Log.e("Firebase", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Following Exception occured $exception")
            }
    }

    fun getSortedList(): StateFlow<MutableList<DefaultSight>> {
        val observedLocationData = getLocationLiveData()
        val list = _defaultSightList.asStateFlow()
        list.value.forEach {
            val l = observedLocationData
            val location = getLocationLiveData().value
            if (location == null) it.distance = null
            else
                it.distance = distanceInMeter(
                    location.latitude,
                    location.longitude,
                    it.latitude,
                    it.longitude
                )
        }
        //list.value.sortedByDescending { it.distance }
        list.value.sortBy { it.distance }
        list.value.filter { if (it.distance != null) it.distance!! <= diameter else false }
        return list
    }



}