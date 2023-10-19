package com.example.springtech

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.springtech.Introduccion.LoginActivity
import com.example.springtech.SPT.HomeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userLoggedIn = checkUserLoggedIn()

        if (userLoggedIn) {
            showMainScreen()
        } else {
            showLoginScreen()
        }
    }

    private fun checkUserLoggedIn(): Boolean {
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("userLoggedIn", false)
    }

    private fun showMainScreen() {

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}