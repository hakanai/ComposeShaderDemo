package com.example.common

import androidx.compose.runtime.Composable

@Composable
fun App() {
//    ShaderToy("mandelbulb.sksl")

// TODO: Make this work
// https://stackoverflow.com/questions/76451513/what-is-too-large-for-a-sksl-shader-and-how-can-i-use-larger-shaders
    ShaderToy("ocean.sksl")
}
