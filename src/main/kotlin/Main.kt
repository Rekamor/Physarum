import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

@Composable
@Preview
fun App() {
    // Основное состояние
    val text = remember { mutableStateOf("Hello, physarum!") }
    val isExpanded = remember { mutableStateOf(false) }
    val isImportDialogOpen = remember { mutableStateOf(false) }
    val importText = remember { mutableStateOf("") }
    val statusMessage = remember { mutableStateOf("") }
    
    // Высота панели настроек
    val settingsPanelHeight = remember { mutableStateOf(400.dp) }
    val minPanelHeight = 200.dp
    val maxPanelHeight = 600.dp
    
    // Настройки для агентов
    val agentSpeed = remember { mutableStateOf("2") }
    val agentViewAngle = remember { mutableStateOf("45") }
    val agentCount = remember { mutableStateOf("1000") }
    
    // Настройки феромонов
    val pheromoneAmount = remember { mutableStateOf("5") }
    val pheromoneDiffusionRate = remember { mutableStateOf("0.1") }
    val pheromoneEvaporationRate = remember { mutableStateOf("0.05") }
    val pheromoneInfluence = remember { mutableStateOf("1.0") }
    
    val scrollState = rememberScrollState()
    
    fun exportSettings() {
        val settings = """
            {
                "agent": {
                    "speed": ${agentSpeed.value},
                    "viewAngle": ${agentViewAngle.value},
                    "count": ${agentCount.value},
                    "pheromoneAmount": ${pheromoneAmount.value}
                },
                "pheromone": {
                    "diffusionRate": ${pheromoneDiffusionRate.value},
                    "evaporationRate": ${pheromoneEvaporationRate.value},
                    "influence": ${pheromoneInfluence.value}
                }
            }
        """.trimIndent()
        
        val selection = StringSelection(settings)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, null)
        statusMessage.value = "Настройки скопированы в буфер обмена"
    }
    
    // Функция для импорта настроек из текста
    fun importSettings(settingsText: String) {
        try {
            // Для простоты используем простой подход
            val text = settingsText.replace("\\s".toRegex(), "")
            
            // Простой парсинг значений из текста
            val speedRegex = """"speed":\s*([0-9.]+)""".toRegex()
            val viewAngleRegex = """"viewAngle":\s*([0-9.]+)""".toRegex()
            val countRegex = """"count":\s*([0-9.]+)""".toRegex()
            val pheromoneAmountRegex = """"pheromoneAmount":\s*([0-9.]+)""".toRegex()
            val diffusionRateRegex = """"diffusionRate":\s*([0-9.]+)""".toRegex()
            val evaporationRateRegex = """"evaporationRate":\s*([0-9.]+)""".toRegex()
            val influenceRegex = """"influence":\s*([0-9.]+)""".toRegex()
            
            speedRegex.find(text)?.groupValues?.get(1)?.let { agentSpeed.value = it }
            viewAngleRegex.find(text)?.groupValues?.get(1)?.let { agentViewAngle.value = it }
            countRegex.find(text)?.groupValues?.get(1)?.let { agentCount.value = it }
            pheromoneAmountRegex.find(text)?.groupValues?.get(1)?.let { pheromoneAmount.value = it }
            diffusionRateRegex.find(text)?.groupValues?.get(1)?.let { pheromoneDiffusionRate.value = it }
            evaporationRateRegex.find(text)?.groupValues?.get(1)?.let { pheromoneEvaporationRate.value = it }
            influenceRegex.find(text)?.groupValues?.get(1)?.let { pheromoneInfluence.value = it }
            
            statusMessage.value = "Настройки успешно импортированы"
        } catch (e: Exception) {
            statusMessage.value = "Ошибка при импорте настроек: ${e.message}"
        }
        
        isImportDialogOpen.value = false
    }
    
    // Функция для вставки текста из буфера обмена
    fun pasteFromClipboard() {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val data = clipboard.getData(DataFlavor.stringFlavor) as String
            importText.value = data
        } catch (e: Exception) {
            statusMessage.value = "Ошибка при вставке из буфера обмена: ${e.message}"
        }
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Основной контент
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        text.value = "Hello, Ivan!"
                    }
                ) {
                    Text(text.value)
                }
                
                // Показываем статусное сообщение, если оно есть
                if (statusMessage.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage.value,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.secondary
                    )
                    
                    // Автоматически скрываем сообщение через некоторое время
                    LaunchedEffect(statusMessage.value) {
                        kotlinx.coroutines.delay(3000)
                        statusMessage.value = ""
                    }
                }
            }

            // Диалог импорта настроек
            if (isImportDialogOpen.value) {
                AlertDialog(
                    onDismissRequest = { isImportDialogOpen.value = false },
                    title = { Text("Импорт настроек") },
                    text = {
                        Column {
                            Text("Вставьте текст с настройками:")
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField(
                                value = importText.value,
                                onValueChange = { importText.value = it },
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { pasteFromClipboard() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Вставить из буфера")
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { importSettings(importText.value) }) {
                            Text("Импортировать")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { isImportDialogOpen.value = false }) {
                            Text("Отмена")
                        }
                    }
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 8.dp,
                            backgroundColor = MaterialTheme.colors.surface
                        ) {
                            // Добавляем прокрутку содержимому панели настроек с учетом изменяемой высоты
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .height(settingsPanelHeight.value) // Используем переменную высоты
                                    .verticalScroll(scrollState) // Добавляем прокрутку
                            ) {
                                Text(
                                    text = "Настройки симуляции Physarum",
                                    style = MaterialTheme.typography.h6
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Блок настроек агентов
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
                                        TextField(
                                            value = agentSpeed.value,
                                            onValueChange = { agentSpeed.value = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Скорость движения агента") },
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = agentViewAngle.value,
                                            onValueChange = { agentViewAngle.value = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Угол зрения агента (в градусах)") },
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = agentCount.value,
                                            onValueChange = { agentCount.value = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Количество агентов") },
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = pheromoneAmount.value,
                                            onValueChange = { pheromoneAmount.value = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Количество феромона, создаваемого агентом") },
                                            singleLine = true
                                        )
                                    }
                                }
                                
                                // Блок настроек феромонов
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
                                        TextField(
                                            value = pheromoneDiffusionRate.value,
                                            onValueChange = { pheromoneDiffusionRate.value = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Скорость диффузии феромона") },
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = pheromoneEvaporationRate.value,
                                            onValueChange = { pheromoneEvaporationRate.value = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Скорость испарения феромона") },
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = pheromoneInfluence.value,
                                            onValueChange = { pheromoneInfluence.value = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Сила влияния феромона на агентов") },
                                            singleLine = true
                                        )
                                    }
                                }
                                
                                // Кнопки действий с настройками
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                                ) {
                                    // Кнопка импорта настроек
                                    Button(
                                        onClick = { isImportDialogOpen.value = true }
                                    ) {
                                        Text("Импорт настроек")
                                    }
                                    
                                    // Кнопка экспорта настроек
                                    Button(
                                        onClick = { exportSettings() }
                                    ) {
                                        Text("Экспорт настроек")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResizeHandle(onDrag: (offset: Float) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        shape = MaterialTheme.shapes.small,
        color = if (isHovered) MaterialTheme.colors.primary.copy(alpha = 0.5f) else Color.Transparent,
        onClick = {  },
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        onDrag(dragAmount.y)
                        change.consume()
                    }
                }
        ) {
            // Линия-индикатор для перетаскивания
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .align(Alignment.Center)
                    .alpha(if (isHovered) 1f else 0.5f)
                    .background(
                        color = if (isHovered) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small
                    )
                    .shadow(1.dp)
            )
        }
    }
}

fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication, title = "Physarum Simulation") {
        App()
    }
}