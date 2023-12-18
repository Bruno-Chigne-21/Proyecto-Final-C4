package com.example.springtech.ui.SPT

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.springtech.R
import com.example.springtech.bd.BaseDatos
import com.example.springtech.ui.MainActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confi)

        val update = findViewById<TextView>(R.id.actu)
        val close = findViewById<TextView>(R.id.cerrar)
        val remove = findViewById<TextView>(R.id.borrar)
        val atras = findViewById<ImageButton>(R.id.atras)

        update.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        close.setOnClickListener {
            val baseDatos = BaseDatos(this)
            baseDatos.dropAll()
            startActivity(Intent(this, MainActivity::class.java))
        }

        remove.setOnClickListener {

        }

        atras.setOnClickListener {
            finish()
        }
    }
}