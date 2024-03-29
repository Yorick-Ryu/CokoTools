package com.yorick.cokotools.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun CokoDropDownMenu(
    modifier: Modifier = Modifier,
    label: String = "Category",
    menuList: List<String>,
    categoryName: String,
    onValueChange: (String) -> Unit,
    onClickMenu: (String) -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = modifier,
            value = categoryName,
            readOnly = true,
            onValueChange = onValueChange,
            enabled = true,
            trailingIcon = {
                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(
                        if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "expend button"
                    )
                }
            },
            label = {
                Text(text = label)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        DropdownMenu(
            modifier = Modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 0.dp),
        ) {
            for (menu in menuList) {
                DropdownMenuItem(
                    text = { Text(text = menu) },
                    onClick = {
                        onClickMenu(menu)
                        expanded = false
                    },
                )
            }
        }
    }
}