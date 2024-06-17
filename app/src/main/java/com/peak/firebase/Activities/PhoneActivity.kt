package com.peak.firebase.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.peak.firebase.R
import com.peak.firebase.databinding.ActivityPhoneBinding
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {
    lateinit var phoneActivityBinding:ActivityPhoneBinding
    val auth:FirebaseAuth=FirebaseAuth.getInstance()
    lateinit var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationCode=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneActivityBinding=ActivityPhoneBinding.inflate(layoutInflater)
        val view=phoneActivityBinding.root
        setContentView(view)

        phoneActivityBinding.btnsendSMS.setOnClickListener {
            val userphoneNo=phoneActivityBinding.editTextPhonenumber.text.toString()
            val options=PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(userphoneNo)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this@PhoneActivity)
                .setCallbacks(mCallbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        phoneActivityBinding.btnverify.setOnClickListener {
            signinWithSMSCode()
        }

        mCallbacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                 TODO("Not yet implemented")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                    verificationCode=p0
            }

        }
        }

    private fun signinWithSMSCode() {
val userEnterCode=phoneActivityBinding.etverifiyCode.text.toString()
        val credential=PhoneAuthProvider.getCredential(verificationCode,userEnterCode)
        //firebasse otomatik olalark bu ikisini kontrol eder
        signinWithPhoneCredential(credential)
    }
    fun signinWithPhoneCredential(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential).addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                val intent=Intent(this@PhoneActivity,MainActivity::class.java)
                startActivity(intent)
                finish()

            }
            else{
                Toast.makeText(applicationContext,"The code you entered is incorrect",Toast.LENGTH_SHORT).show()

            }
        }
    }
}
