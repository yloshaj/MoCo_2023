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

    private val _userList = MutableStateFlow(emptyList<User>().toMutableList())
    val userList = _userList.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    fun setUser(user: User) {
        _currentUser.value = user
    }

    fun startListeningUserList() {
        Firebase.startListeningForUserList { userList ->
            viewModelScope.launch(Dispatchers.Default) {
                _userList.value = userList as MutableList<User>
            }
        }
    }
    private val _bottomBarEnabled = MutableStateFlow(true)
    val bottomBarEnabled = _bottomBarEnabled.asStateFlow()

    fun setBottomBarEnabled(isEnabled: Boolean) {
        _bottomBarEnabled.value = isEnabled
    }

    fun setUserId() {
        Firebase.setUserId(_currentUser.value.id)
    }
}