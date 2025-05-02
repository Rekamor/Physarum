// Обновленный диалог импорта
package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ImportDialog(
    importText: String,
    onImportTextChange: (String) -> Unit,
    onPasteFromClipboard: () -> Unit,
    onImportSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Импорт настроек") },
        text = {
            Column {
                Text("Вставьте текст с настройками:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = importText,
                    onValueChange = onImportTextChange,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отмена")
                }
                
                Button(
                    onClick = onPasteFromClipboard,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Вставить из буфера")
                }
                
                Button(
                    onClick = onImportSettings,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Импорт")
                }
            }
        }
    )
}