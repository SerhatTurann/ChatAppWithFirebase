package com.stturan.chatapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.stturan.chatapplication.*
import com.stturan.chatapplication.model.User

class UserAdapter(private val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.txtUserName.text = user.userName
        if(user.profileImage!=""){
            Picasso.get().load(user.profileImage).into(holder.imgUser)}

        holder.imgUser.setOnClickListener {
            val intent = Intent(this.context,ProfileActivity::class.java)
            intent.putExtra("userID",user.userID)
            intent.putExtra("userName",user.userName)
            this.context.startActivity(intent)
        }

        holder.layoutUser.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("userID",user.userID)
            intent.putExtra("userName",user.userName)
            context.startActivity(intent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtUserName: TextView = view.findViewById(R.id.userName)
        val txtTemp:TextView = view.findViewById(R.id.temp)
        val imgUser: ImageView = view.findViewById(R.id.userImage)
        val layoutUser: LinearLayout = view.findViewById(R.id.layoutUser)
    }
}