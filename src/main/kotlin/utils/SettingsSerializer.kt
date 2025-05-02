package utils

import kotlinx.serialization.json.Json
import model.SimulationSettings
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

object SettingsSerializer {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun toJson(settings: SimulationSettings): String {
        return try {
            json.encodeToString(settings)
        } catch (e: Exception) {
            throw SettingsSerializationException("Ошибка при сериализации настроек: ${e.message}")
        }
    }

    fun fromJson(jsonString: String): SimulationSettings {
        return try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            throw SettingsSerializationException("Ошибка при десериализации настроек: ${e.message}")
        }
    }
}

object ClipboardManager {
    fun copyToClipboard(text: String) {
        try {
            val selection = StringSelection(text)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(selection, null)
        } catch (e: Exception) {
            throw ClipboardException("Ошибка при копировании в буфер обмена: ${e.message}")
        }
    }

    fun getFromClipboard(): String {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            return clipboard.getData(DataFlavor.stringFlavor) as String
        } catch (e: Exception) {
            throw ClipboardException("Ошибка при получении данных из буфера обмена: ${e.message}")
        }
    }
}

class SettingsSerializationException(message: String) : Exception(message)
class ClipboardException(message: String) : Exception(message)