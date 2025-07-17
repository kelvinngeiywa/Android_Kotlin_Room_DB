package com.forms.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.forms.dao.UserDao
import com.forms.entities.User

@Database(entities = [User::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    companion object {
        const val NAME = "User_db"
    }

    abstract fun getUserDao(): UserDao
}