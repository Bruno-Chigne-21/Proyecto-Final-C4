package com.example.springtech.bd

class Usuario {

    var id: Int = 0
    var email: String = ""
    var password: String = ""
    var token: String = ""

    constructor(email:String, password:String, token:String){
        this.email = email
        this.password = password
        this.token
    }

    constructor(){ }

}