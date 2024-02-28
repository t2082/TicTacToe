package com.akuro.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var singlePlayerButton: TextView
    private lateinit var multiPlayerButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        singlePlayerButton = findViewById<TextView>(R.id.btn_singleplay)
        multiPlayerButton = findViewById<TextView>(R.id.btn_multiplay)

        singlePlayerButton.setOnClickListener(){
            singlePlay()
        }
        multiPlayerButton.setOnClickListener(){
            multiPlay()
        }
    }

    private fun singlePlay(){
        val intent = Intent(this, SinglePlayActivity::class.java)
        startActivity(intent)
    }
    private fun multiPlay(){
        val intent = Intent(this, MultiplayActivity::class.java)
        startActivity(intent)
    }

}