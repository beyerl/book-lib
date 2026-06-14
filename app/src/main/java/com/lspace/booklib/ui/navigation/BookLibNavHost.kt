package com.lspace.booklib.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lspace.booklib.di.AppViewModelProvider
import com.lspace.booklib.ui.achievements.AchievementsScreen
import com.lspace.booklib.ui.achievements.AchievementsViewModel
import com.lspace.booklib.ui.achievements.YearBooksScreen
import com.lspace.booklib.ui.book.BookScreen
import com.lspace.booklib.ui.home.HomeScreen
import com.lspace.booklib.ui.importexport.ImportExportScreen
import com.lspace.booklib.ui.library.LibraryScreen
import com.lspace.booklib.ui.search.NewBookScreen
import com.lspace.booklib.ui.search.SearchDetailScreen
import com.lspace.booklib.ui.search.SearchScreen
import com.lspace.booklib.ui.search.SearchViewModel

@Composable
fun BookLibNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            val showBar = TopLevelDestination.entries.any { dest ->
                currentDestination?.hierarchy?.any { it.route == dest.route } == true
            }
            if (showBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { dest ->
                        val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenBook = { navController.navigate(Routes.book(it)) },
                    onOpenImportExport = { navController.navigate(Routes.IMPORT_EXPORT) },
                )
            }

            composable(Routes.LIBRARY) {
                LibraryScreen(onOpenBook = { navController.navigate(Routes.book(it)) })
            }

            // Search graph shares one SearchViewModel across search/detail/new-book.
            navigation(startDestination = Routes.SEARCH, route = Routes.SEARCH_GRAPH) {
                composable(Routes.SEARCH) { entry ->
                    val vm = entry.sharedSearchViewModel(navController)
                    SearchScreen(
                        viewModel = vm,
                        onOpenResult = {
                            vm.select(it)
                            navController.navigate(Routes.SEARCH_DETAIL)
                        },
                        onCreateFromScratch = { navController.navigate(Routes.NEW_BOOK) },
                    )
                }
                composable(Routes.SEARCH_DETAIL) { entry ->
                    val vm = entry.sharedSearchViewModel(navController)
                    SearchDetailScreen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onAdded = { navController.popBackStack(Routes.SEARCH, inclusive = false) },
                    )
                }
                composable(Routes.NEW_BOOK) { entry ->
                    val vm = entry.sharedSearchViewModel(navController)
                    NewBookScreen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onCreated = { navController.popBackStack(Routes.SEARCH, inclusive = false) },
                    )
                }
            }

            // Achievements graph shares one AchievementsViewModel.
            navigation(startDestination = Routes.ACHIEVEMENTS, route = Routes.ACHIEVEMENTS_GRAPH) {
                composable(Routes.ACHIEVEMENTS) { entry ->
                    val vm = entry.sharedAchievementsViewModel(navController)
                    AchievementsScreen(
                        viewModel = vm,
                        onOpenYear = { navController.navigate(Routes.yearBooks(it)) },
                    )
                }
                composable(
                    route = "${Routes.YEAR_BOOKS}/{${Routes.ARG_YEAR}}",
                    arguments = listOf(navArgument(Routes.ARG_YEAR) { type = NavType.IntType }),
                ) { entry ->
                    val vm = entry.sharedAchievementsViewModel(navController)
                    val year = entry.arguments?.getInt(Routes.ARG_YEAR) ?: 0
                    YearBooksScreen(
                        year = year,
                        viewModel = vm,
                        onOpenBook = { navController.navigate(Routes.book(it)) },
                        onBack = { navController.popBackStack() },
                    )
                }
            }

            composable(
                route = "${Routes.BOOK}/{${Routes.ARG_BOOK_ID}}",
                arguments = listOf(navArgument(Routes.ARG_BOOK_ID) { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong(Routes.ARG_BOOK_ID) ?: return@composable
                BookScreen(bookId = id, onBack = { navController.popBackStack() })
            }

            composable(Routes.IMPORT_EXPORT) {
                ImportExportScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun androidx.navigation.NavBackStackEntry.sharedSearchViewModel(
    navController: NavHostController,
): SearchViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(Routes.SEARCH_GRAPH) }
    return viewModel(parentEntry, factory = AppViewModelProvider.Factory)
}

@Composable
private fun androidx.navigation.NavBackStackEntry.sharedAchievementsViewModel(
    navController: NavHostController,
): AchievementsViewModel {
    val parentEntry = remember(this) { navController.getBackStackEntry(Routes.ACHIEVEMENTS_GRAPH) }
    return viewModel(parentEntry, factory = AppViewModelProvider.Factory)
}
