package com.yorick.cokotools.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yorick.cokotools.R
import com.yorick.cokotools.ui.viewmodels.ContributorViewModel
import com.yorick.cokotools.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(
    modifier: Modifier = Modifier,
    contributorViewModel: ContributorViewModel,
    scope: CoroutineScope,
    hostState: SnackbarHostState,
) {
    val contributors = contributorViewModel.contributors
    val context = LocalContext.current
    val message = stringResource(id = R.string.contributors_info)
    val onClickInfo: () -> Unit = {
        scope.launch {
            val result = hostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {}
                SnackbarResult.Dismissed -> {}
            }
        }
    }
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .animateContentSize()
        ) {
            Column(Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.contributors),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    IconButton(onClick = onClickInfo) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(id = R.string.action_helps)
                        )
                    }
                }
                LazyHorizontalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    rows = StaggeredGridCells.Fixed(4),
                ) {
                    items(items = contributors, key = { it.id }) { contributor ->
                        AssistChip(
                            onClick = { /*TODO*/ },
                            label = {
                                Text(text = contributor.name)
                            },
                            modifier = Modifier.padding(start = 12.dp),
                            border = AssistChipDefaults.assistChipBorder(
                                borderColor = MaterialTheme.colorScheme.primary,
                                borderWidth = 1.5.dp
                            ),
                            shape = MaterialTheme.shapes.medium,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(MaterialTheme.shapes.medium),
                        model = ImageRequest.Builder(context)
                            .data("https://yorick.love/img/qrcode/wechat_donate_code.png")
                            .crossfade(true)
                            .build(),
                        error = painterResource(id = R.drawable.donate_code),
                        contentScale = ContentScale.Fit,
                        contentDescription = stringResource(id = R.string.action_donate)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(id = R.string.wx_donate_code))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.please_donate),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp, bottom = 6.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    val uri: Uri = Uri.parse("http://www.coolapk.com/u/3774603")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }) {
                    Text(text = stringResource(id = R.string.coolapk_index))
                }
                Button(onClick = { Utils.joinQQGroup(context) }) {
                    Text(text = stringResource(id = R.string.joinGroup))
                }
            }
            val blogUri = "https://yorick.love"
            val text = buildAnnotatedString {
                append(stringResource(id = R.string.visit_blog))
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                    )
                ) {
                    append(blogUri)
                }
            }
            ClickableText(
                modifier = Modifier.padding(start = 12.dp, bottom = 10.dp),
                text = text, onClick = {
                    val uri: Uri = Uri.parse(blogUri)
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                })
        }
    }
}