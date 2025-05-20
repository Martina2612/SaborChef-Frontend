package com.example.saborchef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.saborchef.model.UserRole
import com.example.saborchef.ui.screens.FavoriteRecipesScreen
import com.example.saborchef.ui.screens.FilterScreen
import com.example.saborchef.ui.screens.RecipeDetailScreen
import com.example.saborchef.ui.screens.SearchScreen
import com.example.saborchef.ui.screens.SearchUiState
import com.example.saborchef.ui.screens.SplashScreen
import com.example.saborchef.ui.screens.WelcomeScreen
import com.example.saborchef.ui.theme.SaborChefTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val navController = rememberNavController()
                    FavoriteRecipesScreen(
                        navController    = navController,
                        query            = "",               // texto inicial de búsqueda
                        onQueryChange    = {},               // callback de cambio de texto
                        onFilterClick    = {}                // callback de filtro
                    )
                    /*SearchScreen(
                        navController     = navController,
                        role              = UserRole.USUARIO,        // o ALUMNO, VISITANTE…
                        uiState           = SearchUiState.Idle,       // o Suggest(...), NoResults, Results(...)
                        query             = "",
                        selectedTags      = emptyList(),
                        onQueryChange     = {},
                        onRemoveTag       = {},
                        onCategorySelect  = { /* log o Toast */ },
                        onSuggestionClick = {},
                        onSortSelected    = {},
                        onFilterClick     = { /* navController.navigate("filter") */ },
                        onRecipeClick     = {}
                    )*/
                    /*NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable ("splash"){FilterScreen(
                            navController = navController,
                            selectedInclude = listOf("Huevo", "Tomate"),
                            selectedExclude = listOf("Maní"),
                            selectedUsers = "usuario42",
                            onIncludeChange = {},
                            onExcludeChange = {},
                            onUserSelect = {},
                            onApply = {}
                        )  }

                        composable("splash") {
                            SplashScreen(
                                navController = navController,
                                // dentro de tu SplashScreen deberías tener algo como:
                                // LaunchedEffect(Unit) { delay(2000); navController.navigate("welcome") }
                            )
                        }
                        composable("welcome") {
                            WelcomeScreen(
                                navController = navController,
                                onContinueAsUser = { /* TODO: navegar a Login o Home */ },
                                onContinueAsGuest = { /* TODO: navegar como invitado */ }
                            )
                        }
                        composable(
                            route = "recipe/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            // aquí recibís el parámetro id y muestras tu detalle
                            val recipeId = backStackEntry.arguments?.getString("id") ?: ""
                            RecipeDetailScreen(
                                recipeId = recipeId,
                                onBack = { navController.popBackStack() }
                            )
                        }*/
                    }
                }
            }

    }
}
