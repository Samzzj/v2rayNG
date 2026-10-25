package com.v2ray.ang.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.colorFabActive
import com.v2ray.ang.ui.compose.colorFabInactiveDark
import com.v2ray.ang.ui.compose.colorFabInactiveLight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch

/**
 * iOS 27 风格悬浮玻璃底栏。
 *
 * 布局要点：
 *  - 不再占用 Scaffold 的 bottomBar 槽位，而是浮在列表之上（见 MainScreen 的 Box 叠层），
 *    否则底栏背后没有内容，背景模糊无从体现。
 *  - 列表底部已预留 80dp contentPadding，因此浮层不会遮住最后一项。
 *  - hazeEffect 提供真实背景高斯模糊；Android 11 及以下自动退化为半透明色块。
 */
@Composable
fun MainBottomBar(
    displayText: String,
    isRunning: Boolean,
    isDarkTheme: Boolean,
    hazeState: HazeState,
    onAction: (MainAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(isRunning) {
        if (!isRunning) {
            rotationAnim.snapTo(0f)
        }
    }

    val pillShape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 14.dp, end = 14.dp, top = 34.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(pillShape)
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        backgroundColor = Color(0xCCFFFFFF),
                        tint = HazeTint(Color(0x40FFFFFF)),
                        blurRadius = 30.dp,
                        noiseFactor = 0.02f
                    )
                )
                .border(width = 1.dp, color = Color(0x99FFFFFF), shape = pillShape)
                .clickable(onClick = { onAction(MainAction.TestCurrentServer) })
                .padding(start = 24.dp, end = 92.dp, top = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics {
                    contentDescription = displayText
                }
            )
        }

        FloatingActionButton(
            onClick = {
                if (!isRunning) {
                    scope.launch {
                        rotationAnim.animateTo(
                            targetValue = 720f,
                            animationSpec = tween(durationMillis = 3000)
                        )
                    }
                }
                onAction(MainAction.ToggleService)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 18.dp, top = 6.dp)
                .offset(y = (-18).dp),
            containerColor = if (isRunning) colorFabActive
            else if (isDarkTheme) colorFabInactiveDark
            else colorFabInactiveLight
        ) {
            Icon(
                painter = if (isRunning) painterResource(R.drawable.ic_stop_24dp)
                else painterResource(R.drawable.ic_play_24dp),
                contentDescription = stringResource(
                    if (isRunning) R.string.acc_stop else R.string.acc_start
                ),
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { rotationZ = rotationAnim.value }
            )
        }
    }
}
