package com.yorick.cokotools.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Grid3x3
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.yorick.cokotools.R

object CookToolsRoute {
    const val HOME = "Home"
    const val SHELL = "Shell"
    const val SETTING = "Setting"
    const val DONATE = "Donate"
    const val TOOL = "Tool"
}

data class CookToolsTopLevelDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class NavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: CookToolsTopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    CookToolsTopLevelDestination(
        route = CookToolsRoute.HOME,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = R.string.home
    ),
    CookToolsTopLevelDestination(
        route = CookToolsRoute.SHELL,
        selectedIcon = Icons.Filled.Grid3x3,
        unselectedIcon = Icons.Outlined.Grid3x3,
        iconTextId = R.string.shell
    ),
    CookToolsTopLevelDestination(
        route = CookToolsRoute.SETTING,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        iconTextId = R.string.setting
    ),
)
