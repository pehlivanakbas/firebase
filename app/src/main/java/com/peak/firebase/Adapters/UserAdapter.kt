package com.peak.firebase.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.peak.firebase.Activities.UpdateUserActivity
import com.peak.firebase.databinding.UsersItemBinding
import com.peak.firebase.modal.Users
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class UsersAdapter(
    var context: Context,
    val userList:ArrayList<Users>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(val adapterBinding: UsersItemBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        //binding yapısı adapterde önce userviewholder:recyclerview.viewHolder yazılır



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding=UsersItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       holder.adapterBinding.tvname.text=userList[position].userNAme
       holder.adapterBinding.tvAge.text=userList[position].userAge.toString()
       holder.adapterBinding.tvEmail.text=userList[position].userEmail

        val imageUrl=userList[position].url
        Picasso.get().load(imageUrl).into(holder.adapterBinding.imageView, object:Callback{
            override fun onSuccess() {
holder.adapterBinding.progressBar.visibility=View.INVISIBLE
            }

            override fun onError(e: Exception?) {
Toast.makeText(context,e?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        })


        holder.adapterBinding.linearLayout.setOnClickListener {
            //silme işlemi yaspacak
            val intent=Intent(context, UpdateUserActivity::class.java)
            intent.putExtra("id",userList[position].userId)
            intent.putExtra("name",userList[position].userNAme)
            intent.putExtra("ager",userList[position].userAge)
            intent.putExtra("email",userList[position].userEmail)
            intent.putExtra("imageUrl",imageUrl)
            intent.putExtra("imageName",userList[position].imageName)
            context.startActivity(intent)
        }


    }
    fun getUserId(position: Int): String {
        return userList[position].userId
    }

    fun getImageName(position: Int):String{
        return userList[position].imageName
    }

}
