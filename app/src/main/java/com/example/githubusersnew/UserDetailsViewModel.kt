package com.example.githubusersnew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class UserDetailsViewModel: ViewModel() {
    private val userRepository = UserRepository.get()
    private val userIdLiveData = MutableLiveData<Int>()

    var userLiveData: LiveData<User?> = Transformations.switchMap(userIdLiveData) {
            userId -> userRepository.getUser(userId)
    }

    fun loadUser(userId: Int) {
        userIdLiveData.value = userId
    }
}