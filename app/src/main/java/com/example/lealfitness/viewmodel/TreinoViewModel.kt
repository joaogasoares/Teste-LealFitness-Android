package com.example.lealfitness.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lealfitness.model.Exercicio
import com.example.lealfitness.model.Treino
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.UUID

class TreinoViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _treinos = MutableStateFlow<List<Treino>>(emptyList())
    val treinos = _treinos.asStateFlow()

    private val _exercicios = MutableStateFlow<List<Exercicio>>(emptyList())
    val exercicios = _exercicios.asStateFlow()

    private var exerciciosListener: ListenerRegistration? = null

    fun getTreinoPorId(id: String): Treino? {
        return _treinos.value.find { it.id == id }
    }

    fun getExercicioPorId(id: String): Exercicio? {
        return _exercicios.value.find { it.id == id }
    }

    fun getTreinos() {
        db.collection("treinos").addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("LealFitness", "Erro ao ouvir treinos", error)
                return@addSnapshotListener
            }
            val lista = value?.documents?.map { doc ->
                doc.toObject(Treino::class.java)!!.copy(id = doc.id)
            } ?: emptyList()
            _treinos.value = lista
        }
    }

    fun salvarTreino(treino: Treino) {
        viewModelScope.launch {
            try {
                if (treino.id.isEmpty()) {
                    db.collection("treinos").add(treino)
                } else {
                    db.collection("treinos").document(treino.id).set(treino)
                }
                // Não precisa chamar getTreinos(), o Listener atualiza sozinho
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro ao salvar treino", e)
            }
        }
    }

    fun deletarTreino(treinoId: String) {
        viewModelScope.launch {
            db.collection("treinos").document(treinoId).delete()
        }
    }

    fun getExercicios(treinoId: String) {
        exerciciosListener?.remove()

        exerciciosListener = db.collection("treinos").document(treinoId)
            .collection("exercicios")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("LealFitness", "Erro ao ouvir exercícios", error)
                    return@addSnapshotListener
                }
                val lista = value?.documents?.map { doc ->
                    doc.toObject(Exercicio::class.java)!!.copy(id = doc.id)
                } ?: emptyList()
                _exercicios.value = lista
            }
    }

    fun salvarExercicio(treinoId: String, exercicio: Exercicio, imagemUri: Uri?, context: Context) {
        viewModelScope.launch {
            var urlFinal = exercicio.imagemUrl

            if (imagemUri != null) {
                try {
                    withTimeout(2000L) {
                        val ref = storage.reference.child("images/${UUID.randomUUID()}")
                        ref.putFile(imagemUri).await()
                        urlFinal = ref.downloadUrl.await().toString()
                    }
                    // -------------------------------------------
                } catch (e: Exception) {
                    Log.e("LealFitness", "Upload falhou ou demorou", e)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Sem internet. Salvando com foto padrão.", Toast.LENGTH_LONG).show()
                    }

                    urlFinal = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400&fit=crop"
                }
            }

            try {
                val exercicioAtualizado = exercicio.copy(imagemUrl = urlFinal)
                val collectionRef = db.collection("treinos").document(treinoId).collection("exercicios")

                if (exercicio.id.isEmpty()) {
                    collectionRef.add(exercicioAtualizado)
                } else {
                    collectionRef.document(exercicio.id).set(exercicioAtualizado)
                }
            } catch (e: Exception) {
                Log.e("LealFitness", "Erro no banco", e)
            }
        }
    }

    fun deletarExercicio(treinoId: String, exercicioId: String) {
        viewModelScope.launch {
            db.collection("treinos").document(treinoId)
                .collection("exercicios").document(exercicioId).delete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        exerciciosListener?.remove()
    }
}