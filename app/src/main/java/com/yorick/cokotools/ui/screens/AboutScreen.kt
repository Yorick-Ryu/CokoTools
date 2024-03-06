package com.yorick.cokotools.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
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
import com.yorick.cokotools.ui.components.CardWithTitle
import com.yorick.cokotools.ui.viewmodels.ContributorViewModel
import com.yorick.cokotools.util.Utils
import com.yorick.cokotools.util.Utils.openUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    contributorViewModel: ContributorViewModel,
    scope: CoroutineScope,
    hostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val contributors = contributorViewModel.contributors.sortedBy { -it.amount }
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
    val donateTips = stringResource(id = R.string.donate_tips)
    val onClickDonateInfo: () -> Unit = {
        scope.launch {
            val result = hostState.showSnackbar(
                message = donateTips,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {}
                SnackbarResult.Dismissed -> {}
            }
        }
    }
    val blogInfo = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.outline)) {
            append(stringResource(id = R.string.visit_blog))
        }
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
            )
        ) {
            append(Utils.BLOG_URL)
        }
    }
    val openSourceInfo = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.outline)) {
            append(stringResource(id = R.string.open_source_desc))
        }
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
            )
        ) {
            append(stringResource(id = R.string.open_source))
        }
    }
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CardWithTitle(
            modifier = Modifier
                .height(240.dp)
                .animateContentSize(),
            cardTitle = stringResource(id = R.string.contributors),
            onClickInfo = onClickInfo
        ) {
            LazyHorizontalStaggeredGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 14.dp),
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
                        border = AssistChipDefaults.assistChipBorder(enabled = true),
                        shape = MaterialTheme.shapes.medium,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CardWithTitle(
            modifier = Modifier,
            cardTitle = stringResource(id = R.string.donate),
            onClickInfo = onClickDonateInfo
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
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
                            .data(Utils.DONATE_CODE_URL)
                            .crossfade(true)
                            .build(),
                        error = painterResource(id = R.drawable.donate_code),
                        contentScale = ContentScale.Fit,
                        contentDescription = stringResource(id = R.string.donate)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(id = R.string.wx_donate_code))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.donate_info),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(onClick = { Utils.openWeChatScan(context) }) {
                        Text(text = stringResource(id = R.string.open_wechat))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        Utils.openAlipayPayPage(
                            Utils.ALIPAY_DONATE_URL,
                            context
                        )
                    }) {
                        Text(text = stringResource(id = R.string.donate_by_alipay))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CardWithTitle(
            modifier = Modifier
                .animateContentSize(),
            cardTitle = stringResource(id = R.string.about)
        ) {
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 6.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    openUrl(Utils.COOLAPK_URL, context)
                }) {
                    Text(text = stringResource(id = R.string.coolapk_index))
                }
                Button(onClick = { Utils.joinQQGroup(context) }) {
                    Text(text = stringResource(id = R.string.joinGroup))
                }
            }
            ClickableText(
                modifier = Modifier.padding(start = 16.dp, bottom = 10.dp),
                text = blogInfo, onClick = {
                    openUrl(Utils.BLOG_URL, context)
                }
            )
            ClickableText(
                modifier = Modifier.padding(start = 16.dp, bottom = 10.dp),
                text = openSourceInfo, onClick = {
                    openUrl(Utils.OPEN_SOURCE_URL, context)
                }
            )
        }
    }
}