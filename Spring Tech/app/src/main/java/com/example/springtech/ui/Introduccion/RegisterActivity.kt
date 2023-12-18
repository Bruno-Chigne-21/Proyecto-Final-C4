package com.example.springtech.ui.Introduccion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.springtech.R
import com.example.springtech.io.ApiService
import com.example.springtech.io.response.RegisterRequest
import com.example.springtech.io.response.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private var urlbase = "http://192.168.84.1:8000/api/v1/auth/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registrar = findViewById<Button>(R.id.btn_register)

        registrar.setOnClickListener{
            RegisterClient()
        }
    }

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlbase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)

    private fun RegisterClient(){
        val email = findViewById<EditText>(R.id.correo).toString()
        val password = findViewById<EditText>(R.id.contra).toString()
        val role = 1
        val nombre = findViewById<EditText>(R.id.nom).toString()
        val lastname = findViewById<EditText>(R.id.apePat).toString()
        val motherlastname = findViewById<EditText>(R.id.apeMat).toString()
        val dni = findViewById<EditText>(R.id.dni).toString()
        val birthdate = findViewById<EditText>(R.id.fec).toString()

        val client = RegisterRequest(email, password, role, nombre, lastname, motherlastname, dni, birthdate)
        val intent = Intent(this, LoginActivity::class.java)

        lifecycleScope.launch {
            try {
                val result: Response<RegisterResponse> = service.regClient(client)
                runOnUiThread {
                    if (result.isSuccessful){
                        Toast.makeText(this@RegisterActivity, "Registrado!", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
                Log.e("TAG", "Error en la llamada al servicio: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Error en la llamada al servicio: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}