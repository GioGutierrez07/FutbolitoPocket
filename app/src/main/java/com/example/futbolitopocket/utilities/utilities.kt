package com.example.futbolitopocket.utilities

import androidx.compose.ui.geometry.Offset

fun checkCollision(
    circleX: Float, circleY: Float, circleRadius: Float,
    squareTopLeft: Offset, squareSize: Float
): Boolean {
    // Calcular las coordenadas del centro del cuadrado
    val squareCenterX = squareTopLeft.x + squareSize / 2
    val squareCenterY = squareTopLeft.y + squareSize / 2

    // Calcular la distancia mínima para detectar colisión
    val minDistance = circleRadius + squareSize

    // Calcular la distancia entre el centro de la pelota y el centro del cuadrado
    val dx = circleX - squareCenterX
    val dy = circleY - squareCenterY
    //La raíz cuadrada se utiliza en el cálculo de la distancia euclidiana entre dos puntos en un plano bidimensional
    val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

    // Verificar si la distancia entre la pelota y el cuadrado es menor que el umbral
    return distance<minDistance
}