package com.yorick.cokotools.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.blueTools
import com.yorick.cokotools.ui.commonTools
import com.yorick.cokotools.ui.components.CokoToolsLogo
import com.yorick.cokotools.ui.theme.CokoToolsTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        CokoToolsCard(
            modifier = Modifier.height(240.dp),
            toolsCategory = R.string.common,
            toolsDesc = R.string.common_desc,
            rows = StaggeredGridCells.Fixed(3)
        ) {
            items(items = commonTools, key = { it.toolName }) { tool ->
                Button(
                    modifier = Modifier.padding(start = 14.dp),
                    onClick = { tool.toolAction(context) }
                ) {
                    Text(
                        text = stringResource(id = tool.toolName),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CokoToolsCard(
            modifier = Modifier.height(180.dp),
            toolsCategory = R.string.blue,
            toolsDesc = R.string.blue_desc,
            rows = StaggeredGridCells.Fixed(2)
        ) {
            items(items = blueTools, key = { it.toolName }) { tool ->
                Button(
                    modifier = Modifier.padding(start = 14.dp),
                    onClick = { tool.toolAction(context) }
                ) {
                    Text(
                        text = stringResource(id = tool.toolName),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CokoToolsCard(
    modifier: Modifier = Modifier,
    @StringRes toolsCategory: Int,
    @StringRes toolsDesc: Int,
    rows: StaggeredGridCells,
    content: LazyStaggeredGridScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var popState by remember {
                    mutableStateOf(false)
                }
                Text(
                    modifier = Modifier,
                    text = stringResource(id = toolsCategory),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                IconButton(onClick = { popState = !popState }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(id = R.string.action_helps)
                    )
                }
                if (popState) {
                    AlertDialog(
                        onDismissRequest = { popState = false },
                        title = {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CokoToolsLogo()
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(id = toolsCategory),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(id = toolsDesc),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { popState = false }) {
                                Text(text = stringResource(id = R.string.understand))
                            }
                        }
                    )
                }
            }
            LazyHorizontalStaggeredGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                rows = rows,
                content = content
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    CokoToolsTheme {
        HomeScreen()
    }
}

