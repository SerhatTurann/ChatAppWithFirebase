package com.stturan.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.stturan.chatapplication.adapter.ChatAdapter
import com.stturan.chatapplication.model.Chat
import com.stturan.chatapplication.model.User
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore
    val chatList = ArrayList<Chat>()
    var user: User? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        val user_id = intent.getStringExtra("userID")


        database.collection("Users").whereEqualTo("userID", user_id.toString())
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.get("userID").toString()
                    val name = document.get("userName").toString()
                    val imgUrl = document.get("profileImage").toString()

                    val _user = User(id, name, imgUrl)

                    user = _user
                }

                userNameChat.text = user!!.userName
                if(user!!.profileImage!=""){
                    Picasso.get().load(user!!.profileImage).into(imgProfileChat)}
            }

        var senderID:String = FirebaseAuth.getInstance().currentUser?.uid.toString()


        imgBackChat.setOnClickListener {
            val intent = Intent(this,UsersActivity::class.java)
            startActivity(intent)
            finish()
        }

        sendButton.setOnClickListener {
            val message = messageText.text.toString()
            sendMessage(senderID, getReceiverID(), message)
        }

        recyclerViewChat.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        readMessages()
    }



    fun getReceiverID() : String{

        return intent.getStringExtra("userID")!!
    }

    private fun sendMessage(senderID:String,receiverID:String,message:String){
        var hashMap = hashMapOf<String, Any>()
        hashMap.put("senderID",senderID)
        hashMap.put("receiverID",receiverID)
        hashMap.put("message",message)
        hashMap.put("date", Timestamp.now())
        database.collection("Chats").add(hashMap).addOnCompleteListener {
                task-> Toast.makeText(this, "Message sended.", Toast.LENGTH_SHORT).show()
                messageText.setText("")
        }.addOnFailureListener{
            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG).show()
        }
        readMessages()
    }


    private fun readMessages(){

        val userID = auth.currentUser!!.uid
        chatList.clear()
        database.collection("Chats")
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val rID = document.get("receiverID") as String
                    val msg = document.get("message") as String
                    val sID = document.get("senderID") as String

                    val _chat = Chat(sID ,rID,msg)

                    if(_chat.senderID.equals(userID)&&_chat.receiverID.equals(getReceiverID())||
                        _chat.senderID.equals(getReceiverID())&&_chat.receiverID.equals(userID))
                    {
                        println("true")
                        chatList.add(_chat) }else{
                            println("false")
                    }
                }
                var chatAdapter = ChatAdapter(this, chatList)
                recyclerViewChat.adapter = chatAdapter

                if (chatList.size!=0){
                    recyclerViewChat.smoothScrollToPosition(chatList.size-1)}
            }
            .addOnFailureListener { exception -> }
    }


}