package com.group16.mytrips.viewModel

import com.group16.mytrips.data.Firebase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group16.mytrips.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val _userList = MutableStateFlow(emptyList<User>())
    val userList = _userList.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    fun setUser(user: User) {
        _currentUser.value = user
    }

    fun startListeningUserList() {
        Firebase.startListeningForUserList { userList ->
            viewModelScope.launch(Dispatchers.IO) {
                _userList.value = userList
            }
        }
    }

    fun setUserId() {
        Firebase.setUserId(_currentUser.value.id)
    }
}