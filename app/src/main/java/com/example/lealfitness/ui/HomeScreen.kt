package com.example.lealfitness.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lealfitness.model.Treino
import com.example.lealfitness.viewmodel.TreinoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: TreinoViewModel) {
    val treinos by viewModel.treinos.collectAsState()

    LaunchedEffect(Unit) { viewModel.getTreinos() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minhas Fichas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("salvar_treino/novo") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Ficha")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (treinos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum treino cadastrado.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(treinos) { treino ->
                        TreinoItem(
                            treino = treino,
                            onClick = { navController.navigate("detalhes_treino/${treino.id}") },
                            onDelete = { viewModel.deletarTreino(treino.id) },
                            onEdit = { navController.navigate("salvar_treino/${treino.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TreinoItem(
    treino: Treino,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = treino.nome,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remover",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (treino.descricao.isNotEmpty()) {
                Text(
                    text = treino.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (treino.diasSemana.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    treino.diasSemana.forEach { dia ->
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = dia,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}