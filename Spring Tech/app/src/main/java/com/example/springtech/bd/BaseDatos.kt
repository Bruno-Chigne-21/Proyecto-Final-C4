package com.example.springtech.bd

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope

val BD="BaseDatos"
class BaseDatos(contexto: Context):SQLiteOpenHelper(contexto, BD,null,1) {

    //Esta funcion se ejecutara cada vez que se llame a esta clase para crear la tabla en caso no exista
    override fun onCreate(p0: SQLiteDatabase?) {
        var sql: String =
            "CREATE TABLE IF NOT EXISTS Usuario(idUser INTEGER PRIMARY KEY, idClient INTEGER, email VARCHAR(250), password VARCHAR(250), token VARCHAR(250))"
        // p0 -> objeto de la clase SQLLiteDatabase,este tiene todos los metodos sql

        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    //Al poner al final :String le decimos que esta funcion retornara un valor de tipo String
    fun insertarDatos(usuario: Usuario):String{
        val p0 = this.writableDatabase

        // este contenedor se encargar de tener los campos y sus valores respectivos que deseamos ingresar
        val contenedorValores = ContentValues()

        contenedorValores.put("idUser",usuario.idUser)
        contenedorValores.put("idClient",usuario.idClient)
        contenedorValores.put("email",usuario.email)
        contenedorValores.put("password",usuario.password)
        contenedorValores.put("token", usuario.token)

        //llevamos acabo la operacion sql jacion uso del p0 que tiene todo los metodos para hacer las insercesiones
        var resultado = p0.insert("Usuario",null,contenedorValores)

        //En caso se haya insertado de manera correcta enviara un numero, pero de no ser asi enviara un -1

        if (resultado == -1.toLong()){
            //inserción fallida
            return "1"
        }else{
            //inserción exitosa
            return "0"
        }
    }

    //mostrar datos
    fun listarDatos(): MutableList<Usuario> {
        val lista: MutableList<Usuario> = ArrayList()
        val db = this.readableDatabase

        val sql = "SELECT * FROM Usuario"
        val resultado = db?.rawQuery(sql, null)

        resultado?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val usu = Usuario()
                    usu.idUser = cursor.getString(cursor.getColumnIndexOrThrow("idUser")).toInt()
                    usu.idClient = cursor.getString(cursor.getColumnIndexOrThrow("idClient")).toInt()
                    usu.email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                    usu.password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                    usu.token = cursor.getString(cursor.getColumnIndexOrThrow("token"))

                    lista.add(usu)
                } while (cursor.moveToNext())
            }
        }

        resultado?.close()
        db.close()

        return lista
    }

    //verificar si la tabla está o no vacía
    fun contenido(): Boolean {
        val db = this.readableDatabase

        val sql = "SELECT * FROM Usuario"
        val resultado = db.rawQuery(sql, null)

        val hayDatos = resultado?.count ?: 0 > 0

        resultado?.close()
        db.close()

        return hayDatos
    }

    //método que es llamado en caso el token ya no esté vigente
    fun actualizarToken(id: Int, nuevoToken: String): Boolean {
        val db = this.writableDatabase

        val contenedorValores = ContentValues()
        contenedorValores.put("token", nuevoToken)

        val resultado = db.update("Usuario", contenedorValores, "idUser=?", arrayOf(id.toString()))

        db.close()

        // Si el resultado es mayor que 0, significa que se actualizó al menos una fila.
        return resultado > 0
    }


    //elimina la base de datos
    fun dropAll(): Boolean {
        val db = this.writableDatabase

        val resultado = db.delete("Usuario", null, null)

        db.close()

        return resultado > 0
    }

}