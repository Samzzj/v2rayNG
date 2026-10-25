package com.v2ray.ang.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * 底部唯一控件：一个浮在渐变上的玻璃胶囊启动开关。
 *
 *  - 不再占用 Scaffold 的 bottomBar 槽位，而是叠在列表之上，
 *    否则背后没有内容，背景模糊无从体现。
 *  - 列表底部预留了 contentPadding，开关不会遮住最后一张卡片。
 *  - hazeEffect 提供真实高斯模糊；Android 11 及以下自动退化为半透明色块。
 */
@Composable
fun MainBottomBar(
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

    val pillShape = RoundedCornerShape(32.dp)
    val accent = when {
        isRunning -> colorFabActive
        isDarkTheme -> colorFabInactiveDark
        else -> colorFabInactiveLight
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(top = 40.dp, bottom = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 18.dp,
                    shape = pillShape,
                    clip = false,
                    ambientColor = Color(0x1F000000),
                    spotColor = Color(0x33000000)
                )
                .clip(pillShape)
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        backgroundColor = Color(0xC4FFFFFF),
                        tint = HazeTint(Color(0x33FFFFFF)),
                        blurRadius = 40.dp,
                        noiseFactor = 0.03f
                    )
                )
                .border(width = 1.dp, color = Color(0xD9FFFFFF), shape = pillShape)
                .clickable {
                    if (!isRunning) {
                        scope.launch {
                            rotationAnim.animateTo(
                                targetValue = 720f,
                                animationSpec = tween(durationMillis = 3000)
                            )
                        }
                    }
                    onAction(MainAction.ToggleService)
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (isRunning) {
                        painterResource(R.drawable.ic_stop_24dp)
                    } else {
                        painterResource(R.drawable.ic_play_24dp)
                    },
                    contentDescription = stringResource(
                        if (isRunning) R.string.acc_stop else R.string.acc_start
                    ),
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer { rotationZ = rotationAnim.value }
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = stringResource(if (isRunning) R.string.acc_stop else R.string.acc_start),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(18.dp))
        }
    }
}
