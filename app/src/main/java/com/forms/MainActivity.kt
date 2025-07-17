package com.forms

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.forms.dao.UserDao
import com.forms.database.AppDatabase
import com.forms.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var displayUsers: TextView
    private lateinit var oneUserView: TextView
    private lateinit var addUserButton: Button

    private lateinit var viewOneUserButton: Button

    private lateinit var userCard: LinearLayout

    private val MIGRATION_1_2 = object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase){
            database.execSQL(
                """
                    CREATE TABLE users (
                          id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                          firstName TEXT NOT NULL,
                          lastName TEXT NOT NULL
                    )
                """.trimIndent()
            )
            database.execSQL(
                """
                    INSERT INTO users(id,firstName,lastName)
                    SELECT id, firstName,lastName FROM users
                """.trimIndent()
            )
            database.execSQL("DROP TABLE user")
            database.execSQL("ALTER TABLE users RENAME TO user")
        }
    }


//    val userDao = db.getUserDao();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase:: class.java, "Users"
        ).addMigrations(MIGRATION_1_2)
            .build()
         userDao = db.getUserDao()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        oneUserView= findViewById(R.id.oneUserView)
        addUserButton = findViewById(R.id.addUserButton)

        viewOneUserButton = findViewById(R.id.viewOneUserButton)

        userCard = findViewById(R.id.userCard)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getUsers()

        addUserButton.setOnClickListener{
            addUser()

        }





        viewOneUserButton.setOnClickListener{
            getUserById(42)

        }

    }

    fun addUser()
    {
        val newUser= User(firstName = "Kelvin", lastName = "Ngeiywa")
        Log.d("Adding User", "${newUser.firstName} to be added!!")

        lifecycleScope.launch(Dispatchers.IO){
            try {
                withContext(Dispatchers.IO){
                    userDao.addUser(newUser);
                    //Toast.makeText(this@MainActivity, "${newUser.firstName} added successfully", Toast.LENGTH_SHORT).show()
                }
                withContext(Dispatchers.Main){

                    Toast.makeText(this@MainActivity, "${newUser.firstName} added successfully", Toast.LENGTH_SHORT).show()
                }

                Log.d("User Added", "${newUser.firstName} added successful!!")
                getUsers()

            }
            catch (e: Exception){
                Log.e("ERROR", "Error adding user ${e.message}")

            }
        }


    }

    fun getUsers(){

        lifecycleScope.launch(Dispatchers.IO){
            try{
                Log.d("Fetching Users", "Users to be fetched")
                val allUsers = userDao.getAll()
                Log.d("FETCHED","Fetched $allUsers")

                withContext(Dispatchers.Main){
                    userCard.removeAllViews()

                    if(allUsers.isNotEmpty()){
                        allUsers.forEach {
                            user ->
                            val userLayout = LinearLayout(this@MainActivity)
                            userLayout.orientation = LinearLayout.HORIZONTAL
                            userLayout.setPadding(3)



                            val userInfo = TextView(this@MainActivity)
                            userInfo.text = "Id: ${user.id},\nFirst Name:${user.firstName},\nLast Name:${user.lastName}"
                            userInfo.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2F)
                            userInfo.setPadding(8,0,8,0)
                            userInfo.isSingleLine = false
                            userInfo.height = 200
                            userLayout.addView(userInfo)

                            val editButton = Button(this@MainActivity)
                            editButton.text = "Edit"
                            editButton.setOnClickListener{
                                updateUser(user.id)
                            }

                            userLayout.addView(editButton)

                            val deleteBtn = Button(this@MainActivity)
                            deleteBtn.text = "Delete"
                            deleteBtn.setOnClickListener{
                                deleteOneUser(user.id)
                            }

                            userLayout.addView(deleteBtn)

                            userCard.addView(userLayout)
                        }
                        Log.d("USERS DISPLAYED", "Users displayed")

                    }
                    else{

                        val emptyUsersView = TextView(this@MainActivity)
                        emptyUsersView.text = "No user found"

                        userCard.addView(emptyUsersView)
                        Log.e("USERS DISPLAYED","No user found")

                    }

                }

            }
            catch (e: Exception){
                Log.e("FETCHING ERROR","Error fetching users: ${e.message}")
                withContext(Dispatchers.Main){
                    displayUsers.text = "Errors fetching user"
                }
            }

        }

    }

    fun getUserById(id: Int){
        lifecycleScope.launch(Dispatchers.IO){
            try {
                Log.d("Fetching User by Id", "Fetching user by Id")
                val fetchedUser = userDao.getUserByid(id)
                Log.d("FETCHED","Fetched user $fetchedUser")

                withContext(Dispatchers.Main){
                    if(true){
                        displayUsers.visibility = View.INVISIBLE
                        oneUserView.text = "Id: ${fetchedUser.id}, First Name: ${fetchedUser.firstName}, Last Name: ${fetchedUser.lastName}"
                        Log.d("USER FOUND","User with $id found")

                    } else{
                        oneUserView.text = "User not found"
                        Log.e("USER NOT FOUND","User with $id not found")

                    }
                }

            }
            catch (e: Exception){
                Log.e("USER UPDATE ERROR","Error updating user: ${e.message}")
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Error updating user", Toast.LENGTH_SHORT).show()

                }

            }
        }
    }

    fun updateUser(id: Int)
    {
        lifecycleScope.launch(Dispatchers.IO){
            try{
                val toBeUpdatedUser = userDao.getUserByid(id)
                 if(true){
                     val updatedUser= toBeUpdatedUser.copy(
                         firstName = "Kelvin",
                         lastName = "Ngeiywa"
                     )
                     userDao.updateUser(updatedUser)
                     Log.d("USER UPDATED","User with id: $id updated successfully!!")
                     withContext(Dispatchers.Main){
                         Toast.makeText(this@MainActivity, "$id Updated successfully!!!", Toast.LENGTH_SHORT).show()
                     }
                     getUsers()

                 } else{
                     Log.e("USER NOT FOUND","User with $id not found")
                     withContext(Dispatchers.Main){
                         Toast.makeText(this@MainActivity, "User $id not found", Toast.LENGTH_SHORT).show()

                     }
                 }

            }
            catch (e: Exception){
                Log.e("USER UPDATE ERROR","Error updating user: ${e.message}")
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Error updating user", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    fun deleteOneUser(id: Int){
        lifecycleScope.launch(Dispatchers.IO){
            try{
                Log.d("Fetching User to delete", "Fetching user to delete")
                val fetchedUser = userDao.getUserByid(id)
                Log.d("FETCHED","Fetched user to delete $fetchedUser")
                if (true){
                    userDao.deleteUser(fetchedUser)
                    Log.d("DELETED","Delete $fetchedUser")
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "${fetchedUser.firstName} deleted!!", Toast.LENGTH_SHORT).show()

                    }
                    getUsers()


                }
                else{
                    oneUserView.text = "User not found"
                    Log.e("USER NOT FOUND","User with $id not found")
                }

            }
            catch (e: Exception){
                Log.e("USER DELETE ERROR","Error deleting user: ${e.message}")
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Error deleting user", Toast.LENGTH_SHORT).show()

                }

            }

        }
    }

    fun deleteAllUsers(){
        lifecycleScope.launch(Dispatchers.IO){
            try{
                userDao.deleteAllUsers();
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Users deleted", Toast.LENGTH_SHORT).show()

                }
                Log.d("USERS DELETED","Users deleted successfully!! ")
                getUsers()

            }
            catch (e: Exception){
                Log.e("USERS DELETE ERROR","Error occurred while deleting users: ${e.message} ")
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "Users deletion error", Toast.LENGTH_SHORT).show()

                }

            }

        }
    }
}