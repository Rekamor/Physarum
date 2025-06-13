package simulationLogic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import model.SimulationSettings

@Composable
fun SimulationVisualization(simulation: PhysarumSimulation) {
    val isRunning by simulation.isRunning.collectAsState()

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawPheromoneField(Array(1) { FloatArray(0)}, simulation)
    }
}

private fun DrawScope.drawPheromoneField(data: Array<FloatArray>, simulation: PhysarumSimulation) {
    val width = size.width.toInt()
    val height = size.height.toInt()
    val fieldWidth = data[0].size
    val fieldHeight = data.size

    val settings = simulation.settings
    
    val scaleX = width.toFloat() / fieldWidth
    val scaleY = height.toFloat() / fieldHeight

}
