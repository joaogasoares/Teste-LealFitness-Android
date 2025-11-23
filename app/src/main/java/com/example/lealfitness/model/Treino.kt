package com.example.lealfitness.model

import com.google.firebase.Timestamp

data class Treino(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val diasSemana: List<String> = emptyList(),
    val data: Timestamp = Timestamp.now()
)