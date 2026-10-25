package com.example.p2prog3.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.p2prog3.data.repository.FavoriteRepository
import com.example.p2prog3.ui.screens.CatalogScreen
import com.example.p2prog3.ui.screens.FavoritesScreen
import com.example.p2prog3.ui.screens.PostsScreen
import com.example.p2prog3.ui.screens.ProductDetailScreen
import com.example.p2prog3.ui.viewmodel.CatalogViewModel
import com.example.p2prog3.ui.viewmodel.FavoritesViewModel
import com.example.p2prog3.ui.viewmodel.PostsViewModel
import com.example.p2prog3.ui.viewmodel.ProductDetailViewModel

sealed class Screen(val route: String, val title: String) {
    object Catalog : Screen("catalog", "Catálogo")
    object Favorites : Screen("favorites", "Favoritos")
    object Posts : Screen("posts", "Publicaciones")
    object ProductDetail : Screen("detail/{productId}", "Detalle") {
        fun createRoute(productId: Int) = "detail/$productId"
    }
}

@Composable
fun MainAppContainer() {
    val navController = rememberNavController()
    val favoriteRepository = remember { FavoriteRepository() }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Ocultar barra inferior en la pantalla de detalle o publicaciones si preferimos
    val showBottomBar = currentRoute in listOf(Screen.Catalog.route, Screen.Favorites.route, Screen.Posts.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Catalog.route,
                        onClick = {
                            navController.navigate(Screen.Catalog.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Catálogo") },
                        label = { Text("Catálogo") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Favorites.route,
                        onClick = {
                            navController.navigate(Screen.Favorites.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                        label = { Text("Favoritos") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Posts.route,
                        onClick = {
                            navController.navigate(Screen.Posts.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.Article, contentDescription = "Publicaciones") },
                        label = { Text("Publicaciones") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Catalog.route) {
                val catalogViewModel: CatalogViewModel = viewModel()
                CatalogScreen(
                    viewModel = catalogViewModel,
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToPosts = {
                        navController.navigate(Screen.Posts.route)
                    }
                )
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
                val detailViewModel: ProductDetailViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return ProductDetailViewModel(favoriteRepository = favoriteRepository) as T
                        }
                    }
                )

                ProductDetailScreen(
                    productId = productId,
                    viewModel = detailViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Posts.route) {
                val postsViewModel: PostsViewModel = viewModel()
                PostsScreen(
                    viewModel = postsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Favorites.route) {
                val favoritesViewModel: FavoritesViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return FavoritesViewModel(favoriteRepository = favoriteRepository) as T
                        }
                    }
                )

                FavoritesScreen(viewModel = favoritesViewModel)
            }
        }
    }
}
