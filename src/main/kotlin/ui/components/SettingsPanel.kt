package ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.SimulationSettings

@Composable
fun FullscreenSettingsContent(
    settings: SimulationSettings,
    onSettingsChanged: (SimulationSettings) -> Unit,
    onExportSettings: () -> Unit,
    onImportSettingsClick: () -> Unit
) {
    // Блок настроек агентов
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        elevation = 4.dp,
        backgroundColor = Color.Black.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Настройки агентов",
                style = MaterialTheme.typography.subtitle1,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Поля ввода с диапазонами
            SettingsFieldWithRange(
                value = settings.agent.speed.toString(),
                onValueChange = { value ->
                    val newSpeed = value.toFloatOrNull() ?: settings.agent.speed
                    val newAgent = settings.agent.copy(speed = newSpeed)
                    onSettingsChanged(settings.copy(agent = newAgent))
                },
                label = "Скорость движения агента",
                minValue = 0f,
                maxValue = 10f
            )

            SettingsFieldWithRange(
                value = settings.agent.viewAngle.toString(),
                onValueChange = { value ->
                    val newAngle = value.toFloatOrNull() ?: settings.agent.viewAngle
                    val newAgent = settings.agent.copy(viewAngle = newAngle)
                    onSettingsChanged(settings.copy(agent = newAgent))
                },
                label = "Угол зрения агента (в градусах)",
                minValue = 0f,
                maxValue = 180f
            )

            // Исправлено для количества агентов - преобразуем float в int
            SettingsFieldWithRange(
                value = settings.agent.count.toString(),
                onValueChange = { value ->
                    val floatValue = value.toFloatOrNull() ?: settings.agent.count.toFloat()
                    val newCount = floatValue.toInt()
                    val newAgent = settings.agent.copy(count = newCount)
                    onSettingsChanged(settings.copy(agent = newAgent))
                },
                label = "Количество агентов",
                minValue = 100f,
                maxValue = 10000f,
                isIntegerValue = true  // Добавляем флаг для целочисленных значений
            )

            // Добавлен диапазон для количества феромонов
            SettingsFieldWithRange(
                value = settings.agent.pheromoneAmount.toString(),
                onValueChange = { value ->
                    val newAmount = value.toFloatOrNull() ?: settings.agent.pheromoneAmount
                    val newAgent = settings.agent.copy(pheromoneAmount = newAmount)
                    onSettingsChanged(settings.copy(agent = newAgent))
                },
                label = "Количество феромона",
                minValue = 0f,
                maxValue = 20f
            )
        }
    }

    // Блок настроек феромонов
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        elevation = 4.dp,
        backgroundColor = Color.Black.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Настройки феромонов",
                style = MaterialTheme.typography.subtitle1,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            SettingsFieldWithRange(
                value = settings.pheromone.diffusionRate.toString(),
                onValueChange = { value ->
                    val newRate = value.toFloatOrNull() ?: settings.pheromone.diffusionRate
                    val newPheromone = settings.pheromone.copy(diffusionRate = newRate)
                    onSettingsChanged(settings.copy(pheromone = newPheromone))
                },
                label = "Скорость диффузии феромона",
                minValue = 0f,
                maxValue = 1f
            )

            SettingsFieldWithRange(
                value = settings.pheromone.evaporationRate.toString(),
                onValueChange = { value ->
                    val newRate = value.toFloatOrNull() ?: settings.pheromone.evaporationRate
                    val newPheromone = settings.pheromone.copy(evaporationRate = newRate)
                    onSettingsChanged(settings.copy(pheromone = newPheromone))
                },
                label = "Скорость испарения феромона",
                minValue = 0f,
                maxValue = 1f
            )

            SettingsFieldWithRange(
                value = settings.pheromone.influence.toString(),
                onValueChange = { value ->
                    val newInfluence = value.toFloatOrNull() ?: settings.pheromone.influence
                    val newPheromone = settings.pheromone.copy(influence = newInfluence)
                    onSettingsChanged(settings.copy(pheromone = newPheromone))
                },
                label = "Сила влияния феромона",
                minValue = 0f,
                maxValue = 5f
            )
        }
    }

    // Заглушки для дополнительных функций
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        elevation = 4.dp,
        backgroundColor = Color.Black.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Дополнительные функции",
                style = MaterialTheme.typography.subtitle1,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Заглушка для генерации рандомных настроек */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Рандомные настройки")
                }

                Button(
                    onClick = { /* Заглушка для скриншота */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Скриншот")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Заглушка для записи экрана */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Запись экрана")
                }

                Button(
                    onClick = { /* Заглушка для переключения градиентов */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Переключение градиентов")
                }
            }
        }
    }

    // Кнопки импорта/экспорта настроек
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onImportSettingsClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("Импорт настроек")
        }

        Button(
            onClick = onExportSettings,
            modifier = Modifier.weight(1f)
        ) {
            Text("Экспорт настроек")
        }
    }
}