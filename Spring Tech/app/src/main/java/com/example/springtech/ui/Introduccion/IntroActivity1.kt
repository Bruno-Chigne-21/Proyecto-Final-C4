package com.example.springtech.ui.Introduccion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.springtech.R

class IntroActivity1 : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro1)

        val siguiente = findViewById<ImageButton>(R.id.Sig)

        siguiente.setOnClickListener {
            val intent = Intent(this, IntroActivity2::class.java)
            startActivity(intent)
        }
    }
}