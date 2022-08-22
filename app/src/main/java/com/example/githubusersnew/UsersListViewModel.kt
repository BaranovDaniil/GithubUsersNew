package com.example.githubusersnew

import androidx.lifecycle.ViewModel

class UsersListViewModel : ViewModel() {
    private val userRepository = UserRepository.get()
    val usersListLiveData = userRepository.getUsers()
    private val githubFetcher = GithubFetcher()
    var sinceParam = 0
    var currentPosition: Int = 0

    init {
        githubFetcher.fetchUsers(0)
    }
}