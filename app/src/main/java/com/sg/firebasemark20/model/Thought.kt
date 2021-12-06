package com.sg.firebasemark20.model


import com.google.firebase.Timestamp
import java.util.*

data class Thought constructor(
  val userName:String,
  val timestamp: Timestamp?,
  val thoughtTxt:String,
  val numLikes:Int,
  val numComments:Int,
  val documentId:String
)