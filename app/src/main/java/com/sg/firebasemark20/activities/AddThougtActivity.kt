package com.sg.firebasemark20.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.firebasemark20.*
import kotlinx.android.synthetic.main.activity_add_thougt.*

class AddThougtActivity : AppCompatActivity() {

    var selectedCategory = FUNNY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_thougt)
    }

    fun addFunnyClick(view: View) {
        if (selectedCategory == FUNNY) {
            addFunnyBtn.isChecked = true
            return
        }
        addSeriosBtn.isChecked = false
        addCrazyBtn.isChecked = false
        selectedCategory = FUNNY
    }

    fun addSeriousClick(view: View) {
        if (selectedCategory == SERIOUS) {
            addSeriosBtn.isChecked = true
            return
        }
        addCrazyBtn.isChecked = false
        addFunnyBtn.isChecked = false
        selectedCategory = SERIOUS

    }

    fun addCrazyClick(view: View) {
        if (selectedCategory == CRAZY) {
            addCrazyBtn.isChecked = true
            return
        }
        addSeriosBtn.isChecked = false
        addFunnyBtn.isChecked = false
        selectedCategory = CRAZY

    }

    fun addPostClick(view: View) {
        //add post to firebase

        val data=HashMap<String,Any>()
        data.put(CATEGORY,selectedCategory)
        data.put(NUM_COMMENTS,0)
        data.put(NUM_LIKES,0)
        data.put(THOUGHT_TXT,addThoughtTxt.text.toString())
        data.put(TIMESTAMP,FieldValue.serverTimestamp())
        data.put(USERNAME,FirebaseAuth.getInstance().currentUser?.displayName.toString())

        FirebaseFirestore.getInstance().collection(THOUGHT_REF).add(data)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                Log.e(TAG,"could not add post exception because --> ${it.message}")
            }

    }


}