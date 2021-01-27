package dev.abgeo.cupid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.abgeo.cupid.entity.User

class UserViewModel : ViewModel() {
    private val _currentUserLiveData = MutableLiveData<User>()
    val currentUserLiveData: LiveData<User>
        get() = _currentUserLiveData

    private val _personLiveData = MutableLiveData<User>()
    val personLiveData: LiveData<User>
        get() = _personLiveData

    fun postCurrentUser(user: User) {
        _currentUserLiveData.postValue(user)
    }

    fun postPerson(user: User) {
        _personLiveData.postValue(user)
    }
}
