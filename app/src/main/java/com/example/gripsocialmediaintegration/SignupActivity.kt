package com.example.gripsocialmediaintegration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.example.gripsocialmediaintegration.databinding.SignupActivityBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: SignupActivityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupActivityBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        onSignupClicked()
        onLoginClicked()

    }


    private fun onSignupClicked() {
        binding.registerBtn.setOnClickListener {

             val email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()

            if(!isDataValid()){
                return@setOnClickListener
            }

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("email",email)
                    intent.putExtra("name","User")

                    startActivity(intent)
                }else{
                    Toast.makeText(this,it.exception?.message.toString(),Toast.LENGTH_LONG).show()

                }
            }


        }
    }

    private fun onLoginClicked() {
        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun isDataValid():Boolean {
      val  email = binding.emailEdt.text.toString().trim()
        val password = binding.passwordEdt.text.toString().trim()
        val passwordrepeat = binding.repeatPasswordEdt.text.toString().trim()

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_LONG).show()

            return false

        }else if (!isValidEmail(email)) {

            Toast.makeText(this,"Invalid email format",Toast.LENGTH_LONG).show()

            return false
        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show()

            return false

        }else if(TextUtils.isEmpty(passwordrepeat)){

            Toast.makeText(this,"Please repeat password",Toast.LENGTH_LONG).show()
            return false

        }else if(password != passwordrepeat) {

            Toast.makeText(this,"Passwords don't match",Toast.LENGTH_LONG).show()

            return false

        }else if (password.length < 6){

            Toast.makeText(this,"Password must be at least 6 characters",Toast.LENGTH_LONG).show()

            return false

        }
        return true

    }


    fun isValidEmail(str: String): Boolean{
        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }



}