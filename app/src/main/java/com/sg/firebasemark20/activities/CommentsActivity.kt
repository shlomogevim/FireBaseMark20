package com.sg.firebasemark20.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sg.firebasemark20.*
import com.sg.firebasemark20.adapters.CommentAdapter
import com.sg.firebasemark20.model.Comment
import kotlinx.android.synthetic.main.activity_comments.*
import java.lang.reflect.Field

class CommentsActivity : AppCompatActivity() {

    lateinit var thoughtDocumentID: String
    var comments = arrayListOf<Comment>()
    lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        thoughtDocumentID = intent.getStringExtra(COMMENTS_KEY).toString()
        commentAdapter = CommentAdapter(comments)
        commentsListview.adapter = commentAdapter
        val layoutManager = LinearLayoutManager(this)
        commentsListview.layoutManager = layoutManager

        FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thoughtDocumentID)
            .collection(COMMENTS_REF)
            .orderBy(TIMESTAMP,Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d(TAG, "cannot create comment because : ${error.localizedMessage}")
                }
                if (snapshot != null) {
                    comments.clear()
                    for (doco in snapshot.documents) {
                        val data = doco.data
                        val name = data!![USERNAME] as String
                        val timestame = doco.getTimestamp(TIMESTAMP)
                        val commentTxt = data[COMMENTS_TXT] as String

                        val newComment = Comment(name, timestame, commentTxt)
                        comments.add(newComment)
                    }
                    commentAdapter.notifyDataSetChanged()

                }


            }
    }

    fun addCommentClick(view: View) {
        val commentText = enterCommentText.text.toString()
        val thoughrRef = FirebaseFirestore.getInstance()
            .collection(THOUGHT_REF).document(thoughtDocumentID)

        FirebaseFirestore.getInstance().runTransaction { transaction ->

            val thought = transaction.get(thoughrRef)
            val numComments = thought.getLong(NUM_COMMENTS)?.plus(1)
            transaction.update(thoughrRef, NUM_COMMENTS, numComments)

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHT_REF)
                .document(thoughtDocumentID).collection(COMMENTS_REF).document()

            val data = HashMap<String, Any>()
            data.put(COMMENTS_TXT, commentText)
            data.put(TIMESTAMP, FieldValue.serverTimestamp())
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString())

            transaction.set(newCommentRef, data)

        }
            .addOnSuccessListener {
                enterCommentText.setText("")
                hideKeyboard()
            }
            .addOnFailureListener { err ->
                Log.d(TAG, "cannot create transaction because : ${err.localizedMessage}")
            }
    }

    private fun hideKeyboard(){
        val inputeManager=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputeManager.isAcceptingText){
            inputeManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }
}