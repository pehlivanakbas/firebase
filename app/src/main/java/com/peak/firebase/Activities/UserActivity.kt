package com.peak.firebase.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.peak.firebase.databinding.ActivityUserBinding
import com.peak.firebase.modal.Users
import com.squareup.picasso.Picasso
import java.util.UUID

class UserActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var adduserbinding: ActivityUserBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReferance: DatabaseReference = database.reference.child("MyUsers")
    lateinit var activityResult: ActivityResultLauncher<Intent>
    var imageUri: Uri? = null
    val fireBaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = fireBaseStorage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adduserbinding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(adduserbinding.root)

        supportActionBar?.title = "Add User"

        //register
        registerActivityForResult()

        adduserbinding.btnAddUser.setOnClickListener {

            uploadPhoto()

        }
        adduserbinding.userProfileImage.setOnClickListener {
            chooseImage()
        }

    }


    fun chooseImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResult.launch(intent)
            //activity result launcher

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResult.launch(intent)
            //activity result launcher
        }
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
                    Picasso.get().load(it).into(adduserbinding.userProfileImage)

                }
            }
        }
    }


   fun uploadPhoto(){

        adduserbinding.btnAddUser.isClickable=false
        adduserbinding.progressBarAddUser.visibility= View.VISIBLE

        val imageName=UUID.randomUUID().toString()
        val imageReferance=storageReference.child("images").child(imageName)
            imageUri?.let {
                uri ->
                imageReferance.putFile(uri).addOnSuccessListener {
                        Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
                    adduserbinding.btnAddUser.isClickable=true
                    adduserbinding.progressBarAddUser.visibility= View.INVISIBLE



                    val myUploaddedImage=storageReference.child("images").child(imageName)
                myUploaddedImage.downloadUrl.addOnSuccessListener {
                    url->
                    val imageurl=url.toString()
                    addUserToDatabase(imageurl, imageName )
                }
                }
                    .addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()

                    }
            }
    }


    fun addUserToDatabase(url:String,imageName:String) {
        val name: String = adduserbinding.etName.text.toString()
        val age: Int = adduserbinding.etAge.text.toString().toInt()
        val email: String = adduserbinding.etEmail.text.toString()
        val id: String = myReferance.push().key.toString()


        val user = Users(id, name, age, email,url,imageName)
        myReferance.child(id).setValue(user).addOnCompleteListener { task ->
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
}