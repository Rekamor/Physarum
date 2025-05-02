package ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.SimulationSettings
import ui.components.ImportDialog
import ui.components.ResizeHandle
import ui.components.SettingsPanel
import utils.ClipboardException
import utils.ClipboardManager
import utils.SettingsSerializer
import utils.SettingsSerializationException

@Composable
@Preview
fun App() {
    // Основное состояние
    val isExpanded = remember { mutableStateOf(false) }
    val isImportDialogOpen = remember { mutableStateOf(false) }
    val importText = remember { mutableStateOf("") }
    val statusMessage = remember { mutableStateOf("") }

    // Высота панели настроек
    val settingsPanelHeight = remember { mutableStateOf(400.dp) }
    val minPanelHeight = 200.dp
    val maxPanelHeight = 600.dp

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
            statusMessage.value = e.message ?: "Неизвестная ошибка при импорте настроек"
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

            // Контейнер для стрелки и панели настроек
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                // Кнопка-стрелка для открытия/закрытия панели
                IconButton(
                    onClick = { isExpanded.value = !isExpanded.value },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = if (isExpanded.value) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = if (isExpanded.value) "Свернуть настройки" else "Развернуть настройки"
                    )
                }

                // Панель настроек
                AnimatedVisibility(
                    visible = isExpanded.value,
                    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
                    exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
                ) {
                    Column {
                        // Перетаскиваемая полоса для изменения размера панели
                        ResizeHandle(
                            onDrag = { yOffset ->
                                // Инвертируем, так как движение вверх должно увеличивать высоту
                                val newHeight = settingsPanelHeight.value - yOffset.dp
                                // Ограничиваем в пределах минимума и максимума
                                settingsPanelHeight.value = newHeight.coerceIn(minPanelHeight, maxPanelHeight)
                            }
                        )

                        // Панель настроек с изменяемым размером
                        SettingsPanel(
                            height = settingsPanelHeight.value,
                            settings = settings.value,
                            onSettingsChanged = {
                                settings.value = it
//                                simulation.updateSettings(it)
                            },
                            onExportSettings = { exportSettings() },
                            onImportSettingsClick = { isImportDialogOpen.value = true }
                        )
                    }
                }
            }
        }
    }
}

//@Composable
//private fun SimulationVisualization(simulation: PhysarumSimulation) {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            val simulationState = simulation.state.value
//
//            Text(
//                "Physarum Simulation",
//                style = MaterialTheme.typography.h4
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Агентов: ${simulationState.agents.size}",
//                style = MaterialTheme.typography.body1
//            )
//
//            Text(
//                text = "Шаг симуляции: ${simulationState.step}",
//                style = MaterialTheme.typography.body1
//            )
//
//            Text(
//                text = "Статус: ${if (simulationState.isRunning) "Запущена" else "Остановлена"}",
//                style = MaterialTheme.typography.body1
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Button(
//                    onClick = {
//                        if (simulationState.isRunning) {
//                            simulation.stop()
//                        } else {
//                            simulation.start()
//                        }
//                    }
//                ) {
//                    Text(if (simulationState.isRunning) "Остановить" else "Запустить")
//                }
//
//                Button(
//                    onClick = { simulation.reset() }
//                ) {
//                    Text("Сбросить")
//                }
//            }
//        }
//    }
//}

fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication, title = "Physarum Simulation") {
        App()
    }
}
