package com.example.springtech.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.springtech.R
import com.example.springtech.bd.BaseDatos
import com.example.springtech.ui.Introduccion.IntroActivity1
import com.example.springtech.ui.SPT.HomeActivity

class MainActivity : AppCompatActivity() {

    private val handler = Handler()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        progressBar = findViewById(R.id.progressBar)

        showLoadingScreen()

        handler.postDelayed({

            hideLoadingScreen()

        }, 3000)
    }

    private fun showLoadingScreen() {
        // Hace visible la ProgressBar
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        progressBar.visibility = View.GONE

        val baseDatos = BaseDatos(this)

        if (baseDatos.contenido()) {

            startActivity(Intent(this, HomeActivity::class.java))

        } else {

            startActivity(Intent(this, IntroActivity1::class.java))

        }
        finish()
    }

}