package ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
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
import model.SimulationSettings
import simulationLogic.PhysarumSimulation
import ui.components.FullscreenSettingsContent
import ui.components.ImportDialog
import simulationLogic.SimulationVisualization
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
    
    // Общий обработчик нажатия Escape
    val handleEscapeKey: (key: Key, type: KeyEventType) -> Boolean = { key, type ->
        if (key == Key.Escape && type == KeyEventType.KeyDown) {
            isExpanded.value = false
            true // Обработали событие
        } else {
            false // Не обработали событие
        }
    }

    // Настройки для симуляции
    val settings = remember { mutableStateOf(SimulationSettings()) }
    
    // Создаем экземпляр симуляции
    val simulation = remember { PhysarumSimulation(settings.value) }

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
            simulation.updateSettings(importedSettings)
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
    
    LaunchedEffect(Unit) {
        simulation.start()
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Основной контент - визуализация симуляции
            SimulationVisualization(simulation)

            // Иконка шестеренки в правом нижнем углу (вместо кнопки настроек)
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                // Полупрозрачный фон для иконки
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                ) {
                    // Иконка настроек
                    IconButton(
                        onClick = { isExpanded.value = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
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

            // Затемнение фона (отдельно от настроек)
            AnimatedVisibility(
                visible = isExpanded.value,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
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
                            handleEscapeKey(keyEvent.key, keyEvent.type)
                        }
                        // Добавляем обработчик кликов, чтобы закрывать настройки при клике на затемнение
                        .clickable { isExpanded.value = false }

                )
            }

            // Панель настроек (отдельно от затемнения)
            AnimatedVisibility(
                visible = isExpanded.value,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300))
            ) {
                // Содержимое настроек
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .onKeyEvent { keyEvent ->
                            handleEscapeKey(keyEvent.key, keyEvent.type)
                        }

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
                                    // Обновляем настройки в симуляции при их изменении
                                    simulation.updateSettings(it)
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