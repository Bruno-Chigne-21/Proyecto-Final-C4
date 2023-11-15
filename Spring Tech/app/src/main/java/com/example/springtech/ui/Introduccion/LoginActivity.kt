package com.example.springtech.ui.Introduccion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.springtech.R
import com.example.springtech.bd.BaseDatos
import com.example.springtech.bd.Usuario
import com.example.springtech.io.ApiService
import com.example.springtech.io.response.LoginRequest
import com.example.springtech.io.response.LoginResponse
import com.example.springtech.ui.SPT.HomeActivity
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText

    private var urlbase = "http://192.168.84.1:8000/api/v1/auth/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val ini = findViewById<Button>(R.id.login)
        ini.setOnClickListener{
            LogIn(it)
        }
    }

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlbase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)

    private fun LogIn(view: View){
        val correo = findViewById<EditText>(R.id.correo).text.toString()
        val contra = findViewById<EditText>(R.id.contra).text.toString()

        val user = LoginRequest(correo, contra)

        val intent = Intent(this, HomeActivity::class.java)

        lifecycleScope.launch {
            try {
                val result: LoginResponse = service.login(user)

                runOnUiThread {
                    if (result.message == "Autenticado correctamente"){
                        Toast.makeText(view.context, "Logeado!", Toast.LENGTH_LONG).show()
                        GuardarDatos(correo, contra)
                        verDatos()
                        startActivity(intent)
                    } else{
                        Toast.makeText(view.context, "Algo anda mal :(", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
                Log.e("TAG", "Error en la llamada al servicio: ${e.message}")
                Toast.makeText(view.context, "Error en la llamada al servicio: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun GuardarDatos(email: String, password: String) {
        try {
            var db = BaseDatos(this)
            var usu = Usuario()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                usu.email = email
                usu.password = password
                val resultado: String = db.insertarDatos(usu)

                if (resultado == "0") {
                    Toast.makeText(this, "Se guardó con éxito! ", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error al Guardar ", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error al intentar guardar datos: ${e.message}")
        }
    }


    private fun verDatos() {
        var db = BaseDatos(this)
        var datos = db.listarDatos()

        for (i in 0 until datos.size) {
            Log.i("Datos", "Código: ${datos[i].id}, Email: ${datos[i].email}, Password: ${datos[i].password}")
        }
    }
}