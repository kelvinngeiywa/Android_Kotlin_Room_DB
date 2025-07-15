package com.forms

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.forms.dao.UserDao
import com.forms.database.AppDatabase
import com.forms.entities.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao


//    val userDao = db.getUserDao();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase:: class.java, "Users"
        ).build()
         userDao = db.getUserDao()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        addUser()
    }

    fun addUser()
    {
        val newUser= User(id=2,firstName = "Kelvin", lastName = "Ngeiywa")
        Log.d("Adding User", "${newUser.firstName} to be added!!")

        lifecycleScope.launch{
            try {
                userDao.addUser(newUser);
                Log.d("User Added", "${newUser.firstName} added successful!!")

            }
            catch (e: Exception){
                Log.e("ERROR", "Error adding user ${e.message}")

            }
        }


    }

    fun getUsers(){

        lifecycleScope.launch(){
            try{

            }
            catch (e: Exception){
                Log.e("FETCHING ERROR","Error fetching users: ${e.message}")
            }
            Log.d("Fetching Users", "Users to be fetched")
            val allUsers = userDao.getAll()

            Log.d("FETCHED","Fetched $allUsers")

        }

    }
}