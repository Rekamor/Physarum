package org.example

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.App

fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication, title = "Physarum Simulation") {
        App()
    }
}