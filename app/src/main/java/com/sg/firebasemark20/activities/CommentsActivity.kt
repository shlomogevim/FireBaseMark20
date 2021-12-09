package com.sg.firebasemark20.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sg.firebasemark20.*
import com.sg.firebasemark20.adapters.CommentAdapter
import com.sg.firebasemark20.interfacrs.CommentsOptionClickListener
import com.sg.firebasemark20.model.Comment
import kotlinx.android.synthetic.main.activity_comments.*
import java.lang.reflect.Field

class CommentsActivity : AppCompatActivity(), CommentsOptionClickListener {

    lateinit var thoughtDocumentID: String
    var comments = arrayListOf<Comment>()
    lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        thoughtDocumentID = intent.getStringExtra(COMMENTS_KEY).toString()
        commentAdapter = CommentAdapter(comments, this)
        commentsListview.adapter = commentAdapter
        val layoutManager = LinearLayoutManager(this)
        commentsListview.layoutManager = layoutManager

        FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thoughtDocumentID)
            .collection(COMMENTS_REF)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
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
                        val documentId = doco.id
                        val userId = data[USER_ID] as String
                        val newComment = Comment(name, timestame, commentTxt, documentId, userId)
                        comments.add(newComment)
                    }
                    commentAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun optionMenuClicked(comment: Comment) {
        //Log.d(TAG," comment --> ${comment.commentTxt}")
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.option_menu, null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDelelBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)

        builder.setView(dialogView)
            .setNegativeButton("Cancel") { _, _ -> }
        val ad = builder.show()
        deleteBtn.setOnClickListener {
            val thoughtRef =
                FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thoughtDocumentID)

            val commentRef =
                FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thoughtDocumentID)
                    .collection(COMMENTS_REF).document(comment.documentID)
            /* commentRef.delete()
                .addOnSuccessListener {
                    ad.dismiss()
                }
                .addOnFailureListener {
                    Log.d(TAG, "cannot create transaction because : ${it.localizedMessage}")

                } //  all this transition because because you want to make minus 1 in the counter comments
        }*/

            FirebaseFirestore.getInstance().runTransaction { transaction ->
                val thought = transaction.get(thoughtRef)
                val numComments = thought.getLong(NUM_COMMENTS)?.minus(1)
                transaction.update(thoughtRef, NUM_COMMENTS, numComments)
                transaction.delete(commentRef)
            }.addOnSuccessListener {
                ad.dismiss()
            }.addOnFailureListener {
                Log.d(TAG, "cannot create transaction because : ${it.localizedMessage}")
            }
        }
      editBtn.setOnClickListener {
          val intent= Intent(this,UpdateCommentActivity::class.java)
          intent.putExtra(THOUGHT_DOC_ID_EXTRA,thoughtDocumentID)
          intent.putExtra(COMMENT_DOC_ID_EXTRA,comment.documentID)
          intent.putExtra(COMMENT_TEXT_EXSTRA,comment.commentTxt)
          ad.dismiss()
          startActivity(intent)
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
            data.put(USER_ID, FirebaseAuth.getInstance().currentUser?.uid.toString())

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

    private fun hideKeyboard() {
        val inputeManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputeManager.isAcceptingText) {
            inputeManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }


}