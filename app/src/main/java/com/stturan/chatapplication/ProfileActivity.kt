package com.stturan.chatapplication


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.stturan.chatapplication.model.User
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    var user: User? =null
    var user_id:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        user_id = intent.getStringExtra("userID").toString()

        getAccountData()

        imgBack.setOnClickListener {
            finish()
        }

        if (auth.currentUser!!.uid == intent.getStringExtra("userID")){
            imgEdit.visibility = View.VISIBLE
        } else {imgEdit.visibility = View.GONE}

        imgEdit.setOnClickListener {
           val intent = Intent(applicationContext,UpdateProfileActivity::class.java)
            startActivity(intent)
        }

    }

    fun getAccountData(){
        database.collection("Users").whereEqualTo("userID", user_id.toString())
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.get("userID").toString()
                    val name = document.get("userName").toString()
                    val imgUrl = document.get("profileImage").toString()

                    val _user = User(id, name, imgUrl)

                    user = _user
                }

                txtUserName.text = user!!.userName
                if(user!!.profileImage!=""){
                    Picasso.get().load(user!!.profileImage).into(imgProfile)}
            }
    }


}