package com.sg.firebasemark20.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.firebasemark20.*
import kotlinx.android.synthetic.main.activity_comments.*
import java.lang.reflect.Field

class CommentsActivity : AppCompatActivity() {

    lateinit var thoughtDocumentID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        thoughtDocumentID= intent.getStringExtra(COMMENTS_KEY).toString()
       // Log.d(TAG," ID =======> $thoughtDocumentID ")


    }

    fun addCommentClick(view: View) {
        val commentText=enterCommentText.text.toString()
        val thoughrRef=FirebaseFirestore.getInstance()
            .collection(THOUGHT_REF).document(thoughtDocumentID)

        FirebaseFirestore.getInstance().runTransaction {transaction ->

            val thought=transaction.get(thoughrRef)
            val numComments= thought.getLong(NUM_COMMENTS)?.plus(1)
            transaction.update(thoughrRef, NUM_COMMENTS,numComments)

            val newCommentRef=FirebaseFirestore.getInstance().collection(THOUGHT_REF)
                .document(thoughtDocumentID).collection(COMMENTS_REF).document()

            val data=HashMap<String,Any>()
            data.put(COMMENTS_TXT,commentText)
            data.put(TIMESTAMP,FieldValue.serverTimestamp())
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString())

          transaction.set(newCommentRef,data)

        }
            .addOnSuccessListener {
                enterCommentText.setText("")
            }
            .addOnFailureListener { err->
                Log.d(TAG,"cannot create transaction because : ${err.localizedMessage}")
            }


    }
}