package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onPasteFromClipboard,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Вставить из буфера")
                }
            }
        },
        confirmButton = {
            Button(onClick = onImportSettings) {
                Text("Импортировать")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}