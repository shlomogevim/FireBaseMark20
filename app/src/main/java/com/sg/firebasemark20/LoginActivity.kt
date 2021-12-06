package com.sg.firebasemark20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth= FirebaseAuth.getInstance()
    }

    fun loginLoginClicked(view: View) {
        var email=loginEmailTxt.text.toString()
        var password=loginPasswordTxt.text.toString()



        email="aa@gmail.com"
        password="123456"


        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                Log.d(TAG,"sorry cannot login bebause --> ${it.localizedMessage}")
            }


    }
    fun loginCreateClicked(view: View) {
        val intent= Intent(this,CreateUserActivity::class.java)
        startActivity(intent)

    }
}