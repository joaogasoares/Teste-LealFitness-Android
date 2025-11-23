package com.example.lealfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.example.lealfitness.ui.*
import com.example.lealfitness.viewmodel.TreinoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val viewModel: TreinoViewModel by viewModels()

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController, viewModel)
                }

                composable("salvar_treino/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: "novo"
                    SalvarTreinoScreen(navController, viewModel, id)
                }

                composable("detalhes_treino/{treinoId}") { backStackEntry ->
                    val treinoId = backStackEntry.arguments?.getString("treinoId") ?: ""
                    DetalheTreinoScreen(navController, viewModel, treinoId)
                }

                composable(
                    route = "salvar_exercicio/{treinoId}?exercicioId={exercicioId}",
                    arguments = listOf(
                        navArgument("treinoId") { type = NavType.StringType },
                        navArgument("exercicioId") { type = NavType.StringType; defaultValue = "novo" }
                    )
                ) { backStackEntry ->
                    val treinoId = backStackEntry.arguments?.getString("treinoId") ?: ""
                    val exercicioId = backStackEntry.arguments?.getString("exercicioId") ?: "novo"
                    SalvarExercicioScreen(navController, viewModel, treinoId, exercicioId)
                }
            }
        }
    }
}