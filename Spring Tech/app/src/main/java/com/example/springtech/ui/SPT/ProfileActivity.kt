package com.example.springtech.ui.SPT

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.springtech.R
import com.example.springtech.bd.BaseDatos
import com.example.springtech.io.ApiService
import com.example.springtech.io.Client
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileActivity : AppCompatActivity() {

    var db = BaseDatos(this)
    private val urlBase = "http://192.168.84.1:8000/api/v1/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlBase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_profile)

        val atras = findViewById<ImageButton>(R.id.atras)
        val btnActualizar = findViewById<Button>(R.id.editar)

        atras.setOnClickListener {
            finish()
        }

        btnActualizar.setOnClickListener {
            updateClient()
        }
    }

    private fun updateClient() {
        val name = findViewById<EditText>(R.id.nom).text.toString()
        val lastname = findViewById<EditText>(R.id.pat).text.toString()
        val motherlastname = findViewById<EditText>(R.id.mat).text.toString()
        val dni = findViewById<EditText>(R.id.dni).text.toString()
        val birthdate = findViewById<EditText>(R.id.cum).text.toString()

        val user = Client(name, lastname, motherlastname, dni, birthdate)

        lifecycleScope.launch {
            try {
                var datos = db.listarDatos()
                var token: String? = null
                var id: Int = 0

                for (i in 0 until datos.size) {
                    token = datos[i].token
                    id = datos[i].idClient
                }

                val response = service.updateClient(
                    id,
                    "Bearer ${token}",
                    user
                )

                if (response.isSuccessful) {
                    val result = response.body()

                    if (result != null && result.message == "Cliente Actualizado") {
                        Toast.makeText(this@ProfileActivity, "Cliente Actualizado", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(this@ProfileActivity, "Algo anda mal :(", Toast.LENGTH_LONG).show()

                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Error en la llamada al servicio", Toast.LENGTH_LONG).show()

                }

            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e("TAG", "Error en la llamada al servicio: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Error en la llamada al servicio: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}