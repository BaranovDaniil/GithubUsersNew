package com.example.githubusersnew

import android.app.Application

class GithubUsersApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        UserRepository.initialize(this)
    }
}