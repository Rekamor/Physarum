package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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