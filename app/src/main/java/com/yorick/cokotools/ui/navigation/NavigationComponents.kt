package com.yorick.cokotools.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CokoToolsNavigationBottomBar(
    selectedDestination: String,
    navigateToTopLevelDestination: (CookToolsTopLevelDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(77.dp)
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { destination ->
            NavigationBarItem(
                selected = selectedDestination == destination.route,
                onClick = { navigateToTopLevelDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (selectedDestination == destination.route) destination.selectedIcon
                        else destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.iconTextId)
                    )
                },
                label = { Text(text = stringResource(id = destination.iconTextId)) },
                alwaysShowLabel = false
            )
        }
    }
}

@Composable
fun CokoToolsNavigationBottomBar(
    selectedDestination: String,
    onClick: () -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(77.dp)
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { destination ->
            NavigationBarItem(
                selected = selectedDestination == destination.route,
                onClick = {  },
                icon = {
                    Icon(
                        imageVector = if (selectedDestination == destination.route) destination.selectedIcon
                        else destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.iconTextId)
                    )
                },
                label = { Text(text = stringResource(id = destination.iconTextId)) },
                alwaysShowLabel = false
            )
        }
    }
}