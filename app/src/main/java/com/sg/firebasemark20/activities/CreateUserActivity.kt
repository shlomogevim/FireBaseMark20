package com.sg.firebasemark20.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.firebasemark20.*
import kotlinx.android.synthetic.main.activity_create_user.*

class CreateUserActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        auth = FirebaseAuth.getInstance()

    }

    fun createCreateClicked(view: View) {

        val email = createEmailTxt.text.toString()
        val password = createPasswordText.text.toString()
        val username = createUsernameTxt.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val changeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                result.user?.updateProfile(changeRequest)
                    ?.addOnFailureListener {
                        Log.d(TAG, "Cannot updae user --> ${it.localizedMessage}")
                    }
                val data = HashMap<String, Any>()
                data.put(USERNAME, username)
                data.put(DATE_CREATED, FieldValue.serverTimestamp())

                result.user?.let {
                    FirebaseFirestore.getInstance().collection(USERS_REF).document(it.uid)
                }
                    ?.set(data)
                    ?.addOnSuccessListener {
                        finish()
                    }
                    ?.addOnFailureListener {
                        Log.d(TAG, "Cannot add user --> ${it.localizedMessage}")
                    }

            }
            .addOnFailureListener { ex ->
                Log.d(TAG, "Cannot create user --> ${ex.message}")

            }


    }

    fun createCancelClicked(view: View) {
        finish()
    }
}