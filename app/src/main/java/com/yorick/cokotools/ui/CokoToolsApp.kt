package com.yorick.cokotools.ui

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yorick.cokotools.ui.navigation.CokoToolsNavigationBottomBar
import com.yorick.cokotools.ui.navigation.CookToolsRoute
import com.yorick.cokotools.ui.navigation.NavigationActions
import com.yorick.cokotools.ui.screen.HomeScreen
import com.yorick.cokotools.ui.theme.CokoToolsTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CokoToolsApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) { NavigationActions(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: CookToolsRoute.HOME
    Scaffold(
        modifier = modifier,
        topBar = { CokoToolsAppBar() },
        bottomBar = {
            CokoToolsNavigationBottomBar(
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationActions::navigateTo
            )
        }
    ) {
        NavHost(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp),
            navController = navController,
            startDestination = CookToolsRoute.HOME
        ) {
            composable(route = CookToolsRoute.HOME) {
                HomeScreen()
            }
            composable(route = CookToolsRoute.SHELL) {

            }
            composable(route = CookToolsRoute.SETTING) {

            }
        }
    }
}

@Preview
@Composable
fun CokoToolsAppPreview() {
    CokoToolsTheme {
//        CokoToolsApp()
    }
}