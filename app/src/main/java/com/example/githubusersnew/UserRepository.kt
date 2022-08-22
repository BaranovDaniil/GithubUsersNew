package com.example.githubusersnew

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.githubusersnew.database.AppDatabase
import java.util.concurrent.Executors

private const val DATABASE_NAME = "users-database"

class UserRepository private constructor(context: Context) {
    private val userDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, DATABASE_NAME
    ).build()
    private val userDAO = userDatabase.userDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getUsers(): LiveData<List<User>> {
        return userDAO.getAll()
    }

    fun getUser(id: Int): LiveData<User?> {
        return userDAO.getUser(id)
    }

    fun addUser(user: User) {
        executor.execute {
            userDAO.insert(user)
        }
    }

    fun addUsers(users: List<User>) {
        executor.execute {
            userDAO.insert(users)
        }
    }


    companion object {
        private var INSTANCE: UserRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = UserRepository(context)
            }
        }

        fun get(): UserRepository {
            return INSTANCE ?: throw
            IllegalStateException("UserRepository must be initialized")
        }
    }
}