package com.stturan.chatapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()


        btnSignUp.setOnClickListener {
            Register(view)
        }
    }

    private fun replaceActivity(activity: Activity){
        activity?.let {
            val intent = Intent(it,activity::class.java)
            startActivity(intent)
        }
    }

    fun Register(view: View){
        val name = textName.text.toString()
        val email = textEmail.text.toString()
        val password = textPassword.text.toString()
        val password2 = textPasswordConfirm.text.toString()

        if (password2==password){
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    var user: FirebaseUser? = auth.currentUser
                    var userID:String = user!!.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID)

                    var hashMap:HashMap<String,Any> = HashMap()
                    hashMap.put("userID",userID)
                    hashMap.put("userName",name)
                    hashMap.put("profileImage","")

                    database.collection("Users").document(userID)
                        .set(hashMap)
                        .addOnSuccessListener {
                            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
                            Navigation.findNavController(view).navigate(action)
                            Toast.makeText(this.context,"Sign up successed.", Toast.LENGTH_LONG).show() }
                        .addOnFailureListener { e ->
                            Toast.makeText(this.context,"Sign up failed.", Toast.LENGTH_LONG).show() }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this.context, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this.context,"Password does not match.", Toast.LENGTH_LONG).show()
        }
    }
}