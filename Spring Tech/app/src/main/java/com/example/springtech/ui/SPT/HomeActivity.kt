package com.example.springtech.ui.SPT

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.springtech.R
import com.example.springtech.bd.BaseDatos
import com.example.springtech.io.ApiService
import com.example.springtech.io.response.CoordinatesResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class HomeActivity: AppCompatActivity(), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle                      
    var latitud: Double = 0.0
    var longitud: Double = 0.0

    //lateinit sirve para que la variable se inicialice después
    private lateinit var map:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //profAvai
    var profAvai = 0

    //lista tecnicos
    

    //lista de coordenadas
    private val coordinatesList = mutableListOf<Triple<Double?, Double?, Int>>()

    //conexion con api
    private val urlbase = "http://192.168.84.1:8000/api/v1/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(urlbase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)

    companion object {
        const val req_code_loc = 0
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_drawer)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createFragment()

        drawer = findViewById(R.id.drawer_layout)

        val btnOpenDrawer: View = findViewById(R.id.btnOpenDrawer)
        btnOpenDrawer.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

        toogle = ActionBarDrawerToggle(
            this,
            drawer,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toogle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //buscador
        val buscar = findViewById<Button>(R.id.buscar)
        val solicitar = findViewById<Button>(R.id.solicitar)
        solicitar.isEnabled = false
        buscar.setOnClickListener{
            filtrar()
        }

        solicitar.setOnClickListener{
            showForm()
        }
    }

    //buscador
    private fun filtrar() {
        map.clear()
        var profession = 0
        var availability = 0
        val av = findViewById<Spinner>(R.id.availability)
        val avail = av.selectedItem.toString()
        val pf = findViewById<Spinner>(R.id.profession)
        val profe = pf.selectedItem.toString()

        when (profe) {
            "Mecánico" -> profession = 1
            "Plomero" -> profession = 2
            "Electricista" -> profession = 3
        }

        when (avail){
            "A domicilio" -> availability = 1
            "Taller" -> availability = 2
            "Ambos" -> availability = 0
        }

        if (ActivityCompat.checkSelfPermission(

                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            if (!isLocationPermission()) {
                return
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitud = it.latitude
                        val longitud = it.longitude

                        // Utiliza latitud y longitud según tus necesidades
                        Log.d("Ubicacion", "Latitud: $latitud, Longitud: $longitud")

                        val selectedProfession =
                            if (availability.toString() != "ambos") profession else pf.toString().toInt()

                        MarcarCoordenadas(latitud, longitud, selectedProfession, availability)

                        Log.d("Parametros", "Latitud: $latitud, Longitud: $longitud, SelectedProfession: ${selectedProfession}, availability: ${availability}")
                    }
                }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> Toast.makeText(this, "Estás en el home ", Toast.LENGTH_LONG).show()
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_list -> startActivity(Intent(this, ListaActActivity::class.java))
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toogle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toogle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //crea un fragmento para asociarlo con el mapa
    private fun createFragment() {
        val mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
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
                    latitud = it.latitude
                    longitud = it.longitude

                    val coordinates = LatLng(latitud, longitud)
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(coordinates, 19f),
                        1000,
                        null
                    )
                }
            }
    }

    //obtenemos las coordenadas y las marcamos
    private fun MarcarCoordenadas(latitud: Double, longitud: Double, pf: Int, av: Int = 0) {
        var db = BaseDatos(this)
        var datos = db.listarDatos()
        var token: String? = null
        val solicitar = findViewById<Button>(R.id.solicitar)

        for (i in 0 until datos.size) {
            token = datos[i].token
        }

        val call: Call<CoordinatesResponse>? = when (av) {
            0 -> service.obtenerCoords("Bearer $token", pf, latitud, longitud)
            1, 2 -> service.obtenerCoordsDomTal("Bearer $token", pf, av, latitud, longitud)
            else -> null
        }

        call?.enqueue(object : Callback<CoordinatesResponse> {
            override fun onResponse(call: Call<CoordinatesResponse>, response: Response<CoordinatesResponse>) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            val technicians = responseBody.body
                            coordinatesList.clear()

                            technicians?.map { person ->
                                val lati = person.professionAvailability.latitude
                                val long = person.professionAvailability.longitude
                                val lat = person.latitude
                                val lon = person.longitude
                                profAvai = person.professionAvailability.id

                                val coordinates = when (av) {
                                    0 -> {
                                        if (lati === null && long === null && lat != 0.0 && lon != 0.0) {
                                            Triple(lat, lon, 1)
                                        } else if (lati != null && long != null && lati != 0.0 && long != 0.0) {
                                            Triple(lati, long, 2)
                                        } else {
                                            Triple(0.0, 0.0, 0)
                                        }
                                    }
                                    1 -> Triple(lat, lon, 1)
                                    2 -> Triple(lati, long, 2)
                                    else -> Triple(0.0, 0.0, 0)
                                }
                                coordinatesList.add(coordinates)
                            }

                            Log.e("HomeActivity", "Se guardó correctamente")
                            Log.e("HomeActivity", "${coordinatesList}")
                            marcador(coordinatesList)
                            solicitar.isEnabled = true
                        } else {
                            Log.e("HomeActivity", "Respuesta nula en la respuesta.")
                        }
                    } else {
                        Log.e("HomeActivity", "Respuesta no exitosa. Código: ${response.code()}")
                    }
                } catch (e: Exception) {
                    handleException(e)
                }
            }

            override fun onFailure(call: Call<CoordinatesResponse>, t: Throwable) {
                handleException(t)
            }
        })
    }

    //formulario de solicitar
    private fun showForm() {
        var db = BaseDatos(this)
        var datos = db.listarDatos()
        var clientID = 0
        var token = ""
        var selectedCategoryId = 0

        for (i in 0 until datos.size) {
            clientID = datos[i].idClient
            token = datos[i].token
        }

        val formView = LayoutInflater.from(this).inflate(R.layout.form_layout, null)
        val editTextTitle: EditText = formView.findViewById(R.id.editTextTitle)
        val editTextDescription: EditText = formView.findViewById(R.id.editTextDescription)

        // Llamada al endpoint para obtener categorías
        lifecycleScope.launch {
            try {
                val response = service.category("Bearer $token")
                Log.d("HomeActivity", "Token: $token")

                runOnUiThread {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            val categoriesList = responseBody.body

                            // Configurar el Spinner con la lista de categorías
                            val spinner: Spinner = formView.findViewById(R.id.spinner)
                            val adapter = ArrayAdapter(
                                this@HomeActivity,
                                android.R.layout.simple_spinner_item,
                                categoriesList.map { it.name }
                            )

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner.adapter = adapter

                            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    selectedCategoryId = categoriesList[position].id
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }
                        } else {
                            Log.e("HomeActivity", "Respuesta nula en la respuesta de categorías.")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "Error al obtener categorías. Código: ${response.code()}, Mensaje: $errorBody"
                        Log.e("HomeActivity", errorMessage)
                    }
                }

            } catch (e: IOException) {
                Log.e("HomeActivity", "Excepción de red: ${e.message}", e)

            } catch (e: Exception) {
                Log.e("HomeActivity", "Error inesperado: ${e.message}", e)
            }
        }


        val alertDialog = AlertDialog.Builder(this, R.style.RoundedDialog)
            .setTitle("Formulario")
            .setView(formView)
            .setPositiveButton("Solicitar") { _, _ ->
                val title = editTextTitle.text.toString()
                val description = editTextDescription.text.toString()

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
                    )
                } else {
                    if (!isLocationPermission()) {
                        return@setPositiveButton
                    }

                    // Dentro del bloque addOnSuccessListener
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                val latitud = it.latitude
                                val longitud = it.longitude

                                Log.d("Ubicacion", "Latitud: $latitud, Longitud: $longitud")

                                lifecycleScope.launch {
                                    try {
                                        // Crear RequestBody para cada parámetro
                                        val professionAvailabilityIdPart = createPartFromString(profAvai.toString())
                                        val clientIdPart = createPartFromString(clientID.toString())
                                        val categoryServiceIdPart = createPartFromString(selectedCategoryId.toString())
                                        val latitudePart = createPartFromString(latitud.toString())
                                        val longitudePart = createPartFromString(longitud.toString())
                                        val titlePart = createPartFromString(title)
                                        val descriptionPart = createPartFromString(description)

                                        // Llamar a la función
                                        val response = service.solicitar(
                                            professionAvailabilityIdPart,
                                            clientIdPart,
                                            categoryServiceIdPart,
                                            latitudePart,
                                            longitudePart,
                                            titlePart,
                                            descriptionPart,
                                            "Bearer $token"
                                        )

                                        if (response.isSuccessful) {
                                            Log.d("HomeActivity", "Solicitud exitosa")
                                        } else {
                                            Log.e("HomeActivity", "Error en la solicitud. Código: ${response.code()}")
                                        }
                                    } catch (e: Exception) {
                                        Log.e("HomeActivity", "Error inesperado al realizar la solicitud: ${e.message}", e)
                                    }
                                }
                            } ?: run {
                                Log.e("Ubicacion", "Ubicación nula")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Ubicacion", "Error al obtener la ubicación: ${e.message}", e)
                        }

                        .addOnFailureListener { e ->
                            Log.e("Ubicacion", "Error al obtener la ubicación: ${e.message}", e)
                        }

                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
            }
            .create()

        alertDialog.show()
    }

    private fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun handleException(exception: Throwable) {
        Log.e("HomeActivity", "Error inesperado: ${exception.message}", exception)
    }

    private fun marcador(listaDeCoordenadas: List<Triple<Double?, Double?, Int>>) {

        val iconMan = BitmapFactory.decodeResource(resources, R.drawable.man)
        val iconGarage = BitmapFactory.decodeResource(resources, R.drawable.garage)

        for ((latitud, longitud, tipo) in listaDeCoordenadas) {
            if (latitud != null && longitud != null) {
                val coordinates = LatLng(latitud, longitud)

                // Cambio del tamaño del ícono
                val icon = when (tipo) {
                    1 -> BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(iconMan, 150, 150, false))
                    2 -> BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(iconGarage, 150, 150, false))
                    else -> BitmapDescriptorFactory.defaultMarker()
                }

                val marker = MarkerOptions()
                    .position(coordinates)
                    .title("Worker")
                    .icon(icon)

                map.addMarker(marker)
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