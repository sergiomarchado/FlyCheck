package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt


@Composable
fun DraggableItem(
    isDragging: Boolean,
    itemId: String,
    onDragStart: () -> Unit,
    onDragEnd: (Float) -> Unit,
    onDrag: (Offset) -> Unit, // ✅ Offset en vez de Float
    registerItemPosition: (String, Float, Float) -> Unit,
    content: @Composable (
        modifier: Modifier,
        offset: Offset,
        startExternalDrag: (Offset) -> Unit,
        endExternalDrag: () -> Unit
    ) -> Unit
) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        label = "dragOffset"
    )

    Box(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                val y = layoutCoordinates.positionInWindow().y
                val height = layoutCoordinates.size.height.toFloat()
                registerItemPosition(itemId, y, height)
            }
            .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
            .then(
                if (isDragging) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                onDragStart()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetY += dragAmount.y
                                onDrag(dragAmount) // ✅ Pasamos Offset directamente
                            },
                            onDragEnd = {
                                onDragEnd(offsetY)
                                offsetY = 0f
                            },
                            onDragCancel = {
                                onDragEnd(offsetY)
                                offsetY = 0f
                            }
                        )
                    }
                } else Modifier
            )
    ) {
        content(
            Modifier.fillMaxWidth(),
            Offset(0f, offsetY), // ✅ offset en formato Offset
            { delta ->
                offsetY += delta.y
                onDrag(delta)
            },
            {
                onDragEnd(offsetY)
                offsetY = 0f
            }
        )
    }
}
