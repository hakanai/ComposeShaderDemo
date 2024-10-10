package standalone

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.window.singleWindowApplication
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

fun main() = singleWindowApplication(title = "Shaaaderrrs") {
    val runtimeEffect = remember {
        RuntimeEffect.makeForShader(
            """
                uniform vec3 iResolution;
                vec4 main(in vec2 fragCoord) {
                    return vec4(fragCoord / iResolution.xy, 0, 1); 
                }
            """.trimIndent()
        )
    }

    val shader by derivedStateOf {
        val builder = RuntimeShaderBuilder(runtimeEffect)
        builder.uniform("iResolution", 800.0f, 600.0f, 1.0f)
        builder.makeShader()
    }

    val brush by derivedStateOf {
        ShaderBrush(shader)
    }

    Surface(modifier = Modifier.background(MaterialTheme.colors.surface)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(brush = brush, size = Size(800.0f, 600.0f))
        }
    }
}
