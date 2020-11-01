package de.ott.ivy.ui.overview.event.impl

import de.ott.ivy.ui.overview.IvyLee
import de.ott.ivy.ui.overview.IvyLee.Companion.getCellByBorderPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
object DeleteButtonHandler {

    fun onLeftClick(borderPane: BorderPane, taskList: VBox){
        val cell = IvyLee.tasks.getCellByBorderPane(borderPane)
        taskList.children.remove(cell.borderPane)
        IvyLee.tasks.remove(cell)
    }

}