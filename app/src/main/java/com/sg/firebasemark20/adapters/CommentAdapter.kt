package com.sg.firebasemark20.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.sg.firebasemark20.R
import com.sg.firebasemark20.interfacrs.CommentsOptionClickListener
import com.sg.firebasemark20.model.Comment

class CommentAdapter(
    private val comments: ArrayList<Comment>,
    val commentOptionListener: CommentsOptionClickListener
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindComment(comments[position])
    }

    override fun getItemCount() = comments.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val commentUsername = itemView?.findViewById<TextView>(R.id.commentListUserName)
        val commentTimestamp = itemView?.findViewById<TextView>(R.id.commentListTimestap)
        val commentTxt = itemView?.findViewById<TextView>(R.id.commentListCommentText)
        val optionImage = itemView?.findViewById<ImageView>(R.id.commentOptionImage)

        fun bindComment(comment: Comment) {
            optionImage?.visibility = View.INVISIBLE
            commentUsername?.text = comment.username
            commentTimestamp?.text = comment.timestamp?.toDate().toString()
            commentTxt?.text = comment.commentTxt

            if (FirebaseAuth.getInstance().currentUser?.uid == comment.userID) {
                optionImage?.visibility = View.VISIBLE
            }

            optionImage?.setOnClickListener {
                        commentOptionListener.optionMenuClicked(comment)
            }

        }
    }
}

