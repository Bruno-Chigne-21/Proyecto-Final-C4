package com.example.springtech.ui.SPT

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.springtech.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class HomeActivity: AppCompatActivity(), OnMapReadyCallback {

    //lateinit sirve para que la variable se inicialice después
    private lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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