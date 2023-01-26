package com.yorick.cokotools.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.Icons
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
    scope: CoroutineScope = rememberCoroutineScope(),
    hostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    if (homeViewModel.categoryWithTools.isNotEmpty()) {
        LazyColumn(modifier = modifier, state = rememberLazyListState()) {
            items(
                items = homeViewModel.categoryWithTools.filter { it.tools.isNotEmpty() },
                key = { it.category.categoryId }) { categoryWithTools: CategoryWithTools ->
                val rows = ceil(categoryWithTools.tools.size.toDouble() / 3).toInt()
                val height: Dp = 60.dp + 60.dp * rows
                CokoToolsCard(
                    modifier = Modifier.height(height),
                    toolsCategory = categoryWithTools.category.name,
                    toolsDesc = categoryWithTools.category.desc
                        ?: stringResource(id = R.string.need_desc),
                    rows = StaggeredGridCells.Fixed(rows),
                    tools = categoryWithTools.tools,
                    toolOnClick = homeViewModel::startActivity,
                    isSuccess = homeViewModel.isSuccess,
                    closeErrorDialog = homeViewModel::closeErrorDialog,
                    scope = scope,
                    hostState = hostState
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    } else {
        Column {
            repeat(3) {
                OnLoadingCard()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CokoToolsCard(
    modifier: Modifier = Modifier,
    toolsCategory: String,
    toolsDesc: String,
    rows: StaggeredGridCells,
    tools: List<Tool>,
    toolOnClick: (context: Context, packageName: String, activityName: String, okMsg: String?) -> Unit,
    isSuccess: Boolean,
    closeErrorDialog: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    hostState: SnackbarHostState = remember { SnackbarHostState() },
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
                    text = toolsCategory,
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
                    BaseAlterDialog(
                        onDismissRequest = { popState = false },
                        title = toolsCategory,
                        text = toolsDesc,
                        onDismiss = { popState = false },
                        onConfirm = { popState = false }
                    )
                }
            }
            val context = LocalContext.current
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
                    var onClick = { toolOnClick(context, tool.tPackage, tool.activity, tool.okMsg) }

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
                        onClick = onClick,
                        interactionSource = interactionSource
                    ) {
                        Text(
                            text = tool.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (!isSuccess) {
                        ErrorDialog(
                            onDismissRequest = closeErrorDialog,
                            text = tool.errMsg ?: stringResource(id = R.string.common_tips),
                            onConfirm = closeErrorDialog,
                            onDismiss = {
                                closeErrorDialog()
                                Utils.openDoc(context)
                            }
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
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(
        modifier = modifier
            .alpha(float)
            .fillMaxWidth()
            .height(200.dp)
    ) {

    }
}

//@Preview
//@Composable
//fun HomeScreenPreview() {
//    CokoToolsTheme {
//        HomeScreen()
//    }
//}

