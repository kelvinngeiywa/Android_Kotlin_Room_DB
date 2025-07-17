package com.forms.entities


import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String

)
