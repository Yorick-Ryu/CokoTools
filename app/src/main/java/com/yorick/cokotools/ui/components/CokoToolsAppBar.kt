package com.yorick.cokotools.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.theme.CokoToolsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CokoToolsAppBar(
    modifier: Modifier = Modifier,
    onClickHelp: () -> Unit = {},
    onClickDonate: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            CokoToolsLogo(modifier.padding(horizontal = 10.dp))
        },
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = onClickHelp) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = stringResource(id = R.string.action_helps)
                )
            }
            IconButton(onClick = onClickDonate) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(id = R.string.action_donate)
                )
            }
        }
    )
}

enum class TabPage {
    Local, Remote
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolTabBar(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onTabSelected: (tabPage: TabPage) -> Unit
) {
    TabRow(
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        divider = {}
    ) {
        ToolTab(
            icon = Icons.Outlined.GridView,
            title = stringResource(R.string.local),
            onClick = { onTabSelected(TabPage.Local) }
        )
        ToolTab(
            icon = Icons.Outlined.CloudDownload,
            title = stringResource(R.string.remote),
            onClick = { onTabSelected(TabPage.Remote) }
        )
    }
}

@Composable
fun ToolTab(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(bottom = 6.dp, top = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}


@Preview
@Composable
fun CokoToolsAppBarPreview() {
    CokoToolsTheme {
        CokoToolsAppBar()
    }
}