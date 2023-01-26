package com.yorick.cokotools.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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


@Preview
@Composable
fun CokoToolsAppBarPreview() {
    CokoToolsTheme() {
        CokoToolsAppBar()
    }
}