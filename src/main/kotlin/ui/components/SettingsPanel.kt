package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.AgentSettings
import model.PheromoneSettings
import model.SimulationSettings

@Composable
fun SettingsPanel(
    height: Dp,
    settings: SimulationSettings,
    onSettingsChanged: (SimulationSettings) -> Unit,
    onExportSettings: () -> Unit,
    onImportSettingsClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(height)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Настройки симуляции Physarum",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Блок настроек агентов
            AgentSettingsCard(
                agentSettings = settings.agent,
                onAgentSettingsChanged = { newAgentSettings ->
                    onSettingsChanged(settings.copy(agent = newAgentSettings))
                }
            )
            
            // Блок настроек феромонов
            PheromoneSettingsCard(
                pheromoneSettings = settings.pheromone,
                onPheromoneSettingsChanged = { newPheromoneSettings ->
                    onSettingsChanged(settings.copy(pheromone = newPheromoneSettings))
                }
            )
            
            // Кнопки действий с настройками
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                // Кнопка импорта настроек
                Button(
                    onClick = onImportSettingsClick
                ) {
                    Text("Импорт настроек")
                }
                
                // Кнопка экспорта настроек
                Button(
                    onClick = onExportSettings
                ) {
                    Text("Экспорт настроек")
                }
            }
        }
    }
}

@Composable
private fun AgentSettingsCard(
    agentSettings: AgentSettings,
    onAgentSettingsChanged: (AgentSettings) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Настройки агентов",
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsTextField(
                value = agentSettings.speed.toString(),
                onValueChange = { value ->
                    val newSpeed = value.toFloatOrNull() ?: agentSettings.speed
                    onAgentSettingsChanged(agentSettings.copy(speed = newSpeed))
                },
                label = "Скорость движения агента"
            )
            
            SettingsTextField(
                value = agentSettings.viewAngle.toString(),
                onValueChange = { value ->
                    val newViewAngle = value.toFloatOrNull() ?: agentSettings.viewAngle
                    onAgentSettingsChanged(agentSettings.copy(viewAngle = newViewAngle))
                },
                label = "Угол зрения агента (в градусах)"
            )
            
            SettingsTextField(
                value = agentSettings.count.toString(),
                onValueChange = { value ->
                    val newCount = value.toIntOrNull() ?: agentSettings.count
                    onAgentSettingsChanged(agentSettings.copy(count = newCount))
                },
                label = "Количество агентов"
            )
            
            SettingsTextField(
                value = agentSettings.pheromoneAmount.toString(),
                onValueChange = { value ->
                    val newAmount = value.toFloatOrNull() ?: agentSettings.pheromoneAmount
                    onAgentSettingsChanged(agentSettings.copy(pheromoneAmount = newAmount))
                },
                label = "Количество феромона, создаваемого агентом"
            )
        }
    }
}

@Composable
private fun PheromoneSettingsCard(
    pheromoneSettings: PheromoneSettings,
    onPheromoneSettingsChanged: (PheromoneSettings) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Настройки феромонов",
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsTextField(
                value = pheromoneSettings.diffusionRate.toString(),
                onValueChange = { value ->
                    val newRate = value.toFloatOrNull() ?: pheromoneSettings.diffusionRate
                    onPheromoneSettingsChanged(pheromoneSettings.copy(diffusionRate = newRate))
                },
                label = "Скорость диффузии феромона"
            )
            
            SettingsTextField(
                value = pheromoneSettings.evaporationRate.toString(),
                onValueChange = { value ->
                    val newRate = value.toFloatOrNull() ?: pheromoneSettings.evaporationRate
                    onPheromoneSettingsChanged(pheromoneSettings.copy(evaporationRate = newRate))
                },
                label = "Скорость испарения феромона"
            )
            
            SettingsTextField(
                value = pheromoneSettings.influence.toString(),
                onValueChange = { value ->
                    val newInfluence = value.toFloatOrNull() ?: pheromoneSettings.influence
                    onPheromoneSettingsChanged(pheromoneSettings.copy(influence = newInfluence))
                },
                label = "Сила влияния феромона на агентов"
            )
        }
    }
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
}