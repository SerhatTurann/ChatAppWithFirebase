package com.stturan.chatapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.stturan.chatapplication.adapter.UserAdapter
import com.stturan.chatapplication.model.User
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    val userList = ArrayList<User>()
    var user:User? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        userRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getUserList()
        getAccountData()

        accountImage.setOnClickListener {
            val intent = Intent(applicationContext,ProfileActivity::class.java)
            intent.putExtra("userID",user!!.userID)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            alertDialog()
        }
    }

    private fun getAccountData(){
        database.collection("Users").whereEqualTo("userID", auth.currentUser!!.uid)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.get("userID").toString()
                    val name = document.get("userName").toString()
                    val imgUrl = document.get("profileImage").toString()

                    val _user = User(id, name, imgUrl)

                    user = _user
                }

                username.text = user!!.userName + "     "
                if(user!!.profileImage!=""){
                    Picasso.get().load(user!!.profileImage).into(accountImage)}

            }
    }
    private fun signOut(){
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun alertDialog(){
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Logout")
        alert.setMessage("Are you want to logout?")
        alert.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialogInterface, i ->
            signOut() })
        alert.setNegativeButton("No", DialogInterface.OnClickListener{ dialogInterface, i ->
            Toast.makeText(this,"Logout canceled.", Toast.LENGTH_LONG).show() })
        alert.show()
    }

    fun getUserList(){


        userList.clear()
        database.collection("Users").get().addOnSuccessListener { documents->
            for (document in documents){
                val id = document.get("userID").toString()
                val name = document.get("userName").toString()
                val imgUrl = document.get("profileImage").toString()

                val user = User(id,name,imgUrl)

                userList.add(user)
            }

            var userAdapter = UserAdapter(this, userList)

            userRecyclerView.adapter = userAdapter
        }
    }
}