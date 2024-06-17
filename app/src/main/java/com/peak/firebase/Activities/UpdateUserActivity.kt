package com.peak.firebase.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.peak.firebase.databinding.ActivityUpdateUserBinding
import com.peak.firebase.modal.Users
import com.squareup.picasso.Picasso
import java.util.UUID

class UpdateUserActivity : AppCompatActivity() {
    lateinit var updateUserBinding:ActivityUpdateUserBinding

        val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    val myreferance:DatabaseReference=database.reference.child("MyUsers")

    lateinit var activityResult: ActivityResultLauncher<Intent>
    var imageUri: Uri? = null
    val fireBaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = fireBaseStorage.reference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateUserBinding=ActivityUpdateUserBinding.inflate(layoutInflater)
        val view=updateUserBinding.root
        setContentView(view)

        supportActionBar?.title="Update User"
        //register
        registerActivityForResult()
        getAndSetData()

        updateUserBinding.btnUpdateUser.setOnClickListener {
        updatePhoto()

        }
        updateUserBinding.updateuserProfileImage.setOnClickListener {
            chooseImage()
        }
        }

    fun chooseImage() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResult.launch(intent)
            //activity result launcher

        }
    fun registerActivityForResult() {
        activityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val resultCode = result.resultCode
            val imageData = result.data
            if (resultCode == RESULT_OK && imageData != null) {
                imageUri = imageData.data
                //Pİcasso Library
                imageUri.let {
                    Picasso.get().load(it).into(updateUserBinding.updateuserProfileImage)

                }
            }
        }
    }
    fun updatePhoto(){

        updateUserBinding.btnUpdateUser.isClickable=false
        updateUserBinding.progressBarupdate.visibility= View.VISIBLE

        val imageName= intent.getStringExtra("imageName").toString()
        val imageReferance=storageReference.child("images").child(imageName)
        imageUri?.let {
                uri ->
            imageReferance.putFile(uri).addOnSuccessListener {
                Toast.makeText(this,"Image UPdated",Toast.LENGTH_LONG).show()
                updateUserBinding.btnUpdateUser.isClickable=true
                updateUserBinding.progressBarupdate.visibility= View.INVISIBLE


                val myUploaddedImage=storageReference.child("images").child(imageName)
                myUploaddedImage.downloadUrl.addOnSuccessListener {
                        url->
                    val imageurl=url.toString()
                    updateData(imageurl, imageName )
                }
            }
                .addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()

                }
        }
    }
    fun addUserToDatabase(url:String,imageName:String) {
        val name: String = updateUserBinding.etUpdateName .text.toString()
        val age: Int = updateUserBinding.etUpdateAge .text.toString().toInt()
        val email: String = updateUserBinding.etUpdateEmail.text.toString()
        val id: String = myreferance.push().key.toString()


        val user = Users(id, name, age, email,url,imageName)
        myreferance.child(id).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "The user has been added to the Database",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


    fun getAndSetData(){
        val name=intent.getStringExtra("name")
        val age=intent.getIntExtra("age",0).toString()
        val email=intent.getStringExtra("email")
        val imageUrl=intent.getStringExtra("imageUrl")

        updateUserBinding.etUpdateName.setText(name)
        updateUserBinding.etUpdateAge.setText(age)
        updateUserBinding.etUpdateEmail.setText(email)
        Picasso.get().load(imageUrl).into(updateUserBinding.updateuserProfileImage)

    }
    fun updateData(imageurl:String,imageName:String){
        val updatedNaAme=updateUserBinding.etUpdateName.text.toString()
        val updatedage=updateUserBinding.etUpdateAge.text.toString().toInt()
        val updatedmail=updateUserBinding.etUpdateEmail.text.toString()
        val userID=intent.getStringExtra("id").toString()
        val userMap= mutableMapOf<String,Any>()
        userMap["userId"] =userID
        userMap["userNAme"]=updatedNaAme
        userMap["userAge"]=updatedage
        userMap["userEmail"]=updatedmail
        userMap["url"]=imageurl
        myreferance.child(userID).updateChildren(userMap).addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"The user has been updated",Toast.LENGTH_LONG).show()
                updateUserBinding.btnUpdateUser.isClickable=true
                updateUserBinding.progressBarupdate.visibility= View.INVISIBLE
                finish()
            }
        }
    }

    }
