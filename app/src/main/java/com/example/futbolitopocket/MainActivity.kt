package com.example.futbolitopocket


import ElementosCampo
import MyScreen
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


import androidx.compose.runtime.mutableStateOf


import android.hardware.SensorEvent


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.futbolitopocket.utilities.checkCollision

class MainActivity : ComponentActivity(),SensorEventListener {



    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // Score counters for each goal
    private var blueScore by mutableStateOf(0)
    private var grayScore by mutableStateOf(0)


    private var posX = mutableStateOf(0f)
    private var posY = mutableStateOf(0f)
    private var velX = mutableStateOf(0f)
    private var velY = mutableStateOf(0f)
    var RADIUS= 10.dp
    // Sensitivity factor
    private val sensitivityFactor = 4f
    private val zFactor = 0.5f // Factor de ajuste para el eje Z

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtiene una instancia del SensorManager para interactuar con los sensores del dispositivo
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

       // Obtiene una referencia al sensor de acelerómetro predeterminado del dispositivo
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        // para escuchar eventos del sensor de acelerómetro.
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)


        setContent {
            // Obtiene la configuración actual del dispositivo
            val configuration = LocalConfiguration.current
           // Obtiene la densidad actual del dispositivo
            val density = LocalDensity.current

         // Define el radio del círculo en dp
            val circleRadiusDp = RADIUS
          // Convierte dp a px
            val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
            val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
            val circleRadiusPx = with(density) { circleRadiusDp.toPx() }

         // Efecto lanzado una vez cuando el componente es lanzado
            LaunchedEffect(Unit) {
                // Inicializa la posición al centro de la pantalla
                posX.value = (screenWidthPx / 2)
                posY.value = (screenHeightPx / 2)
            }

            // Define las posiciones de los cuadrados
            val squareSizeDp = 70.dp
            val squareSizePx = with(density) { squareSizeDp.toPx() }
            val squares = remember {
                listOf(
                    Offset(screenWidthPx / 4 - squareSizePx / 2, screenHeightPx / 6 - squareSizePx / 2),
                    Offset(screenWidthPx / 2 - squareSizePx / 2, screenHeightPx / 6 - squareSizePx / 2),
                    Offset(3 * screenWidthPx / 4 - squareSizePx / 2, screenHeightPx / 6 - squareSizePx / 2),
                    Offset(screenWidthPx / 3 - squareSizePx / 2, screenHeightPx / 2 - squareSizePx / 2),
                    Offset(2 * screenWidthPx / 3 - squareSizePx / 2, screenHeightPx / 2 - squareSizePx / 2),
                    Offset(screenWidthPx / 4 - squareSizePx / 2, 5 * screenHeightPx / 6 - squareSizePx / 2),
                    Offset(screenWidthPx / 2 - squareSizePx / 2, 5 * screenHeightPx / 6 - squareSizePx / 2),
                    Offset(3 * screenWidthPx / 4 - squareSizePx / 2, 5 * screenHeightPx / 6 - squareSizePx / 2)
                )
            }

           MyScreen {
               ElementosCampo(
                   x =posX.value ,
                   y = posY.value,
                   circleRadiusPx =circleRadiusPx ,
                   screenWidthPx = screenWidthPx,
                   screenHeightPx = screenHeightPx,
                   squares =squares ,
                   squareSizePx = squareSizeDp.value,
                   blueScore = blueScore,
                   grayScore = grayScore
               ) {

               }
           }

           
        }

    }
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Actualiza la velocidad basada en los valores del acelerómetro y el factor de sensibilidad
            // Update velocity based on accelerometer values and sensitivity factor
            velX.value += it.values[0] * sensitivityFactor
            velY.value += it.values[1] * sensitivityFactor
            val velZ = it.values[2] * zFactor

            // Incorporar velZ en la lógica de movimiento
            velX.value += velZ
            velY.value += velZ

            // Update position based on velocity
            posX.value += velX.value
            posY.value += velY.value

            val maxX = resources.displayMetrics.widthPixels.toFloat()
            val maxY = resources.displayMetrics.heightPixels.toFloat()

            // Convert radius from dp to px
            val density = resources.displayMetrics.density
            val circleRadiusPx = density * 10 // Example with 10dp

            // Bounce on edges
            if (posX.value - circleRadiusPx < 0f || posX.value + circleRadiusPx > maxX) {
                velX.value = -velX.value * 0.299f // Apply damping to simulate energy loss
                posX.value = posX.value.coerceIn(circleRadiusPx, maxX - circleRadiusPx)
            }
            if (posY.value - circleRadiusPx < 0f || posY.value + circleRadiusPx > maxY) {
                velY.value = -velY.value * 0.299f // Apply damping to simulate energy loss
                posY.value = posY.value.coerceIn(circleRadiusPx, maxY - circleRadiusPx)
            }

            // Goal detection
            val goalTop = 0f
            val goalBottom = maxY / 26
            val goalWidth = maxX / 8

            val ballCenterX = posX.value
            val ballCenterY = posY.value

            val isInsideTopGoal = ballCenterY - circleRadiusPx > goalTop && ballCenterY - circleRadiusPx < goalBottom &&
                    ballCenterX > (maxX - goalWidth) / 2 && ballCenterX < (maxX + goalWidth) / 2

            val isInsideBottomGoal = ballCenterY + circleRadiusPx > maxY - goalBottom && ballCenterY + circleRadiusPx < maxY &&
                    ballCenterX > (maxX - goalWidth) / 2 && ballCenterX < (maxX + goalWidth) / 2

            if (isInsideTopGoal || isInsideBottomGoal) {
                val goalColor = if (isInsideTopGoal) Color.Blue else Color.Gray

                posX.value = maxX / 2
                posY.value = maxY / 2
                velX.value = 0f
                velY.value = 0f

                onGoalScored(goalColor)
            }

            // Check for collisions with squares
            val squares = listOf(
                Offset(maxX / 4 - circleRadiusPx / 2, maxY / 6 - circleRadiusPx / 2),
                Offset(maxX / 2 - circleRadiusPx / 2, maxY / 6 - circleRadiusPx / 2),
                Offset(3 * maxX / 4 - circleRadiusPx / 2, maxY / 6 - circleRadiusPx / 2),
                Offset(maxX / 3 - circleRadiusPx / 2, maxY / 2 - circleRadiusPx / 2),
                Offset(2 * maxX / 3 - circleRadiusPx / 2, maxY / 2 - circleRadiusPx / 2),
                Offset(maxX / 4 - circleRadiusPx / 2, 5 * maxY / 6 - circleRadiusPx / 2),
                Offset(maxX / 2 - circleRadiusPx / 2, 5 * maxY / 6 - circleRadiusPx / 2),
                Offset(3 * maxX / 4 - circleRadiusPx / 2, 5 * maxY / 6 - circleRadiusPx / 2)
            )
            for (square in squares) {
                if (checkCollision(posX.value, posY.value, circleRadiusPx, square, circleRadiusPx)) {
                    velX.value = -velX.value
                    velY.value = -velY.value
                }
            }
        }

    }


    private fun onGoalScored(color: Color){

        // Esta función se llama cada vez que se anota un gol
        if (color == Color.Blue) {
            // Aumentar el marcador del equipo azul
            blueScore++
        } else if (color == Color.Gray) {
            // Aumentar el marcador del equipo gris
            grayScore++
        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


}




