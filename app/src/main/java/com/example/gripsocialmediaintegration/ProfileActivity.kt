package com.example.gripsocialmediaintegration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gripsocialmediaintegration.databinding.ProfileActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding:ProfileActivityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val name = intent.getStringExtra("name").toString()
        binding.profileName.setText(name)
        val email = intent.getStringExtra("email").toString()
        binding.profileEmail.setText(email)
        val uri = intent.getStringExtra("photo").toString()

        if (uri.isNotEmpty()) {
            Picasso.get()
                .load(Uri.parse(uri))
                .placeholder(R.drawable.user_girl)
                .transform( CircleTransform())
                .into(binding.profilePicImv);
        }else{

            binding.profilePicImv.setImageResource(R.drawable.user_girl)

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        onSignoutClicked()
    }

    private fun onSignoutClicked() {
        binding.signoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            googleSignInClient.signOut()

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }
    }

}