// XXX: Weird how the errors only go away if I opt in twice (?!)
@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)

package com.example.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder
import java.io.PrintWriter
import java.io.StringWriter

@Composable
fun ShaderToy(shaderResourcePath: String) {
    // FIXME: Possibly not best idea to compile the shader on the UI thread
    val runtimeEffectResult = remember(shaderResourcePath) {
        try {
            val header = ResourceLoader.Default.load("shadertoy-uniforms.sksl").use { stream ->
                stream.bufferedReader().readText()
            }
            val skSL = ResourceLoader.Default.load(shaderResourcePath).use { stream ->
                stream.bufferedReader().readText()
            }
            Result.success(RuntimeEffect.makeForShader(header + "\n" + skSL))
        } catch (e: Exception) {
            if (e is InterruptedException) {
                Thread.currentThread().interrupt()
            }
            Result.failure(e)
        }
    }

    Surface(modifier = Modifier.background(MaterialTheme.colors.surface)) {
        runtimeEffectResult.fold(
            { runtimeEffect -> ShaderCanvas(runtimeEffect) },
            { e -> ErrorView(e) }
        )
    }
}

@Composable
private fun ShaderCanvas(runtimeEffect: RuntimeEffect) {
    var timeState by remember { mutableStateOf(0.0f) }
    var timeDeltaState by remember { mutableStateOf(0.0f) }
    var mouseState by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(Unit) {
        val startTime = withFrameMillis { it }
        while (isActive) {
            withFrameMillis { frameTime ->
                timeState = (frameTime - startTime) / 1000.0f
                timeDeltaState = frameTime / 1000.0f
            }
        }
    }

    val builder = RuntimeShaderBuilder(runtimeEffect)
    val shader by derivedStateOf {
        builder.uniform("iResolution", 800.0f, 600.0f, 1.0f)
        builder.uniform("iTime", timeState)
        builder.uniform("iTimeDelta", timeDeltaState)
        // TODO: Mouse is technically supposed to work like this:
        //       mouse.xy  = mouse position during last button down
        //   abs(mouse.zw) = mouse position during last button click
        //  sign(mouze.z)  = button is down
        //  sign(mouze.w)  = button is clicked <- this makes no sense, does it fire for one frame only?
        builder.uniform("iMouse", mouseState.x, mouseState.y, 0.0f, 0.0f)
        builder.makeShader()
    }

    val brush by derivedStateOf { ShaderBrush(shader) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onPointerEvent(PointerEventType.Press) { event: PointerEvent ->
                mouseState = event.changes.first().position
            }
    ) {
        drawRect(brush = brush, size = Size(800.0f, 600.0f))
    }
}

@Composable
private fun ErrorView(e: Throwable) {
    SelectionContainer {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical)
        ) {
            Text(text = e.stackTraceToString())
        }
    }
}
