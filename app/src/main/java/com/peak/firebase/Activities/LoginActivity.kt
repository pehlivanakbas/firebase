package com.peak.firebase.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.peak.firebase.R
import com.peak.firebase.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding:ActivityLoginBinding

     val auth:FirebaseAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       loginBinding=ActivityLoginBinding.inflate(layoutInflater)
        val view=loginBinding.root
        setContentView(view)
        setOnClicklistener()


    }


    fun setOnClicklistener(){
        loginBinding.btnSignin.setOnClickListener {
            val userEmail=loginBinding.etEmailSignin.text.toString()
            val userPassword=loginBinding.etPasswordSignin.text.toString()
            signInWithFirebase(userEmail,userPassword)


        }
        loginBinding.btnSignup.setOnClickListener {
            val intent=Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)

        }
        loginBinding.btnforget.setOnClickListener {
            val intent=Intent(this,ForgetActivity::class.java)
            startActivity(intent)
        }

        loginBinding.btnSigninwithphonenumber.setOnClickListener {
            val intent=Intent(this,PhoneActivity::class.java)
            startActivity(intent)
        }
    }

    fun signInWithFirebase(useremail:String,userpassword:String){
        auth.signInWithEmailAndPassword(useremail, userpassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Login is Succesful",Toast.LENGTH_SHORT).show()
                    val intent=Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(applicationContext,task.exception?.toString(), Toast.LENGTH_SHORT).show()

                }
            }
    }

    override fun onStart() {
        super.onStart()
        val user=auth.currentUser
        //user infoları buraya kaydolur
        if(user!=null){
            val intent=Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }



}