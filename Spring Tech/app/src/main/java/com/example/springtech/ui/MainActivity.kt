package com.example.springtech.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.springtech.R
import com.example.springtech.io.ApiService
import com.example.springtech.io.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val ini = findViewById<Button>(R.id.ing)
        ini.setOnClickListener{
            performLogin()
        }

    }

    private fun performLogin(){
        val etEmail = findViewById<EditText>(R.id.correo).text.toString()
        val etPassword = findViewById<EditText>(R.id.contra).text.toString()

        val call = apiService.postlogin(etEmail, etPassword)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val loginResponse = response.body()
                    if (loginResponse == null){
                        Toast.makeText(applicationContext, "Respuesta nula del servidor", Toast.LENGTH_LONG).show()
                        return
                    } else {
                        Toast.makeText(applicationContext, "Todo bien", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error de red", Toast.LENGTH_LONG).show()
            }
        })

    }

}