package com.peak.firebase.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.peak.firebase.R
import com.peak.firebase.databinding.ActivityForgetBinding

class ForgetActivity : AppCompatActivity() {
    lateinit var forgetbinding: ActivityForgetBinding
    val auth:FirebaseAuth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgetbinding=ActivityForgetBinding.inflate(layoutInflater)
        val view=forgetbinding.root
        setContentView(view)

        forgetbinding.btnReset.setOnClickListener {
            val email=forgetbinding.etReset.text.toString()

            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"We sent a password reset mail to your mail adress",Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
}