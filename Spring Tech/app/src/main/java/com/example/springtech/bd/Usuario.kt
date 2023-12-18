package com.example.springtech.bd

class Usuario {

    var idUser: Int = 0
    var idClient: Int = 0
    var email: String = ""
    var password: String = ""
    var token: String = ""

    constructor(idUser: Int, idClient: Int, email:String, password:String, token:String){
        this.idUser = idUser
        this.idClient = idClient
        this.email = email
        this.password = password
        this.token
    }

    constructor(){ }

}