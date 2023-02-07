package com.yorick.cokotools.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yorick.cokotools.R
import com.yorick.cokotools.data.model.CategoryWithTools
import com.yorick.cokotools.data.model.Tool
import com.yorick.cokotools.ui.components.BaseAlterDialog
import com.yorick.cokotools.ui.components.ErrorDialog
import com.yorick.cokotools.ui.viewmodels.HomeViewModel
import com.yorick.cokotools.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    onClickFab: () -> Unit,
    scope: CoroutineScope,
    hostState: SnackbarHostState,
) {
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    // 分类详细信息弹窗
    var popState by remember {
        mutableStateOf(false)
    }
    var cateTitle by remember { mutableStateOf("") }
    var cateText by remember { mutableStateOf("") }
    if (popState) {
        BaseAlterDialog(
            onDismissRequest = { popState = false },
            title = cateTitle,
            text = cateText,
            onDismiss = { popState = false },
            onConfirm = { popState = false },
        )
    }
    // 功能打开失败弹窗
    val context = LocalContext.current
    val closeErrorDialog = homeViewModel::closeErrorDialog
    var errMsg: String by remember { mutableStateOf("") }
    if (!homeViewModel.isSuccess) {
        ErrorDialog(
            onDismissRequest = closeErrorDialog,
            text = errMsg,
            onConfirm = closeErrorDialog,
            onDismiss = {
                closeErrorDialog()
                Utils.openDoc(context)
            }
        )
    }
    val needDesc = stringResource(id = R.string.need_desc)
    val commonTips = stringResource(id = R.string.common_tips)
    // 加载界面
    if (homeUiState.loading) {
        Column(modifier.verticalScroll(rememberScrollState())) {
            repeat(3) {
                OnLoadingCard()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = !homeUiState.loading,
            enter = fadeIn()
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = rememberLazyListState()
            ) {
                items(
                    items = homeUiState.categoryWithTools.filter { it.tools.isNotEmpty() },
                    key = { it.category.categoryId }) { categoryWithTools: CategoryWithTools ->
                    val rows = ceil(categoryWithTools.tools.size.toDouble() / 3).toInt()
                    val height: Dp = 60.dp + 60.dp * rows
                    CokoToolsCard(
                        modifier = Modifier.height(height),
                        toolsCategory = categoryWithTools.category.name,
                        rows = StaggeredGridCells.Fixed(rows),
                        tools = categoryWithTools.tools,
                        onClickButton = { tool ->
                            homeViewModel.startActivity(
                                context,
                                tool.tPackage,
                                tool.activity,
                                tool.okMsg
                            )
                            errMsg = tool.errMsg ?: commonTips
                        },
                        scope = scope,
                        hostState = hostState
                    ) {
                        cateTitle = categoryWithTools.category.name
                        cateText =
                            categoryWithTools.category.desc ?: needDesc
                        popState = !popState
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            onClick = onClickFab
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(id = R.string.edit)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CokoToolsCard(
    modifier: Modifier = Modifier,
    toolsCategory: String,
    rows: StaggeredGridCells,
    tools: List<Tool>,
    onClickButton: (tool: Tool) -> Unit,
    scope: CoroutineScope,
    hostState: SnackbarHostState,
    onClickCategoryInfo: () -> Unit // 点击分类详情信息
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(Modifier.padding(bottom = 4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier,
                    text = toolsCategory,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                IconButton(onClick = onClickCategoryInfo) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(id = R.string.action_helps)
                    )
                }
            }
            var onClick = onClickButton
            LazyHorizontalStaggeredGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                rows = rows
            ) {
                items(items = tools, key = { it.id }) { tool ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val desc = tool.desc ?: stringResource(id = R.string.need_desc)
                    val onLongPress = {
                        scope.launch {
                            val result = hostState.showSnackbar(
                                message = desc,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {}
                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    }
                    if (isPressed) {
                        onLongPress()
                        onClick = {}
                    }
                    Button(
                        modifier = Modifier
                            .padding(start = 14.dp),
                        onClick = { onClick(tool) },
                        interactionSource = interactionSource
                    ) {
                        Text(
                            text = tool.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnLoadingCard(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val float by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(
        modifier = modifier
            .alpha(float)
            .fillMaxWidth()
            .height(200.dp)
    ) {}
}

