package com.peak.firebase.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

import com.peak.firebase.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var signUpBinding: ActivitySignUpBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding=ActivitySignUpBinding.inflate(layoutInflater)
        val view=signUpBinding.root
        setContentView(view)


        signUpBinding.btnSignupinSignup.setOnClickListener {
            val userEmail=signUpBinding.etEmailSignup.text.toString()
            val userPassword=signUpBinding.etPasswordSignup.text.toString()
            signUpWithFirebase(userEmail,userPassword )
            finish()
        }

    }
    fun signUpWithFirebase(useremail:String,userpassword:String){
        auth.createUserWithEmailAndPassword(useremail,userpassword)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Your account has been created", Toast.LENGTH_SHORT).show()

                }
                else{
                    Toast.makeText(this,task.exception?.toString(), Toast.LENGTH_SHORT).show()

                }
            }
    }

}