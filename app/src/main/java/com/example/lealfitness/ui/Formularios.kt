package com.example.lealfitness.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lealfitness.model.Exercicio
import com.example.lealfitness.model.Treino
import com.example.lealfitness.viewmodel.TreinoViewModel

@Composable
fun SalvarTreinoScreen(
    navController: NavController,
    viewModel: TreinoViewModel,
    treinoId: String
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    val diasDisponiveis = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab")
    val diasSelecionados = remember { mutableStateListOf<String>() }

    val titulo = if (treinoId == "novo") "Nova Ficha" else "Editar Ficha"

    LaunchedEffect(treinoId) {
        if (treinoId != "novo") {
            val treinoExistente = viewModel.getTreinoPorId(treinoId)
            treinoExistente?.let {
                nome = it.nome
                descricao = it.descricao
                diasSelecionados.clear()
                diasSelecionados.addAll(it.diasSemana)
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome (ex: Treino A)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição (ex: Peito e Tríceps)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Dias da Semana:", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            diasDisponiveis.forEach { dia ->
                val selecionado = diasSelecionados.contains(dia)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (selecionado) MaterialTheme.colorScheme.primary else Color.LightGray)
                        .clickable {
                            if (selecionado) diasSelecionados.remove(dia) else diasSelecionados.add(dia)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dia.take(1),
                        color = if (selecionado) Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    val treinoParaSalvar = Treino(
                        id = if (treinoId == "novo") "" else treinoId,
                        nome = nome,
                        descricao = descricao,
                        diasSemana = diasSelecionados.toList()
                    )
                    viewModel.salvarTreino(treinoParaSalvar)
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Salvar")
            }
        }
    }
}

@Composable
fun SalvarExercicioScreen(
    navController: NavController,
    viewModel: TreinoViewModel,
    treinoId: String,
    exercicioId: String = "novo"
) {
    var nome by remember { mutableStateOf("") }
    var obs by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf("") }

    val titulo = if (exercicioId == "novo" || exercicioId.isEmpty()) "Novo Exercício" else "Editar Exercício"

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    LaunchedEffect(exercicioId) {
        if (exercicioId != "novo" && exercicioId.isNotEmpty()) {
            val exercicioExistente = viewModel.getExercicioPorId(exercicioId)
            exercicioExistente?.let {
                nome = it.nome
                obs = it.observacoes
                currentImageUrl = it.imagemUrl
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Exercício") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = obs,
            onValueChange = { obs = it },
            label = { Text("Observações (Séries/Repetições)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (imageUri != null) {
            Text("Nova imagem selecionada", color = Color.Green)
        } else if (currentImageUrl.isNotEmpty()) {
            AsyncImage(
                model = currentImageUrl,
                contentDescription = null,
                modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.medium)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Selecionar Imagem / Alterar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    val idFinal = if (exercicioId == "novo") "" else exercicioId

                    val exercicio = Exercicio(id = idFinal, nome = nome, observacoes = obs, imagemUrl = currentImageUrl)

                    viewModel.salvarExercicio(treinoId, exercicio, imageUri)
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Salvar")
            }
        }
    }
}