package com.example.githubusersnew

import android.util.Log
import com.example.githubusersnew.api.GithubApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "loadUsers"

class GithubFetcher {
    private val githubApi: GithubApi
    private val userRepository = UserRepository.get()

    init {
        val retrofit: Retrofit =
            Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        githubApi = retrofit.create(GithubApi::class.java)
    }

    fun fetchUsers(sinceParam: Int): List<User> {
        var userItems: List<User> = mutableListOf()
        val githubRequest: Call<List<User>> = githubApi.fetchUsers(sinceParam)
        githubRequest.enqueue(object :
            Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e(TAG, "Failed to fetch users", t)
            }

            override fun onResponse(
                call: Call<List<User>>,
                response: Response<List<User>>
            ) {
                Log.d(TAG, "Response received")
                val usersResponse: List<User>? = response.body()
                userItems = usersResponse ?: mutableListOf()
                userRepository.addUsers(userItems)
            }
        })
        return userItems
    }
}