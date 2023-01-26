package com.yorick.cokotools.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.components.CokoToolsAppBar
import com.yorick.cokotools.ui.navigation.CokoToolsNavigationBottomBar
import com.yorick.cokotools.ui.navigation.CookToolsRoute
import com.yorick.cokotools.ui.navigation.NavigationActions
import com.yorick.cokotools.ui.screens.DonateScreen
import com.yorick.cokotools.ui.screens.HomeScreen
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.ui.viewmodels.ContributorViewModel
import com.yorick.cokotools.ui.viewmodels.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CokoToolsApp(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    contributorViewModel: ContributorViewModel,
) {
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val navigationActions = remember(navController) { NavigationActions(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: CookToolsRoute.HOME
    var barsVisibility by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(
                visible = barsVisibility,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it },
            ) {
                CokoToolsAppBar(onClickDonate = {
                    navController.navigate(
                        CookToolsRoute.DONATE
                    ) { launchSingleTop = true }
                })
            }
            AnimatedVisibility(
                visible = !barsVisibility,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    title = { Text(text = stringResource(id = R.string.action_donate)) }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = barsVisibility,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                CokoToolsNavigationBottomBar(
                    selectedDestination = selectedDestination,
                    navigateToTopLevelDestination = navigationActions::navigateTo
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = hostState) }
    ) {
        NavHost(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp),
            navController = navController,
            startDestination = CookToolsRoute.HOME
        ) {
            composable(route = CookToolsRoute.HOME) {
                barsVisibility = true
                HomeScreen(
                    homeViewModel = homeViewModel,
                    scope = scope,
                    hostState = hostState
                )
            }
            composable(route = CookToolsRoute.SHELL) {
                barsVisibility = true
            }
            composable(route = CookToolsRoute.SETTING) {
                barsVisibility = true
            }
            composable(route = CookToolsRoute.DONATE) {
                barsVisibility = false
                DonateScreen(
                    contributorViewModel = contributorViewModel,
                    scope = scope,
                    hostState = hostState
                )
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