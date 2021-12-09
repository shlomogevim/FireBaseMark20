package com.sg.firebasemark20.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.firebasemark20.*
import kotlinx.android.synthetic.main.activity_update_comment.*

class UpdateCommentActivity : AppCompatActivity() {

    lateinit var thoughtDocId: String
    lateinit var commentDocId: String
    lateinit var commentTxt: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_comment)

        thoughtDocId = intent.getStringExtra(THOUGHT_DOC_ID_EXTRA).toString()
        commentDocId = intent.getStringExtra(COMMENT_DOC_ID_EXTRA).toString()
        commentTxt = intent.getStringExtra(COMMENT_TEXT_EXSTRA).toString()

        updaeCommentTxt.setText(commentTxt)


    }



    fun updateCommentClick(view:View) {
        FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thoughtDocId)
            .collection(COMMENTS_REF).document(commentDocId)
            .update(COMMENTS_TXT,updaeCommentTxt.text.toString())
            .addOnSuccessListener {
                hideKeyboard()
                finish()
            }
            .addOnFailureListener {
                Log.d(TAG,"cannot edit comment -> ${it.localizedMessage}")
            }
    }
    private fun hideKeyboard() {
        val inputeManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputeManager.isAcceptingText) {
            inputeManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

}