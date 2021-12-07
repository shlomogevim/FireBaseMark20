package com.sg.firebasemark20.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sg.firebasemark20.R
import com.sg.firebasemark20.model.Comment

class CommentAdapter (private val comments:ArrayList<Comment>):RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindComment(comments[position])
    }

    override fun getItemCount()=comments.size

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        val commentUsername=itemView?.findViewById<TextView>(R.id.commentListUserName)
        val commentTimestamp=itemView?.findViewById<TextView>(R.id.commentListTimestap)
        val commentTxt=itemView?.findViewById<TextView>(R.id.commentListCommentText)

      fun bindComment(comment: Comment){
          commentUsername?.text=comment.username
          commentTimestamp?.text=comment.timestamp?.toDate().toString()
          commentTxt?.text=comment.commentTxt


      }
    }
}

/*class ThoughtsAdapter(val thoughts: ArrayList<Thought>,val itemClick:((Thought)-> Unit)) :
    RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view,itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindThought(thoughts[position])
    }

    override fun getItemCount() = thoughts.size

    inner class ViewHolder(itemView: View,val itemClick: (Thought) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val username = itemView?.findViewById<TextView>(R.id.listViewUsername)
        val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        val thoughtTxt = itemView?.findViewById<TextView>(R.id.listViewToughtTxt)
        val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikes)
        val likesImage = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)

        fun bindThought(thought: Thought) {
            username?.text = thought.userName
            thoughtTxt?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()
            timestamp?.text = thought.timestamp?.toDate().toString()
            itemView.setOnClickListener {itemClick(thought) }

            likesImage.setOnClickListener {
               FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thought.documentId)
                   .update(NUM_LIKES,thought.numLikes+1)
            }
        }

    }
}*/