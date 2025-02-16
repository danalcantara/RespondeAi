package com.example.respondeai.Model

import java.io.Serializable


data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
val pontuacao:String

):Serializable {
    constructor() : this("", "","", "0")
}