package com.sg.firebasemark20.model

import com.google.firebase.Timestamp


data class Comment(
    val username:String,
    val timestamp:Timestamp?,
    val commentTxt:String
)