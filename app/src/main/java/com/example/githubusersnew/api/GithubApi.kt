package com.example.githubusersnew.api

import com.example.githubusersnew.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GithubApi {

    @GET(
        "users?since=0"
    )
    fun fetchUsers(@Query("since") since: Int): Call<List<User>>
}