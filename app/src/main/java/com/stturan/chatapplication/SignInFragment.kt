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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth


        val guncelKullanici = auth.currentUser
        if(guncelKullanici != null){
            replaceActivity(UsersActivity())
        }

        textRegister.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            Navigation.findNavController(it).navigate(action)
        }

        btnSignIn.setOnClickListener {
            SignIn(view)
        }
    }

    private fun replaceActivity(activity: Activity){
        val intent = Intent(this.context,activity::class.java)
        startActivity(intent)
    }

    fun SignIn(view: View){

        val email = textEmail.text.toString()
        val password = textPassword.text.toString()

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){

                val guncelKullanici = auth.currentUser?.email.toString()

                Toast.makeText(this.context,"Welcome ${guncelKullanici}", Toast.LENGTH_LONG).show()
                replaceActivity(UsersActivity())
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this.context, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }

}