package com.stturan.chatapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.stturan.chatapplication.model.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.activity_update_profile.kullaniciIsmi
import kotlinx.android.synthetic.main.activity_users.*
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {

    var imageUri : Uri? = null
    var imageBitmap : Bitmap? = null
    var user:User? =null

    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        getAccountData()

        imageBack.setOnClickListener {
            finish()
        }

        doneImage.setOnClickListener {
            Edit(it)

        }

        updateImage.setOnClickListener {
            ChooseImage(it)
        }
    }

    fun getAccountData(){
        database.collection("Users").whereEqualTo("userID", auth.currentUser!!.uid)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.get("userID").toString()
                    val name = document.get("userName").toString()
                    val imgUrl = document.get("profileImage").toString()

                    val _user = User(id, name, imgUrl)

                    user = _user
                }

                editName.setText(user!!.userName)
                if(user!!.profileImage!=""){
                    Picasso.get().load(user!!.profileImage).into(updateImage)}
            }

    }

    fun ChooseImage(view: View){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //permission not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        }else{
            //permission granted
            val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode==1){
            if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode==2 && resultCode == Activity.RESULT_OK && data != null){

            imageUri = data.data
            if (imageUri!=null){

                if (Build.VERSION.SDK_INT >28){
                    val source = ImageDecoder.createSource(this.contentResolver,imageUri!!)
                    imageBitmap = ImageDecoder.decodeBitmap(source)
                    updateImage.setImageBitmap(imageBitmap)
                }else{
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                    updateImage.setImageBitmap(imageBitmap)
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun Edit(view: View){

        //storage stuff

        //UUID universal unique id
        val uuid = auth.currentUser!!.uid.toString()
        val nameImage = "${uuid}.jpg"

        val reference = storage.reference

        val imageReference = reference.child("images").child(nameImage)


        if(imageUri != null){
            imageReference.putFile(imageUri!!).addOnSuccessListener {
                val downloadImageReference = reference.child("images").child(nameImage)
                downloadImageReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    //database stuff

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("profileImage",downloadUrl)
                    postHashMap.put("userID",uuid)
                    postHashMap.put("userName",editName.text.toString())

                    println(postHashMap.size)

                    database.collection("Users").document(uuid)
                        .set(postHashMap).addOnCompleteListener { task->
                        if (task.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener{
                        Toast.makeText(applicationContext,it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener{
                    Toast.makeText(applicationContext,it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }


    }

}