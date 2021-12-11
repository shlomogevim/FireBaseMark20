package com.sg.firebasemark20.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sg.firebasemark20.INDICATE_USER_USE
import com.sg.firebasemark20.R
import com.sg.firebasemark20.TAG
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth
    var indicateUserUse=0
    var email=""
    var password=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth= FirebaseAuth.getInstance()
        indicateUserUse=intent.getIntExtra(INDICATE_USER_USE,0)

        if (indicateUserUse==1){                           // need logout first
            loginEmailTxt.setText("aaa@gmail.com")     // and after that press the button 
            loginPasswordTxt.setText("111111")
        }
        if (indicateUserUse==2){
            loginEmailTxt.setText("bbb@gmail.com")
            loginPasswordTxt.setText("111111")
        }
        if (indicateUserUse==3){
            loginEmailTxt.setText("ccc@gmail.com")
            loginPasswordTxt.setText("111111")
        }
        if (indicateUserUse==4){                       // need to add button
            loginEmailTxt.setText("ddd@gmail.com")
            loginPasswordTxt.setText("111111")
        }

    }

    fun loginLoginClicked(view: View) {
         email=loginEmailTxt.text.toString()
         password=loginPasswordTxt.text.toString()


        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                Log.d(TAG,"sorry cannot login bebause --> ${it.localizedMessage}")
            }
    }
    fun loginCreateClicked(view: View) {
        val intent= Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
    }
}