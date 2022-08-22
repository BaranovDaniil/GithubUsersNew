package com.example.githubusersnew.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubusersnew.User
import java.util.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<User>>
    @Query("SELECT * FROM user WHERE id=(:id)")
    fun getUser(id: Int): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<User>)

    @Delete
    fun delete(user: User)
}