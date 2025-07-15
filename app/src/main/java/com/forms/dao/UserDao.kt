package com.forms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.forms.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun addUser(vararg user: User)

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>
}