package com.yorick.cokotools.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yorick.cokotools.R
import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.Tool
import com.yorick.cokotools.ui.components.AddNewToolDialog
import com.yorick.cokotools.ui.components.ToolTabBar
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun ToolScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    addNewTool: (tool: Tool, context: Context) -> Unit,
    upLoadTool: (tool: Tool, context: Context) -> Unit,
    deleteTool: (tool: Tool) -> Unit,
    downLoadTool: (tool: Tool) -> Unit,
    scope: CoroutineScope
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState()
    val state1 = rememberLazyListState()
    val state2 = rememberLazyListState()
    val fabVisibility by derivedStateOf {
        state1.firstVisibleItemIndex == 0 && pagerState.currentPage == 0
    }
    var alterVisibility by remember { mutableStateOf(false) }
    if (alterVisibility) {
        AddNewToolDialog(
            onDismissRequest = { alterVisibility = false },
            onConfirm = { tool, context ->
                addNewTool(tool, context)
                alterVisibility = false
            },
            onDismiss = { alterVisibility = false },
            categories = uiState.categories,
            toolMaxID = uiState.tools.last().id
        )
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            ToolTabBar(
                pagerState = pagerState
            ) {
                scope.launch {
                    pagerState.animateScrollToPage(
                        page = it.ordinal,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                modifier = modifier,
                visible = fabVisibility,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = { alterVisibility = true },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(id = R.string.add)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    )
    { paddingValues ->
        val tabs = listOf<@Composable () -> Unit>(
            {
                TooList(
                    tools = uiState.tools,
                    categories = uiState.categories,
                    isLocal = true,
                    upLoadTool = upLoadTool,
                    deleteTool = deleteTool,
                    downLoadTool = downLoadTool,
                    state = state1
                )
            },
            {
                TooList(
                    tools = uiState.showTools,
                    categories = uiState.categories,
                    isLocal = false,
                    upLoadTool = upLoadTool,
                    deleteTool = deleteTool,
                    downLoadTool = downLoadTool,
                    state = state2
                )
            }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(paddingValues),
            pageCount = tabs.size
        ) { page ->
            tabs[page]()
        }
    }
}

@Composable
fun TooList(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    tools: List<Tool>,
    isLocal: Boolean,
    upLoadTool: (tool: Tool, context: Context) -> Unit,
    deleteTool: (tool: Tool) -> Unit,
    downLoadTool: (tool: Tool) -> Unit,
    state: LazyListState
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 5.dp),
        state = state
    ) {
        if (tools.isNotEmpty()) {
            if (isLocal) {
                items(items = tools, key = { it.id }) { tool ->
                    ToolsListItem(tool = tool, categories = categories) {
                        IconButton(onClick = { upLoadTool(tool, context) }) {
                            Icon(
                                imageVector = Icons.Outlined.Upload,
                                contentDescription = stringResource(id = R.string.upload)
                            )
                        }
                        IconButton(onClick = { deleteTool(tool) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(id = R.string.delete)
                            )
                        }
                    }
                }
            } else {
                items(items = tools, key = { it.id }) { tool ->
                    ToolsListItem(tool = tool, categories = categories) {
                        IconButton(
                            onClick = { downLoadTool(tool) },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = stringResource(id = R.string.download)
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_tool),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ToolsListItem(
    modifier: Modifier = Modifier,
    tool: Tool,
    categories: List<Category>,
    operation: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .width(50.dp)
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = 6.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier.widthIn(min = 10.dp, max = 240.dp)) {
            Row {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = modifier.width(10.dp))
                OSTag(OSName = categories.first { it.categoryId == (tool.category ?: 1) }.name)
            }
            Text(
                text = tool.desc ?: stringResource(id = R.string.need_desc),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row {
            operation()
        }
    }
}

@Composable
fun OSTag(
    modifier: Modifier = Modifier,
    OSName: String
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = OSName,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview
@Composable
fun OSCTagPreview() {
    CokoToolsTheme {
        OSTag(OSName = "OriginOS")
    }
}




