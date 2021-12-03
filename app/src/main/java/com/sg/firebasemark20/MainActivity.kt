package com.sg.firebasemark20

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sg.firebasemark20.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    lateinit var selectCategory: String
    lateinit var thoughtsAdapter: ThoughtsAdapter
    val thoughts= arrayListOf<Thought>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            val intent = Intent(this, AddThougtActivity::class.java)
            startActivity(intent)
        }
        thoughtsAdapter= ThoughtsAdapter((thoughts))
        thoughtListView.adapter=thoughtsAdapter
        val layoutManager=LinearLayoutManager(this)
        thoughtListView.layoutManager=layoutManager
    }

    fun mainFunnyClicked(view: View) {
        mainFunnyBtn.isChecked = true
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectCategory = FUNNY
        /*thoughtsListener.remove()
        setListener()*/
    }
    fun mainSeriousClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = true
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectCategory = SERIOUS
        /*thoughtsListener.remove()
        setListener()*/
    }
    fun mainCreazyClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = true
        mainPopularBtn.isChecked = false
        selectCategory = CRAZY
       /* thoughtsListener.remove()
        setListener()*/
    }
    fun mainPopularClicked(view:View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = true
        selectCategory = POPULAR
       /* thoughtsListener.remove()
        setListener()*/
    }


}