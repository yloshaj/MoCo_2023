package com.group16.mytrips.viewModel

import androidx.lifecycle.ViewModel
import com.group16.mytrips.data.ModelClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel: ViewModel() {
    val modelClass = ModelClass()

    private var _sightList = MutableStateFlow(modelClass.sightList)
    val sightList = _sightList.asStateFlow()

    private var _avatar = MutableStateFlow(modelClass.listOfAvatars)
    val avatar = _avatar.asStateFlow()

    private var _xp = MutableStateFlow(230)
    val xp = _xp.asStateFlow()

    fun addXP (sightXP: Int) {
        _xp.value += sightXP
    }
}