package com.example.githubusersnew.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.githubusersnew.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}