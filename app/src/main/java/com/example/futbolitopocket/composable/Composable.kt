import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.futbolitopocket.R

@Composable
fun MyScreen(content: @Composable () -> Unit) {
    BackgroundImage(
        backgroundImageResId = R.drawable.futbol,// ID de recurso de la imagen de fondo
        content = { content() } // El contenido que quieres mostrar encima de la imagen de fondo
    )
}


@Composable
fun BackgroundImage(backgroundImageResId: Int, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Green)) {
        // Mostrar la imagen de fondo
        Image(
            painter = painterResource(id = backgroundImageResId),
            contentDescription = null, // El contenido de la imagen no necesita descripción
            modifier = Modifier.fillMaxSize() // La imagen ocupa todo el tamaño del contenedor
        )

        // Mostrar el contenido encima de la imagen de fondo
        content()
    }
}


// @Composable función para dibujar los elementos del campo de juego
@Composable
fun ElementosCampo(
    x: Float, y: Float, circleRadiusPx: Float, screenWidthPx: Float, screenHeightPx: Float,
    squares: List<Offset>, squareSizePx: Float, blueScore: Int, grayScore: Int,
    onGoalScored: (Color) -> Unit
) {
    Canvas(modifier = Modifier) {
        // Dibujar el círculo (pelota)
        drawCircle(
            color = Color.White,
            center = Offset(
                x.coerceIn(circleRadiusPx, screenWidthPx - circleRadiusPx),
                y.coerceIn(circleRadiusPx, screenHeightPx - circleRadiusPx)
            ),
            radius = circleRadiusPx
        )

        // Dibujar la portería superior
        val goalTop = 0f
        val goalBottom = screenHeightPx / 24
        val goalWidth = screenWidthPx / 6
        drawRect(
            color = Color.Blue,
            topLeft = Offset((screenWidthPx - goalWidth) / 2, goalTop),
            size = Size(goalWidth, goalBottom)
        )

        // Dibujar la portería inferior
        drawRect(
            color = Color.Gray,
            topLeft = Offset((screenWidthPx - goalWidth) / 2, screenHeightPx - goalBottom),
            size = Size(goalWidth, goalBottom)
        )

        // Dibujar los cuadrados (obstáculos)
        for (square in squares) {
            drawRect(
                color = Color.Red,
                topLeft = square,
                size = Size(squareSizePx, squareSizePx)
            )
        }

        // Dibujar marcadores de puntuación
        val scoreMarkerSize = 30f
        val scoreMarkerOffset = 100f
        // Dibujar el texto de puntuación
        val scoreTextSize = 60f
        val paint = Paint().asFrameworkPaint().apply {
            color = Color.Black.toArgb() // Color del texto
            textSize = scoreTextSize // Tamaño del texto
            textAlign = android.graphics.Paint.Align.CENTER // Alineación del texto



        }
        // Dibujar el marcador de puntuación azul
        drawContext.canvas.nativeCanvas.drawText("Azul :$grayScore", scoreMarkerOffset, scoreMarkerOffset + scoreTextSize /2, paint)
        // Dibujar el marcador de puntuación gris
        drawContext.canvas.nativeCanvas.drawText(
            "Gris :$blueScore",
            screenWidthPx - scoreMarkerOffset - scoreTextSize / 2,
            screenHeightPx - scoreMarkerOffset,
            paint
        )
    }
}