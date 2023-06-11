package com.group16.mytrips.viewModel

import android.app.Application
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.group16.mytrips.data.DefaultSightFB
import com.group16.mytrips.data.LocationLiveData
import com.group16.mytrips.data.SightFB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import kotlin.math.roundToInt

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val radius = 70  // radius in meters

    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData
    fun startLocationUpdates() = {
        ->
        locationLiveData.startLocationUpdates()
    }

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val defaultSightRef = firestore.collection("DefaultSightFB")
    private val collectionRef = firestore.collection("SightFB")
    private val userRef = firestore.collection("User")
    private val userId = "7T9VijHoW9vQTKnLlDhp"

    private val _defaultSightList = MutableStateFlow(emptyList<DefaultSightFB>().toMutableList())
    val defaultSightList = _defaultSightList.asStateFlow()

    private val _sightList = MutableStateFlow(emptyList<SightFB>().toMutableList())
    val sightList = _sightList.asStateFlow()



    private var _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    fun setAlert(shouldShow: Boolean) {
        if (_currentSight.value.sightId != -1) _alert.value = shouldShow
    }

    fun alreadyVisited() = _sightList.value.any { it.sightId == _currentSight.value.sightId}

    private var _sightVisitedAlert = MutableStateFlow(false)
    val sightVisitedAlert = _sightVisitedAlert.asStateFlow()
    fun setSightVisitedAlert(visited: Boolean) {
        _sightVisitedAlert.value = visited
    }

    private var _uriList = MutableStateFlow<List<String>>(emptyList())
    val uriList = _uriList.asStateFlow()
    fun setUriList(list: List<String>) {
        _uriList.value = list

    }

    private var _currentSight = MutableStateFlow(DefaultSightFB())
    val currentSight = _currentSight.asStateFlow()
    fun setCurrentSight(sight: DefaultSightFB) {
        _currentSight.value = sight
    }


    fun uploadPicturesToFirebaseStorage(
        newPictures: Boolean
    ) {
        val storageRef = storage.reference
        if (_sightList.value.none { it.sightId == _currentSight.value.sightId } || _currentSight.value.sightId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (!newPictures) uploadNewSight(null, null)
                    else {
                        val uploadTasks = _uriList.value.mapIndexed { index, uri ->
                            val imageRef = storageRef.child("images/${UUID.randomUUID()}_$index")
                            imageRef.putFile(Uri.parse(uri)).await()
                            imageRef.downloadUrl.await()
                        }

                        val downloadUrls = uploadTasks.map { task ->
                            task.toString()
                        }
                        Log.e("URL", downloadUrls[0])


                        uploadNewSight(downloadUrls[0], downloadUrls[1])
                    }
                } catch (e: Exception) {
                    Log.e("Firebase Storage", e.toString())
                }
            }
        }
        if (_sightList.value.filter { it.sightId == _currentSight.value.sightId }.isNotEmpty()) setSightVisitedAlert(true)
    }

    fun uploadNewSight(picture: String?, thumbnail: String?) {

        val sight = _sightList.value.filter { it.sightId == _currentSight.value.sightId }
        if (sight.isEmpty()) {
            val defaultSight = _defaultSightList.value.first { it.sightId == _currentSight.value.sightId }

            val newSight = SightFB(
                sightId = defaultSight.sightId,
                picture = picture ?: defaultSight.defualtPicture,
                thumbnail = thumbnail ?: defaultSight.thumbnail,
                sightName = defaultSight.sightName,
                latitude = defaultSight.latitude,
                longitude = defaultSight.longitude,
                date = getDate()
            )
            collectionRef.add(newSight)
            updateXP(20)
        }
    }

    fun startListeningForData() {
        startListeningForSightList()
        startListeningForDefaultSightList()
    }

    fun stopListeningForData() {
        stopListeningForSightList()
        stopListeningForDefaultSightList()
    }





    private lateinit var listenerRegistration: ListenerRegistration

    fun startListeningForDefaultSightList() {
        listenerRegistration = defaultSightRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val sights = snapshot?.documents?.mapNotNull { document ->
                document.toObject(DefaultSightFB::class.java)
            } ?: emptyList()

            _defaultSightList.value = sights.toMutableList()
        }
    }
    fun stopListeningForDefaultSightList() {
        listenerRegistration.remove()
    }



    private lateinit var listenerRegistrationSights: ListenerRegistration

    fun startListeningForSightList() {
        listenerRegistrationSights = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            val sights = snapshot?.documents?.mapNotNull { document ->
                document.toObject(SightFB::class.java)
            } ?: emptyList()

            _sightList.value = sights.toMutableList()
        }
    }
    fun stopListeningForSightList() {
        listenerRegistrationSights.remove()
    }


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


    fun getSortedList(): StateFlow<MutableList<DefaultSightFB>> {
        val location = getLocationLiveData().value
        val observedLocationData = getLocationLiveData()
        val list = _defaultSightList.asStateFlow()
        list.value.forEach {
            val l = observedLocationData

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
        if (location != null) list.value.retainAll { it.distance!! <= radius }

        return list
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


    fun getDate() : String {
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

}