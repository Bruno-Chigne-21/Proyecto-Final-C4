package com.example.springtech.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.springtech.R
import com.example.springtech.bd.BaseDatos
import com.example.springtech.io.ApiService
import com.example.springtech.io.response.LoginRequest
import com.example.springtech.io.response.LoginResponse
import com.example.springtech.ui.Introduccion.IntroActivity1
import com.example.springtech.ui.SPT.HomeActivity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

class MainActivity : AppCompatActivity() {

    private var urlbase = "http://192.168.84.1:8000/api/v1/auth/"
    private val handler = Handler()
    private lateinit var progressBar: ProgressBar
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        progressBar = findViewById(R.id.progressBar)

        showLoadingScreen()

        handler.postDelayed({
            hideLoadingScreen()
        }, 6000)
    }

    private fun showLoadingScreen() {
        progressBar.visibility = View.VISIBLE
    }

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlbase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)

    private fun hideLoadingScreen() {
        progressBar.visibility = View.GONE
        val baseDatos = BaseDatos(this)
        val intentHome = Intent(this, HomeActivity::class.java)
        val intentIntro = Intent(this, IntroActivity1::class.java)

        try {
            if (baseDatos.contenido()) {
                val datos = baseDatos.listarDatos()
                for (i in 0 until datos.size) {
                    token = datos[i].token
                    if (isTokenValid(token)) {
                        startActivity(intentHome)
                        finish()
                        return
                    } else {
                        Log.d("MainActivity", "Token no válido. Llamando al servicio de login.")
                        val user = LoginRequest(datos[i].email, datos[i].password)
                        lifecycleScope.launch {
                            try {
                                val result: LoginResponse = service.login(user)
                                val token = result.body.token

                                if (result.message == "Autenticado correctamente") {
                                    if (baseDatos.actualizarToken(1, token)) {
                                        Log.i("MainActivity", "Token Actualizado correctamente")
                                        startActivity(intentHome)
                                        finish()
                                    } else {
                                        Log.e(
                                            "MainActivity",
                                            "Error al actualizar el token en la base de datos"
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e(
                                    "MainActivity",
                                    "Error en la llamada al servicio: ${e.message}"
                                )
                            }
                        }
                    }
                }
            } else {
                Log.d("MainActivity", "La base de datos no tiene contenido. Redirigiendo a IntroActivity1.")
                startActivity(intentIntro)
                finish()
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MainActivity", "Error general: ${e.message}")
        }
    }

    private fun isTokenValid(token: String): Boolean {
        try {
            val secretKey = "argi4mM5i0TYXps7nKB/MTuDxuYzW5C/eYQoUDoHOfmXOZ76miVDPTD1rbb5lptMvh8fD5TPspz0fycodcT4KIjkYHwzj1ZvrjjJ17NZBQOhR1/iA75JeCXD3QCvx86pzB6eqWQnWyNufC3XOEn/Yb6KoFWX/QA35VkOqQUy52+75Z+UvDspvUIffKjd/qed4LKr0kKsEKEiJOYphOn5mzxCe9And+t36c9Ody4Vxh7ppoMyspl0r1aCQhU5ncyqNQ7bKoowwnNW/k1NobDDF6DzrWg35Nm2PQccrEb6PnFFKzJK17UJ9F7uOztCtfGapzBW5yJFCPZJO/FWd4RHSL82WboJzvHZSWkNgOHXPw86BGVLisdyGYvVrPnpQPJX4ziCBppIJHoq/puYRQ2Qtg=="

            val claims: Claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
            return !claims.expiration.before(Date())

        } catch (e: Exception) {
            // El token no es válido
            return false
        }
    }
    


}