package com.sergiom.flycheck.ui.screens.b_custom.components.editor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onDrag: (Float) -> Unit,
    registerItemPosition: (String, Float, Float) -> Unit,
    content: @Composable (
        modifier: Modifier,
        delta: Float,
        startExternalDrag: (delta: Float) -> Unit,
        endExternalDrag: () -> Unit
    ) -> Unit
) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        label = "dragOffset"
    )

    // DEBUG: muestra estado de renderizado
    println("🧩 [DraggableItem] Renderizando itemId=$itemId con isDragging=$isDragging offsetY=$offsetY")

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
                    println("🟡 [DraggableItem] isDragging == true → activando pointerInput para $itemId")
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                println("🟢 [DraggableItem] onDragStart ejecutado")
                                onDragStart()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetY += dragAmount.y
                                onDrag(offsetY)
                            },
                            onDragEnd = {
                                println("🛑 [DraggableItem] onDragEnd ejecutado con offsetY: $offsetY")
                                onDragEnd(offsetY)
                                offsetY = 0f
                            },
                            onDragCancel = {
                                println("🟠 [DraggableItem] onDragCancel ejecutado con offsetY: $offsetY")
                                onDragEnd(offsetY)
                                offsetY = 0f
                            }
                        )
                    }
                } else Modifier
            )
    ) {
        content(
            Modifier
                .fillMaxWidth(),
            offsetY, // ✅ Añade esto como segundo parámetro: el delta
            { delta ->
                offsetY += delta
                onDrag(offsetY)
            },
            {
                println("🟥 [DraggableItem] endExternalDrag ejecutado con offsetY: $offsetY")
                onDragEnd(offsetY)
                offsetY = 0f
            }
        )

    }
}
