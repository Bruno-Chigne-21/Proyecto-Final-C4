package com.example.springtech.bd

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val BD="BaseDatos"
class BaseDatos(contexto: Context):SQLiteOpenHelper(contexto, BD,null,1) {

    //Esta funcion se ejecutara cada vez que se llame a esta clase para crear la tabla en caso no exista
    override fun onCreate(p0: SQLiteDatabase?) {
        var sql:String =
            "CREATE TABLE IF NOT EXISTS Usuario(id Integer PRIMARY KEY AUTOINCREMENT, email VARCHAR(250), password VARCHAR(250))"
        // p0 -> objeto de la clase SQLLiteDatabase,este tiene todos los metodos sql

        p0?.execSQL(sql)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    //Al poner al final :String le decimos que esta funcion retornara un valor de tipo String
    fun insertarDatos(usuario: Usuario):String{
        val p0 = this.writableDatabase

        // este contenedor se encargar de tener los campos y sus valores respectivos que deseamos ingresar
        val contenedorValores = ContentValues()

        contenedorValores.put("email",usuario.email)
        contenedorValores.put("password",usuario.password)

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

    fun listarDatos(): MutableList<Usuario> {
        val lista: MutableList<Usuario> = ArrayList()
        val db = this.readableDatabase

        val sql = "SELECT * FROM Usuario"
        val resultado = db?.rawQuery(sql, null)

        if (resultado != null) {
            resultado.moveToFirst()
            do {
                val usu = Usuario()
                usu.id = resultado.getString(resultado.getColumnIndexOrThrow("id")).toInt()
                usu.email = resultado.getString(resultado.getColumnIndexOrThrow("email"))
                usu.password = resultado.getString(resultado.getColumnIndexOrThrow("password"))

                lista.add(usu)

            } while (resultado.moveToNext())

            resultado.close()
            db.close()
        }

        return lista
    }

    fun contenido(): Boolean {
        val db = this.readableDatabase

        val sql = "SELECT * FROM Usuario"
        val resultado = db.rawQuery(sql, null)

        val hayDatos = resultado?.count ?: 0 > 0

        resultado?.close()
        db.close()

        return hayDatos
    }

}