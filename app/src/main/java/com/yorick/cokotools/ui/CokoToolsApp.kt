package com.yorick.cokotools.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.yorick.cokotools.ui.screens.*
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.ui.viewmodels.ContributorViewModel
import com.yorick.cokotools.ui.viewmodels.HomeViewModel
import com.yorick.cokotools.ui.viewmodels.SettingViewModel
import com.yorick.cokotools.ui.viewmodels.ShellViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CokoToolsApp(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    shellViewModel: ShellViewModel,
    contributorViewModel: ContributorViewModel,
    settingViewModel: SettingViewModel
) {
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val navigationActions = remember(navController) { NavigationActions(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: CookToolsRoute.HOME
    var barsVisibility by remember { mutableStateOf(true) }
    var topTitle by remember { mutableStateOf("") }
    Scaffold(
        modifier = modifier.navigationBarsPadding(),
        topBar = {
            if (barsVisibility) {
                CokoToolsAppBar(onClickDonate = {
                    navController.navigate(
                        CookToolsRoute.DONATE
                    ) { launchSingleTop = true }
                })
            } else {
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
                    title = { Text(text = topTitle) }
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
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = CookToolsRoute.HOME
        ) {
            composable(route = CookToolsRoute.HOME) {
                barsVisibility = true
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onClickFab = {
                        navController.navigate(CookToolsRoute.TOOL)
                        homeViewModel.getAllRemoteTools()
                    },
                    scope = scope,
                    hostState = hostState
                )
            }
            composable(route = CookToolsRoute.SHELL) {
                barsVisibility = true
                ShellScreen(
                    shellViewModel = shellViewModel,
                    scope = scope,
                    hostState = hostState
                )
            }
            composable(route = CookToolsRoute.SETTING) {
                barsVisibility = true
                SettingScreen(settingViewModel = settingViewModel)
            }
            composable(route = CookToolsRoute.TOOL) {
                topTitle = stringResource(id = R.string.tool_management)
                barsVisibility = false
                ToolScreen(
                    homeViewModel = homeViewModel,
                    addNewTool = homeViewModel::addNewTool,
                    upLoadTool = homeViewModel::uploadTool,
                    deleteTool = homeViewModel::deleteTool,
                    downLoadTool = homeViewModel::downloadTool,
                    scope = scope
                )
            }
            composable(route = CookToolsRoute.DONATE) {
                topTitle = stringResource(id = R.string.action_donate)
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