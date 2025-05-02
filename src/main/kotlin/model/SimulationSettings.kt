package model

import kotlinx.serialization.Serializable

@Serializable
data class SimulationSettings(
    val agent: AgentSettings = AgentSettings(),
    val pheromone: PheromoneSettings = PheromoneSettings()
)

@Serializable
data class AgentSettings(
    val speed: Float = 2f,
    val viewAngle: Float = 45f,
    val count: Int = 1000,
    val pheromoneAmount: Float = 5f
)

@Serializable
data class PheromoneSettings(
    val diffusionRate: Float = 0.1f,
    val evaporationRate: Float = 0.05f,
    val influence: Float = 1.0f
)