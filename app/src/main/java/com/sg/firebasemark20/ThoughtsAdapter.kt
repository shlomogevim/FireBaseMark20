package com.sg.firebasemark20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ThoughtsAdapter(val thoughts: ArrayList<Thought>) :
    RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindThought(thoughts[position])
    }

    override fun getItemCount() = thoughts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView?.findViewById<TextView>(R.id.listViewUsername)
        val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        val thoughtTxt = itemView?.findViewById<TextView>(R.id.listViewToughtTxt)
        val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikes)
        val likesImage = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)

        fun bindThought(thougt: Thought) {
            username?.text = thougt.userName
            thoughtTxt?.text = thougt.thoughtTxt
            numLikes?.text = thougt.numLikes.toString()
            timestamp?.text = thougt.timestamp?.toDate().toString()
            likesImage.setOnClickListener {
               FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thougt.documentId)
                   .update(NUM_LIKES,thougt.numLikes+1)
            }
        }

    }
}