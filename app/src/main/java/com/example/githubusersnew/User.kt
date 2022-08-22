package com.example.githubusersnew

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @SerializedName("id") @PrimaryKey val id: Int,
    @SerializedName("login") @ColumnInfo(name = "login") val login: String?,
    @SerializedName("html_url") @ColumnInfo(name = "url") val userUrl: String?,
    @SerializedName("avatar_url") @ColumnInfo(name="avatar_url") val avatarUrl: String?
)