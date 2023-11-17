package com.example.springtech.ui.SPT

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.springtech.R
import com.example.springtech.io.ApiService
import com.example.springtech.io.response.ResponseBody
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity: AppCompatActivity(), OnMapReadyCallback {

    //lateinit sirve para que la variable se inicialice después
    private lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //------------------------------------------------------------------------------------------
    //lista de coordenadas
    private val coordinatesList = mutableListOf<Pair<Double, Double>>()

    //conexion con api
    private val urlbase = "http://192.168.84.1:8000/api/v1/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlbase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)
    //------------------------------------------------------------------------------------------

    companion object{
        const val req_code_loc = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createFragment()
    }

    //crea un fragmento para asociarlo con el mapa
    private fun createFragment(){
        val mapFragment : SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //almacena la instancia del mapa y es llamado cuando el mapa está listo
    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        enableLocation()
        MarcarCoordenadas()
    }

    //Habilita la funcionalidad de ubicación en el mapa de google
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermission()) {
            //si
            map.isMyLocationEnabled = true
            getLocation()
        } else {
            //no
            requestLocation()
        }
    }

    //obtener la ubicación
    private fun getLocation() {
        if (!isLocationPermission()) {
            // Verificar los permisos antes de intentar obtener la ubicación
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitud: Double = it.latitude
                    val longitud: Double = it.longitude

                    val coordinates = LatLng(latitud, longitud)
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(coordinates, 19f),
                        1000,
                        null
                    )
                }
            }
    }

    //------------------------------------------------------------------------------------------
    //obtenemos las coordenadas y las marcamos
    private val call = service.obtenerDatos(
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlSWQiOjIsInN1YiI6IjEiLCJpYXQiOjE3MDAxOTk4ODQsImV4cCI6MTcwMDI4NjI4NH0.4V9gAyb02F2cI-_iJySxlvmF_Ez2tPhr0Blvi9DGYqI",
        2,
        2,
        "12.3456",
        "-78.9012"
    )
    private fun MarcarCoordenadas() {

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null && responseBody.body != null) {
                            val technicians = responseBody.body

                            coordinatesList.clear()

                            coordinatesList.addAll(technicians?.map { technical ->
                                Pair(technical.latitude.toDouble(), technical.longitude.toDouble())
                            } ?: emptyList())
                            Log.e("HomeActivity", "Se guardó correctamente")
                            marcador(coordinatesList)

                        } else {
                            Log.e("HomeActivity", "Respuesta nula o cuerpo nulo en la respuesta.")
                        }
                    } else {
                        Log.e("HomeActivity", "Respuesta no exitosa. Código: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("HomeActivity", "Error inesperado: ${e.message}", e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MainActivity", "Error en la llamada: ${t.message}", t)
            }
        })
    }

    private fun marcador(listaDeCoordenadas: List<Pair<Double, Double>>) {
        val icon = BitmapFactory.decodeResource(resources, R.drawable.man)

        for ((latitud, longitud) in listaDeCoordenadas) {
            val coordinates = LatLng(latitud, longitud)

            //cambio del tamaño del ícono
            val scaledBitmap = Bitmap.createScaledBitmap(icon, 150, 150, false)
            val scaledIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

            val marker = MarkerOptions()
                .position(coordinates)
                .title("Worker")
                .icon(scaledIcon)

            map.addMarker(marker)
        }
    }
    //------------------------------------------------------------------------------------------

    //<permisos de ubicación>

    //Verifica si la aplicación tiene el permiso de ubicación
    private fun isLocationPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    //Solicita al usuario el permiso de ubicación
    private fun requestLocation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                req_code_loc
            )
        }
    }

    //Maneja la respuesta del usuario a la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            req_code_loc -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
                getLocation()
            } else{
                Toast.makeText(this, "Ve a ajustes y acepta los permisos de localización", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }


    //Se llama cuando las operaciones del fragmento se reanudan
    override fun onResumeFragments() {
        if(!::map.isInitialized) return
        super.onResumeFragments()
        if(!isLocationPermission()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Ve a ajustes y acepta los permisos de localización", Toast.LENGTH_SHORT).show()
        } else {
            getLocation()
        }
    }
    //</permisos de ubicación>

}