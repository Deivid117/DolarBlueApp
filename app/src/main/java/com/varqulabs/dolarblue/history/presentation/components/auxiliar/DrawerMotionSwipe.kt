package com.varqulabs.dolarblue.history.presentation.components.auxiliar

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.varqulabs.dolarblue.history.domain.model.Conversion
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragToManageConversion(
    conversion: Conversion,
    onClickDeleteConversion: () -> Unit,
    onClickShowEditConversionDialog: () -> Unit,
    content: @Composable (Conversion) -> Unit
) {
    val density = LocalDensity.current

    val defaultActionSize = 80.dp
    val actionSizePx = with(density) { defaultActionSize.toPx() }
    val endActionSizePx = with(density) { (defaultActionSize * 2).toPx() }

    val decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay()
    val confirmValueChange: (newValue: DragAnchors) -> Boolean = { true }

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            anchors = DraggableAnchors {
                DragAnchors.Center at 0f
                DragAnchors.End at endActionSizePx
            },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec,
            confirmValueChange = confirmValueChange,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } }
        )
    }

    DraggableItem(
        state = anchoredDraggableState,
        endAction = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            ) {
                EditAction(
                    Modifier
                        .width(defaultActionSize)
                        .fillMaxHeight()
                        .offset {
                            IntOffset(
                                ((-anchoredDraggableState
                                    .requireOffset()) + actionSizePx)
                                    .roundToInt(), 0
                            )
                        }
                ) { onClickShowEditConversionDialog() }
                DeleteAction(
                    Modifier
                        .width(defaultActionSize)
                        .fillMaxHeight()
                        .offset {
                            IntOffset(
                                ((-anchoredDraggableState
                                    .requireOffset() * 0.5f) + actionSizePx)
                                    .roundToInt(), 0
                            )
                        }
                ) { onClickDeleteConversion() }
            }
        },
        content = {
            content(conversion)
        }
    )
}