package com.example.respondeai.Model

import java.io.Serializable

data class Perguntas(
    val pergunta: String,
    val item1:String,
    val item2:String,
    val item3:String,
    val respCerta:String
):Serializable {
    // Construtor sem argumentos, necessário para o Firestore
    constructor() : this("", "", "", "", ""
    )
}