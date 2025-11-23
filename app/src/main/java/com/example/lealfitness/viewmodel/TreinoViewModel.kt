package com.example.lealfitness.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lealfitness.model.Exercicio
import com.example.lealfitness.model.Treino
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TreinoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _treinos = MutableStateFlow<List<Treino>>(emptyList())
    val treinos = _treinos.asStateFlow()

    private val _exercicios = MutableStateFlow<List<Exercicio>>(emptyList())
    val exercicios = _exercicios.asStateFlow()

    fun getTreinoPorId(id: String): Treino? {
        return _treinos.value.find { it.id == id }
    }

    fun getExercicioPorId(id: String): Exercicio? {
        return _exercicios.value.find { it.id == id }
    }

    fun getTreinos() {
        viewModelScope.launch {
            try {
                val result = db.collection("treinos").get().await()
                val lista = result.documents.map { doc ->
                    doc.toObject(Treino::class.java)!!.copy(id = doc.id)
                }
                _treinos.value = lista
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro ao buscar treinos", e)
            }
        }
    }

    fun salvarTreino(treino: Treino) {
        viewModelScope.launch {
            try {
                if (treino.id.isEmpty()) {
                    db.collection("treinos").add(treino).await()
                } else {
                    db.collection("treinos").document(treino.id).set(treino).await()
                }
                getTreinos()
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro ao salvar treino", e)
            }
        }
    }

    fun deletarTreino(treinoId: String) {
        viewModelScope.launch {
            try {
                db.collection("treinos").document(treinoId).delete().await()
                getTreinos()
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro ao deletar", e)
            }
        }
    }

    fun getExercicios(treinoId: String) {
        viewModelScope.launch {
            try {
                val result = db.collection("treinos").document(treinoId)
                    .collection("exercicios").get().await()
                val lista = result.documents.map { doc ->
                    doc.toObject(Exercicio::class.java)!!.copy(id = doc.id)
                }
                _exercicios.value = lista
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro ao buscar exercícios", e)
            }
        }
    }

    // --- AQUI ESTÁ A MÁGICA ---
    fun salvarExercicio(treinoId: String, exercicio: Exercicio, imagemUri: Uri?) {
        viewModelScope.launch {
            var urlFinal = exercicio.imagemUrl

            if (imagemUri != null) {
                try {
                    val ref = storage.reference.child("images/${UUID.randomUUID()}")
                    ref.putFile(imagemUri).await()
                    urlFinal = ref.downloadUrl.await().toString()
                } catch (e: Exception) {
                    Log.e("LealFitness", "Upload falhou, usando imagem de teste", e)
                    urlFinal = "https://img.freepik.com/free-vector/illustration-gallery-icon_53876-27002.jpg"
                }
            }

            try {
                val exercicioAtualizado = exercicio.copy(imagemUrl = urlFinal)
                val collectionRef = db.collection("treinos").document(treinoId).collection("exercicios")

                if (exercicio.id.isEmpty()) {
                    collectionRef.add(exercicioAtualizado).await()
                } else {
                    collectionRef.document(exercicio.id).set(exercicioAtualizado).await()
                }
                getExercicios(treinoId) // Atualiza a tela
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro ao salvar no banco", e)
            }
        }
    }

    fun deletarExercicio(treinoId: String, exercicioId: String) {
        viewModelScope.launch {
            try {
                db.collection("treinos").document(treinoId)
                    .collection("exercicios").document(exercicioId).delete().await()
                getExercicios(treinoId)
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro ao deletar exercício", e)
            }
        }
    }
}