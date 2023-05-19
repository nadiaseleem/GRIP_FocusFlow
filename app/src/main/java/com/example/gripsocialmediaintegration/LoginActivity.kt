package com.example.gripsocialmediaintegration

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gripsocialmediaintegration.databinding.LoginActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import java.util.regex.Pattern

open class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var name:String
    private lateinit var email:String
    private var photo:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = binding.emailEdt.text.toString()
        name = "User"
        firebaseAuth = FirebaseAuth.getInstance()
        onLoginClicked()
        onSignupClicked()
        onGoogleLoginClicked()
        onTwitterLoginClicked()
    }




    private fun onTwitterLoginClicked() {
        binding.twitterImv.setOnClickListener{
            loginWithTwitter()

        }
    }

    private fun loginWithTwitter() {
        val provider = OAuthProvider.newBuilder("twitter.com")
        val pendingResultTask = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener {
                    email = it.user?.email.toString()
                    name = it.user?.displayName.toString()
                    photo = it.user?.photoUrl.toString()
                    sendIntent(email,name,photo)
                }
                .addOnFailureListener {
                    Toast.makeText(this,it.message.toString(), Toast.LENGTH_SHORT).show()

                }
        } else {
            firebaseAuth
                .startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener {
                    email = it.user?.email.toString()
                    name = it.user?.displayName.toString()
                    photo = it.user?.photoUrl.toString()
                    sendIntent(email,name,photo)

                }
                .addOnFailureListener {
                    Toast.makeText(this,it.message.toString(), Toast.LENGTH_SHORT).show()

                }
        }
    }

    private fun onGoogleLoginClicked() {
        binding.googleImv.setOnClickListener{
            loginWithGoogle()

        }
    }

    private fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }


    private fun sendIntent(
        email: String = binding.emailEdt.text.toString(),
        name: String = "User",
        photoUrl: String? = null
    ) {
        val intent = Intent(this,ProfileActivity::class.java)
        intent.putExtra("email",email)
        intent.putExtra("name",name)
        intent.putExtra("photo",photoUrl)

        startActivity(intent)
    }

    private fun onSignupClicked() {
        binding.registerBtn.setOnClickListener {
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onLoginClicked() {
        binding.loginBtn.setOnClickListener {

            email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()

            if(!isDataValid()){
                return@setOnClickListener
            }
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    sendIntent(email)
                }else{
                    Toast.makeText(this,it.exception?.message.toString(),Toast.LENGTH_LONG).show()


                }
            }

        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun isDataValid():Boolean {
        val email = binding.emailEdt.text.toString().trim()
        val password = binding.passwordEdt.text.toString().trim()

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this,"Please enter your email", Toast.LENGTH_LONG).show()

            return false

        }else if (!isValidEmail(email)) {

            Toast.makeText(this,"Invalid email format", Toast.LENGTH_LONG).show()

            return false
        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(this,"Please enter password", Toast.LENGTH_LONG).show()

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
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account!=null){
                updatUI(account)
            }
        }else{
            Toast.makeText(this,task.exception?.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                email = account.email.toString()
                name = account.displayName.toString()
                photo = account.photoUrl.toString()
                sendIntent(email, name, photo)
            }else{
                Toast.makeText(this,it.exception?.message.toString(),Toast.LENGTH_SHORT).show()

            }
        }
    }


    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
           sendIntent(email, name, photo)
        }
    }

}