package com.yorick.cokotools.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.theme.CokoToolsTheme
import com.yorick.cokotools.ui.viewmodels.ShellUiState
import com.yorick.cokotools.ui.viewmodels.ShellViewModel
import com.yorick.cokotools.ui.viewmodels.ShizukuState
import rikka.shizuku.Shizuku

@Composable
fun ShellScreen(
    modifier: Modifier = Modifier,
    shellViewModel: ShellViewModel,
) {
    val uiState by shellViewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = modifier.fillMaxSize()) {
        ShellCards(uiState = uiState)
    }
}

@Composable
fun ShellCards(
    modifier: Modifier = Modifier,
    uiState: ShellUiState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable {
                        when (uiState.shizukuState) {
                            ShizukuState.NOT_RUNNING -> {}
                            ShizukuState.RUNNING_BUT_LOW_VERSION -> {}
                            ShizukuState.RUNNING_BUT_NOT_GRANTED -> {
                                Shizuku.requestPermission(1)
                            }
                            ShizukuState.RUNNING_AND_GRANTED -> {}
                        }
                    }
                    .padding(vertical = 10.dp, horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.shizuku_state),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                when (uiState.shizukuState) {
                    ShizukuState.NOT_RUNNING -> {
                        ShizukuStateCardContent(
                            icon = Icons.Outlined.HighlightOff,
                            shizukuState = stringResource(id = R.string.not_running),
                            stateDesc = stringResource(id = R.string.not_running_tip),
                        )
                    }
                    ShizukuState.RUNNING_BUT_LOW_VERSION -> {
                        ShizukuStateCardContent(
                            icon = Icons.Outlined.ErrorOutline,
                            shizukuState = stringResource(id = R.string.running),
                            permissionState = stringResource(id = R.string.low_version),
                            stateDesc = stringResource(id = R.string.current_version) + " "
                                    + uiState.shizukuVersion + " "
                                    + stringResource(id = R.string.running_but_low_version_tip),
                        )
                    }
                    ShizukuState.RUNNING_BUT_NOT_GRANTED -> {
                        ShizukuStateCardContent(
                            icon = Icons.Outlined.ErrorOutline,
                            shizukuState = stringResource(id = R.string.running),
                            permissionState = stringResource(id = R.string.not_granted),
                            stateDesc = stringResource(id = R.string.click_to_grant),
                        )
                    }
                    ShizukuState.RUNNING_AND_GRANTED -> {
                        ShizukuStateCardContent(
                            icon = Icons.Outlined.CheckCircle,
                            shizukuState = stringResource(id = R.string.running),
                            permissionState = stringResource(id = R.string.granted),
                            stateDesc = stringResource(id = R.string.version) + " " + uiState.shizukuVersion,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShizukuStateCardContent(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    shizukuState: String,
    permissionState: String? = null,
    stateDesc: String = "",
) {
    val stateColor by animateColorAsState(
        if (shizukuState == stringResource(id = R.string.running) &&
            permissionState == stringResource(id = R.string.granted)
        ) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.tertiary
        }
    )
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape)
                .background(color = stateColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = modifier,
                imageVector = icon,
                contentDescription = shizukuState,
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.height(45.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = shizukuState,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (permissionState != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    StateDescTag(
                        stateDesc = permissionState,
                        stateColor = stateColor
                    )
                }
            }
            Text(
                text = stateDesc,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
}

@Composable
fun StateDescTag(
    modifier: Modifier = Modifier,
    stateDesc: String,
    stateColor: Color,
) {
    Box(
        modifier = modifier
            .background(
                color = stateColor,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stateDesc,
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview
@Composable
fun ShellCardsPreview() {
    CokoToolsTheme {
        ShellCards(uiState = ShellUiState(shizukuState = ShizukuState.NOT_RUNNING))
    }
}