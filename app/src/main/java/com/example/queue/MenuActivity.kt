package com.example.queue

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Welcome to the Menu Screen!"
        textView.textSize = 24f
        setContentView(textView)
    }
}
