package com.sg.firebasemark20.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.firebasemark20.COMMENTS_KEY
import com.sg.firebasemark20.NUM_LIKES
import com.sg.firebasemark20.R
import com.sg.firebasemark20.THOUGHT_REF
import com.sg.firebasemark20.activities.CommentsActivity
import com.sg.firebasemark20.interfacrs.ThoughtOptionClickListener
import com.sg.firebasemark20.model.Thought
import kotlin.collections.ArrayList

class ThoughtsAdapter(
    val thoughts: ArrayList<Thought>, val thoughtOptionListener: ThoughtOptionClickListener,
    val itemClick: ((Thought) -> Unit)
) :
    RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindThought(thoughts[position])
    }

    override fun getItemCount() = thoughts.size

    inner class ViewHolder(itemView: View, val itemClick: (Thought) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        val username = itemView?.findViewById<TextView>(R.id.listViewUsername)
        val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        val thoughtTxt = itemView?.findViewById<TextView>(R.id.listViewToughtTxt)
        val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikes)
        val likesImage = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)
        val commentImage = itemView?.findViewById<ImageView>(R.id.listViewCommentImage)
        val numComment = itemView?.findViewById<TextView>(R.id.numCommentsLabel)
        val optionImage = itemView?.findViewById<ImageView>(R.id.thoughtOptinImage)


        fun bindThought(thought: Thought) {
            optionImage.visibility = View.INVISIBLE
            username?.text = thought.userName
            thoughtTxt?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()
            timestamp?.text = thought.timestamp?.toDate().toString()
            numComment?.text = thought.numComments.toString()

            itemView.setOnClickListener { itemClick(thought) }

            //     commentImage.setOnClickListener { itemClick(thought)}


            likesImage.setOnClickListener {
                FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thought.documentId)
                    .update(NUM_LIKES, thought.numLikes + 1)
            }
            if (FirebaseAuth.getInstance().currentUser?.uid == thought.userId) {
                optionImage.visibility = View.VISIBLE

                optionImage?.setOnClickListener {
                    thoughtOptionListener.thoughtOptionMenuClick(thought)

                }
            }
        }

    }
}