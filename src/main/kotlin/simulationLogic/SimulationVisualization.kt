package simulationLogic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun SimulationVisualization(simulation: PhysarumSimulation) {
    val isRunning by simulation.isRunning.collectAsState()
    
    LaunchedEffect(isRunning) {
        while (isActive && isRunning) {
            simulation.step()
            delay(16)
        }
    }
    
    var pheromoneData by remember { mutableStateOf(simulation.getPheromoneFieldData()) }
    var agentPositions by remember { mutableStateOf(simulation.getAgentsPositions()) }
    
    LaunchedEffect(Unit) {
        while (isActive) {

        }
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawPheromoneField(pheromoneData)

    }
}

private fun DrawScope.drawPheromoneField(pheromoneData: Array<FloatArray>) {
    val width = size.width.toInt()
    val height = size.height.toInt()
    val fieldWidth = pheromoneData[0].size
    val fieldHeight = pheromoneData.size
    
    val scaleX = width.toFloat() / fieldWidth
    val scaleY = height.toFloat() / fieldHeight
    
}
