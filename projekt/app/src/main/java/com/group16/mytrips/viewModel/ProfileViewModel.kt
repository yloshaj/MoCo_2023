package com.group16.mytrips.viewModel

import com.group16.mytrips.data.Firebase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group16.mytrips.data.Avatar
import com.group16.mytrips.data.SightFB
import com.group16.mytrips.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {


    private val _user = MutableStateFlow(User())
    val user = _user.asStateFlow()

    private val _avatarList = MutableStateFlow(emptyList<Avatar>())
    val avatarList = _avatarList.asStateFlow()

    private val _sightList = MutableStateFlow(emptyList<SightFB>())
    val sightList = _sightList.asStateFlow()


    fun startListeningForUser() {
        Firebase.startListeningForUser { user ->
            viewModelScope.launch(Dispatchers.IO) {
                _user.value = user
            }
        }
    }

    fun startListeningForSightList() {
        Firebase.startListeningForSightList { sights ->
            viewModelScope.launch(Dispatchers.IO) {
                _sightList.value = sights
            }
        }
    }

    fun startListeningForAvatarList() {
        Firebase.startListeningForAvatarList { avatare ->
            viewModelScope.launch(Dispatchers.IO) {
                _avatarList.value = avatare
            }
        }
    }

    fun updateAvatar(path: Int) = viewModelScope.launch(Dispatchers.IO) {
        Firebase.updateAvatar(path)
    }

    fun updateLiked(sight: SightFB) = viewModelScope.launch(Dispatchers.IO) {
        Firebase.updateIcon(sight)
    }

    fun startListeningForData() {
        startListeningForUser()
        startListeningForSightList()
        startListeningForAvatarList()
    }
}