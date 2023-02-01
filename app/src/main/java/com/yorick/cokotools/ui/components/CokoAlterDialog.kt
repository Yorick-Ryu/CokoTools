package com.yorick.cokotools.ui.components

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.yorick.cokotools.R
import com.yorick.cokotools.data.model.Category
import com.yorick.cokotools.data.model.Tool

// 失败弹窗组件
@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String = stringResource(id = R.string.title_error),
    text: String = stringResource(id = R.string.common_tips),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseAlterDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        negativeText = R.string.exceptions_read_help_doc,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

// 基础弹窗组件
@Composable
fun BaseAlterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    @StringRes positiveText: Int = R.string.accept,
    @StringRes negativeText: Int = R.string.cancel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelable: Boolean = true
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CokoToolsLogo()
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = positiveText))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = negativeText))
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = cancelable,
            dismissOnBackPress = cancelable
        )
    )
}

// 基础弹窗组件
@Composable
fun BaseAlterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: String,
    @StringRes positiveText: Int = R.string.accept,
    @StringRes negativeText: Int = R.string.cancel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelable: Boolean = true,
    text: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CokoToolsLogo()
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = positiveText))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = negativeText))
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = cancelable,
            dismissOnBackPress = cancelable
        ),
        text = text,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewToolDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: (tool: Tool, context: Context) -> Unit,
    onDismiss: () -> Unit,
    categories: List<Category>,
    toolMaxID: Int,
) {
    val context = LocalContext.current
    val inputRequired: String = stringResource(id = R.string.input_required)
    var name by remember {
        mutableStateOf("")
    }
    var desc by remember {
        mutableStateOf("")
    }
    var categoryName by remember {
        mutableStateOf(categories.first().name)
    }
    var tPackage by remember {
        mutableStateOf("")
    }
    var activity by remember {
        mutableStateOf("")
    }
    var okMsg by remember {
        mutableStateOf("")
    }
    var errMsg by remember {
        mutableStateOf("")
    }
    val tool = Tool(
        id = toolMaxID + 1,
        name = name,
        desc = desc,
        category = categories.first { it.name == categoryName }.categoryId,
        tPackage = tPackage,
        activity = activity,
        okMsg = okMsg,
        errMsg = errMsg
    )
    BaseAlterDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = stringResource(id = R.string.add_new_tool),
        onConfirm = {
            if (tool.name != "" && tool.tPackage != "" && tool.activity != "") {
                onConfirm(tool, context)
            } else {
                Toast.makeText(context, inputRequired, Toast.LENGTH_SHORT).show()
            }
        },
        onDismiss = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                label = {
                    Text(
                        text = stringResource(id = R.string.tool_name)
                                + stringResource(id = R.string.required)
                    )
                },
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.tool_desc)) },
                value = desc,
                onValueChange = { desc = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            CokoDropDownMenu(
                modifier = Modifier,
                label = stringResource(id = R.string.tool_category),
                menuList = categories.map { it.name },
                categoryName = categoryName,
                onValueChange = { categoryName = it },
                onClickMenu = { categoryName = it }
            )
            OutlinedTextField(
                label = {
                    Text(
                        text = stringResource(id = R.string.tool_package)
                                + stringResource(id = R.string.required)
                    )
                },
                value = tPackage,
                onValueChange = { tPackage = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                label = {
                    Text(
                        text = stringResource(id = R.string.tool_activity)
                                + stringResource(id = R.string.required)
                    )
                },
                value = activity,
                onValueChange = { activity = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.tool_ok_msg)) },
                value = okMsg,
                onValueChange = { okMsg = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            OutlinedTextField(
                label = { Text(text = stringResource(id = R.string.tool_err_msg)) },
                value = errMsg,
                onValueChange = { errMsg = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
        }
    }
}

