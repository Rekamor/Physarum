package simulationLogic

import model.SimulationSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhysarumSimulation(initialSettings: SimulationSettings) {
    private val _settings = MutableStateFlow(initialSettings)
    val settings: StateFlow<SimulationSettings> = _settings.asStateFlow()
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    init {
        initializeAgents()
    }
    
    fun updateSettings(newSettings: SimulationSettings) {
        val oldSettings = _settings.value
        _settings.value = newSettings
        
        updateAgentSettings()
    }
    
    private fun initializeAgents() {
        val currentSettings = _settings.value

    }
    
    private fun updateAgentSettings() {
        val currentSettings = _settings.value

    }
    
    fun start() {
        _isRunning.value = true
    }
    
    fun stop() {
        _isRunning.value = false
    }

}
