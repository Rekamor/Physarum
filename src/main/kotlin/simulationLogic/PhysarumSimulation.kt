package simulationLogic

import model.SimulationSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import simulation.Agent

class PhysarumSimulation(initialSettings: SimulationSettings) {
    private val _settings = MutableStateFlow(initialSettings)
    val settings: StateFlow<SimulationSettings> = _settings.asStateFlow()
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    private var agents = Array(initialSettings.agent.count) { Agent() }
    private var pheromoneField = PheromoneField(
        width = 800,
        height = 600,
        diffusionRate = initialSettings.pheromone.diffusionRate,
        evaporationRate = initialSettings.pheromone.evaporationRate
    )
    
    init {
        initializeAgents()
    }
    
    fun updateSettings(newSettings: SimulationSettings) {
        val oldSettings = _settings.value
        _settings.value = newSettings
        
        if (oldSettings.agent.count != newSettings.agent.count) {
            agents = Array(newSettings.agent.count) { Agent() }
            initializeAgents()
        }
        
        pheromoneField.setDiffusionRate(newSettings.pheromone.diffusionRate)
        pheromoneField.setEvaporationRate(newSettings.pheromone.evaporationRate)
        
        updateAgentSettings()
    }
    
    private fun initializeAgents() {
        val currentSettings = _settings.value
        agents.forEach { agent ->
            agent.speed = currentSettings.agent.speed
            agent.viewAngle = currentSettings.agent.viewAngle
            agent.pheromoneAmount = currentSettings.agent.pheromoneAmount
            
            // Случайное начальное положение и направление
            agent.x = (0..800).random().toFloat()
            agent.y = (0..600).random().toFloat()
            agent.angle = (0..360).random().toFloat()
        }
    }
    
    private fun updateAgentSettings() {
        val currentSettings = _settings.value
        agents.forEach { agent ->
            agent.speed = currentSettings.agent.speed
            agent.viewAngle = currentSettings.agent.viewAngle
            agent.pheromoneAmount = currentSettings.agent.pheromoneAmount
        }
    }
    
    fun start() {
        _isRunning.value = true
    }
    
    fun stop() {
        _isRunning.value = false
    }
    
    fun step() {
        agents.forEach { agent ->
            val sensorResult = sense(agent)
            agent.move(sensorResult)
            
            pheromoneField.deposit(agent.x.toInt(), agent.y.toInt(), agent.pheromoneAmount)
        }
        
        pheromoneField.update()
    }
    
    fun getPheromoneFieldData(): Array<FloatArray> {
        return pheromoneField.getData()
    }
    
}
