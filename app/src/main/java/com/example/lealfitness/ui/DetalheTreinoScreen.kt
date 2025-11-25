package com.example.lealfitness.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lealfitness.viewmodel.TreinoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheTreinoScreen(navController: NavController, viewModel: TreinoViewModel, treinoId: String) {
    val exercicios by viewModel.exercicios.collectAsState()

    LaunchedEffect(treinoId) { viewModel.getExercicios(treinoId) }

    Scaffold(

        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Exercícios do Treino") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("salvar_exercicio/$treinoId") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Exercício")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(exercicios) { exercicio ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            if (exercicio.imagemUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = exercicio.imagemUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp).clip(MaterialTheme.shapes.small)
                                )
                            }
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(exercicio.nome, style = MaterialTheme.typography.titleMedium)
                                if(exercicio.observacoes.isNotEmpty()) {
                                    Text(exercicio.observacoes, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Row {
                            IconButton(onClick = {
                                navController.navigate("salvar_exercicio/$treinoId?exercicioId=${exercicio.id}")
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deletarExercicio(treinoId, exercicio.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}