package com.group16.mytrips.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.group16.mytrips.data.Avatar
import com.group16.mytrips.data.SightFB
import com.group16.mytrips.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {


    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("SightFB")
    private val userRef = firestore.collection("User")
    private val userId = "7T9VijHoW9vQTKnLlDhp"
    private val avatarRef = firestore.collection("avatar")



    private var _user = MutableStateFlow(User())
    val user = _user.asStateFlow()


    private var _avatarList = MutableStateFlow(emptyList<Avatar>().toMutableList())
    val avatarList = _avatarList.asStateFlow()


    fun stopListeningForAvatar() {
        listenerRegistrationAvatar.remove()
    }
    fun startListeningForData() {
        startListeningForUser()
        startListeningForSightList()
        startListeningForAvatar()
    }

    fun stopListeningForData() {
        stopListeningForUser()
        stopListeningForSightList()
        stopListeningForAvatar()
    }

    private lateinit var listenerRegistrationUser: ListenerRegistration
    fun startListeningForUser() {
        listenerRegistrationUser =
            userRef.document(userId).addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val sight = snapshot?.toObject(User::class.java)

                _user.value = sight ?: User()
            }
    }

    fun stopListeningForUser() {
        listenerRegistrationUser.remove()
    }

    private lateinit var listenerRegistrationAvatar: ListenerRegistration
    fun startListeningForAvatar() {
        listenerRegistrationAvatar =
            avatarRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val avatare = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Avatar::class.java)
                } ?: emptyList()

                _avatarList.value = avatare.toMutableList()
            }
    }



    private val _sightList = MutableStateFlow(emptyList<SightFB>().toMutableList())
    val sightList = _sightList.asStateFlow()

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


    fun updateAvatar(localPath: Int) {
        val documentRef = userRef.document(userId)
        val data = hashMapOf(
            "avatar" to localPath
        )
        documentRef.set(data, SetOptions.merge())
            .addOnSuccessListener { Log.d("Firebase", "Avatar uploaded") }
            .addOnFailureListener { Log.e("Firebase", "$it") }
    }
    fun getSortedSightList(): StateFlow<List<SightFB>> {
        val sortedSightList = _sightList.asStateFlow()
        sortedSightList.value.sortByDescending {
            (it.date.substring(6, 10).toInt() * 10000) + (it.date.substring(3, 5)
                .toInt() * 100) + it.date.substring(0, 2).toInt()
        }
        return sortedSightList
    }


    fun getFilteredAvatarList(): StateFlow<MutableList<Avatar>> {
        val list = _avatarList.asStateFlow()
        list.value.retainAll { it.level <= _user.value.overallxp/100 }
        return list
    }


}