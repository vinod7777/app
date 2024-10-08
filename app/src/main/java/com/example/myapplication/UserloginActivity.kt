package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import java.util.concurrent.TimeUnit

class UserloginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.userlogin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        val otpSend = findViewById<Button>(R.id.otpSend)
        val phone = findViewById<EditText>(R.id.phone)
        otpSend.setOnClickListener {
            val phoneNumber = phone.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendOTP(phoneNumber)
            } else {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sendOTP(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91"+phoneNumber)            // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)       // Timeout and unit
            .setActivity(this)                       // Activity (for callback binding)
            .setCallbacks(callbacks)                 // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Automatic verification can happen, but usually we want to handle it manually
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@UserloginActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token

            // Redirect to the OTP Verification Page
            val intent = Intent(this@UserloginActivity, VerifyotpActivity::class.java)
            intent.putExtra("storedVerificationId", verificationId)
            startActivity(intent)
            Toast.makeText(this@UserloginActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
        }
    }
}