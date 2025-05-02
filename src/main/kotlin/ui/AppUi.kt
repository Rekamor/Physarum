package ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.SimulationSettings
import ui.components.ImportDialog
import utils.ClipboardException
import utils.ClipboardManager
import utils.SettingsSerializationException
import utils.SettingsSerializer

@Composable
@Preview
fun App() {
    // Основное состояние
    val isExpanded = remember { mutableStateOf(false) }
    val isImportDialogOpen = remember { mutableStateOf(false) }
    val importText = remember { mutableStateOf("") }
    val statusMessage = remember { mutableStateOf("") }
    
    // Для получения фокуса и обработки клавиши Escape
    val focusRequester = remember { FocusRequester() }

    // Настройки для симуляции
    val settings = remember { mutableStateOf(SimulationSettings()) }

//    val simulation = remember {
//        PhysarumSimulation(
//            width = 800,
//            height = 600,
//            settings = settings.value
//        )
//    }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            kotlinx.coroutines.delay(16) // ~60fps
//            simulation.update()
//        }
//    }

    fun exportSettings() {
        try {
            val settingsJson = SettingsSerializer.toJson(settings.value)
            ClipboardManager.copyToClipboard(settingsJson)
            statusMessage.value = "Настройки скопированы в буфер обмена"
        } catch (e: SettingsSerializationException) {
            statusMessage.value = e.message ?: "Неизвестная ошибка при экспорте настроек"
        } catch (e: ClipboardException) {
            statusMessage.value = e.message ?: "Неизвестная ошибка при работе с буфером обмена"
        }
    }

    fun importSettings(settingsText: String) {
        try {
            val importedSettings = SettingsSerializer.fromJson(settingsText)
            settings.value = importedSettings
//            simulation.updateSettings(importedSettings)
            statusMessage.value = "Настройки успешно импортированы"
        } catch (e: SettingsSerializationException) {
            statusMessage.value = e.message ?: "Ошибка при импорте настроек: ${e.message}"
        }

        isImportDialogOpen.value = false
    }

    fun pasteFromClipboard() {
        try {
            val clipboard = ClipboardManager.getFromClipboard()
            importText.value = clipboard
        } catch (e: ClipboardException) {
            statusMessage.value = e.message ?: "Неизвестная ошибка при вставке из буфера обмена"
        }
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Основной контент - визуализация симуляции
//            SimulationVisualization(simulation)

            // Кнопка "Настройки"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Button(
                    onClick = { isExpanded.value = true },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Настройки")
                }
            }

            // Показываем статусное сообщение, если оно есть
            if (statusMessage.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusMessage.value,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                )

                // Автоматически скрываем сообщение через некоторое время
                LaunchedEffect(statusMessage.value) {
                    kotlinx.coroutines.delay(3000)
                    statusMessage.value = ""
                }
            }

            // Диалог импорта настроек
            if (isImportDialogOpen.value) {
                ImportDialog(
                    importText = importText.value,
                    onImportTextChange = { importText.value = it },
                    onPasteFromClipboard = { pasteFromClipboard() },
                    onImportSettings = { importSettings(importText.value) },
                    onDismiss = { isImportDialogOpen.value = false }
                )
            }

            // Полноэкранные настройки
            AnimatedVisibility(
                visible = isExpanded.value,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) +
                        slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
            ) {
                // Запрашиваем фокус при открытии панели
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                
                // Затемнение фона с обработчиком клавиши Escape
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .focusRequester(focusRequester)
                        .onKeyEvent { keyEvent ->
                            // Проверяем, что была нажата клавиша Escape
                            if (keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyDown) {
                                isExpanded.value = false
                                true // Обработали событие
                            } else {
                                false // Не обработали событие
                            }
                        }
                ) {
                    // Содержимое настроек
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                    ) {
                        // Рамка настроек (только белые края без фона)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = "Настройки",
                                    style = MaterialTheme.typography.h5,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Вместо стандартной панели настроек используем модифицированную
                                // с уменьшенной шириной полей и добавлением диапазонов
                                FullscreenSettingsContent(
                                    settings = settings.value,
                                    onSettingsChanged = {
                                        settings.value = it
                                        // simulation.updateSettings(it)
                                    },
                                    onExportSettings = { exportSettings() },
                                    onImportSettingsClick = { isImportDialogOpen.value = true }
                                )
                            }
                            
                            // Крестик размещаем над содержимым
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .size(48.dp)  // Увеличенная область нажатия
                                    .background(
                                        color = Color.Black.copy(alpha = 0.5f), 
                                        shape = RoundedCornerShape(24.dp)
                                    )  // Добавляем фон для лучшей видимости
                            ) {
                                IconButton(
                                    onClick = { isExpanded.value = false },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Закрыть",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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

@Composable
fun SettingsFieldWithRange(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    minValue: Float? = null,
    maxValue: Float? = null,
    isIntegerValue: Boolean = false  // Добавляем флаг для целочисленных значений
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Уменьшенное в два раза поле ввода
        TextField(
            value = value,
            onValueChange = { newValue ->
                // Для целочисленных значений разрешаем только цифры
                if (isIntegerValue) {
                    if (newValue.matches(Regex("^\\d*$")) || newValue.isEmpty()) {
                        onValueChange(newValue)
                    }
                } else {
                    // Для float значений разрешаем цифры и одну точку
                    if (newValue.matches(Regex("^\\d*\\.?\\d*$")) || newValue.isEmpty()) {
                        onValueChange(newValue)
                    }
                }
            },
            modifier = Modifier.weight(1f),
            label = { Text(label, color = Color.White) },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                backgroundColor = Color.Black.copy(alpha = 0.3f),
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f)
            )
        )

        // Если есть диапазон, показываем его справа
        if (minValue != null && maxValue != null) {
            Spacer(modifier = Modifier.width(16.dp))

            // Настройка диапазона в правой половине
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Диапазон: $minValue - $maxValue",
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Slider(
                    value = value.toFloatOrNull()?.coerceIn(minValue, maxValue) ?: minValue,
                    onValueChange = { 
                        // Для целочисленных значений округляем до целого
                        if (isIntegerValue) {
                            onValueChange(it.toInt().toString())
                        } else {
                            onValueChange(it.toString())
                        }
                    },
                    valueRange = minValue..maxValue,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White.copy(alpha = 0.8f),
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )
            }
        } else {
            // Если диапазона нет, оставляем пустое место
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication, title = "Physarum Simulation") {
        App()
    }
}