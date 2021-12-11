package com.sg.firebasemark20.activities

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.sg.firebasemark20.*
import com.sg.firebasemark20.R
import com.sg.firebasemark20.adapters.ThoughtsAdapter
import com.sg.firebasemark20.interfacrs.CommentsOptionClickListener
import com.sg.firebasemark20.interfacrs.ThoughtOptionClickListener
import com.sg.firebasemark20.model.Thought
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ThoughtOptionClickListener {

    lateinit var selectCategory: String
    lateinit var thoughtsAdapter: ThoughtsAdapter
    private val thoughts = arrayListOf<Thought>()
    private val thoughtCollectionRef = FirebaseFirestore.getInstance().collection(THOUGHT_REF)
    lateinit var thoughtListener: ListenerRegistration
    lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectCategory = FUNNY

        fab.setOnClickListener { view ->
            val intent = Intent(this, AddThougtActivity::class.java)
            intent.putExtra(INDICATE_USER_USE,0)
            startActivity(intent)
        }
        setUserBtns()

        thoughtsAdapter = ThoughtsAdapter(thoughts, this) { thought ->
            val commentIntent = Intent(this, CommentsActivity::class.java)
            commentIntent.putExtra(COMMENTS_KEY, thought.documentId)
            startActivity(commentIntent)
        }

        thoughtListView.adapter = thoughtsAdapter
        val layoutManager = LinearLayoutManager(this)
        thoughtListView.layoutManager = layoutManager

        auth = FirebaseAuth.getInstance()


    }


    override fun onResume() {
        super.onResume()
        updateUI()
    }

    fun updateUI() {
        if (auth.currentUser == null) {
            mainUserName.text="It's LogOut state"
            mainCrazyBtn.isEnabled = false
            mainFunnyBtn.isEnabled = false
            mainPopularBtn.isEnabled = false
            mainSeriousBtn.isEnabled = false
            fab.isEnabled = false
            thoughts.clear()
            thoughtsAdapter.notifyDataSetChanged()

        } else {
            val name = auth.currentUser!!.displayName
            mainUserName.text="It's LogIn state, current user : $name"
            mainCrazyBtn.isEnabled = true
            mainFunnyBtn.isEnabled = true
            mainPopularBtn.isEnabled = true
            mainSeriousBtn.isEnabled = true
            fab.isEnabled = true
            setListener()
        }
    }

    fun setListener() {
        if (selectCategory == POPULAR) {
            thoughtListener = thoughtCollectionRef
                .orderBy(NUM_LIKES, Query.Direction.DESCENDING)

                .addSnapshotListener(this) { snapshot, exception ->

                    if (exception != null) {
                        Log.e(
                            TAG, "there is exception--> ${exception.message}"
                        )
                    }
                    if (snapshot != null) {
                        parseData(snapshot)
                    }
                }
        } else {
            thoughtListener = thoughtCollectionRef
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(CATEGORY, selectCategory)
                .addSnapshotListener(this) { snapshot, exception ->

                    if (exception != null) {
                        Log.e(
                            TAG, "there is exception--> ${exception.message}"
                        )
                    }
                    if (snapshot != null) {
                        parseData(snapshot)
                    }
                }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu.getItem(0)
        if (auth.currentUser == null) {
            // log out state
            menuItem.title = "Login"
        } else {
            // log in state
            menuItem.title = "Logout"
           updateUI()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun thoughtOptionMenuClick(thought: Thought) {

        val thoughtRef =
            FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thought.documentId)

        //mainUserName.text=thought.userName

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.option_menu, null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDelelBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)
        builder.setView(dialogView)
            .setNegativeButton("Cancel") { _, _ -> }
        val ad = builder.show()

        deleteBtn.setOnClickListener {
            val collectionRef =
                FirebaseFirestore.getInstance().collection(THOUGHT_REF).document(thought.documentId)
                    .collection(COMMENTS_REF)

            deleteCollection(collectionRef, thought) { sucsses ->
                if (sucsses) {
                    thoughtRef.delete()
                        .addOnSuccessListener {
                            ad.dismiss()
                        }
                        .addOnFailureListener {
                            Log.e(TAG, "cannot delete thought because --> ${it.localizedMessage}")
                        }
                }
            }
        }


        editBtn.setOnClickListener {
                val intent = Intent(this, UpdateThoughtActivity::class.java)
                intent.putExtra(THOUGHT_DOC_ID_EXTRA, thought.documentId)
                intent.putExtra(THOUGHT_TEXT_EXSTRA, thought.thoughtTxt)
                ad.dismiss()
                startActivity(intent)
            }

    }

    fun deleteCollection(                    // to delet all comments of this thought
        collection: CollectionReference,
        thought: Thought,
        complete: (Boolean) -> Unit
    ) {
        collection.get().addOnSuccessListener { snapshot ->
            thread {
                val batch = FirebaseFirestore.getInstance().batch()
                for (document in snapshot) {
                    val docRef = FirebaseFirestore.getInstance().collection(THOUGHT_REF)
                        .document(thought.documentId)
                        .collection(COMMENTS_REF).document(document.id)
                    batch.delete(docRef)
                }
                batch.commit()
                    .addOnSuccessListener {
                        complete(true)
                    }
                    .addOnFailureListener {
                        Log.e(
                            TAG,
                            "cannot delete sub collection with bath because --> ${it.localizedMessage}"
                        )

                    }
            }
        }.addOnFailureListener {
            Log.e(TAG, "cannot retrive documents because --> ${it.localizedMessage}")

        }
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_login) {
            if (auth.currentUser == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                auth.signOut()
                updateUI()
            }
            return true
        }
        return false
    }



    fun parseData(snapshot: QuerySnapshot) {
        var thoughtTxt = ""
        var userId=""
        var numLikes = 0L
        var numComments = 0L
        thoughts.clear()
        for (document in snapshot.documents) {
            val data = document.data
            if (data != null) {
                val name = data[USERNAME] as String
                if (data[THOUGHT_TXT] != null) {
                    thoughtTxt = data[THOUGHT_TXT] as String
                }
                if (data[NUM_LIKES] != null) {
                    numLikes = data[NUM_LIKES] as Long
                }
                if (data[NUM_COMMENTS] != null) {
                    numComments = data[NUM_COMMENTS] as Long
                }
                val timestamp = document.getTimestamp(TIMESTAMP)
                val documentId = document.id
                if (data[USER_ID] != null) {
                     userId = data[USER_ID] as String
                }
                val newThought = Thought(
                    name,
                    timestamp,
                    thoughtTxt,
                    numLikes.toInt(),
                    numComments.toInt(),
                    documentId,
                    userId
                )
                thoughts.add(newThought)
            }
            thoughtsAdapter.notifyDataSetChanged()
        }
    }
    private fun setUserBtns() {
        user1Btn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(INDICATE_USER_USE,1)
            startActivity(intent)
        }
        user2Btn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(INDICATE_USER_USE,2)
            startActivity(intent)
        }
        user3Btn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(INDICATE_USER_USE,3)
            startActivity(intent)
        }
    }

    fun mainFunnyClicked(view: View) {
        mainFunnyBtn.isChecked = true
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectCategory = FUNNY
        thoughtListener.remove()
        setListener()
    }

    fun mainSeriousClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = true
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectCategory = SERIOUS
        thoughtListener.remove()
        setListener()
    }

    fun mainCreazyClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = true
        mainPopularBtn.isChecked = false
        selectCategory = CRAZY
        thoughtListener.remove()
        setListener()
    }

    fun mainPopularClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = true
        selectCategory = POPULAR
        thoughtListener.remove()
        setListener()
    }


}