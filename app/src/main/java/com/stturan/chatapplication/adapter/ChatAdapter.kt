package com.stturan.chatapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stturan.chatapplication.*
import com.stturan.chatapplication.model.Chat

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var MESSAGE_TYPE_LEFT = 0
    private var MESSAGE_TYPE_RIGHT = 1
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType==MESSAGE_TYPE_LEFT){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)}

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]

        holder.txtUserName.text = chat.message
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.userNameMessage)
    }

    override fun getItemViewType(position: Int): Int {
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        var currUser = auth.currentUser!!
        if (currUser.uid == chatList[position].receiverID) {
            return MESSAGE_TYPE_LEFT
        } else {
            return MESSAGE_TYPE_RIGHT
        }
    }
}