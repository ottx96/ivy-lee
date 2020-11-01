package de.ott.ivy.ui.overview.event

import de.ott.ivy.ui.overview.IvyLee
import de.ott.ivy.ui.overview.IvyLee.Companion.getTaskByBorderPane
import de.ott.ivy.ui.overview.event.impl.BrowserHandler
import de.ott.ivy.ui.overview.event.impl.DeleteButtonHandler
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlinx.serialization.ExperimentalSerializationApi
import tornadofx.CssBox
import tornadofx.style

@ExperimentalSerializationApi
class IvyLeeEventHandler(private val taskList: VBox) {

    fun mark(event: MouseEvent) {
        (event.target as BorderPane).style {
            borderColor += CssBox(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
            backgroundColor += IvyLee.tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color.desaturate()
        }
    }

    fun unmark(event: MouseEvent) {
        (event.target as BorderPane).style {
            backgroundColor += IvyLee.tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }
    }

    fun onClick(event: MouseEvent) {
        when(val element = event.target){
            is BorderPane -> {
                if (event.isPrimaryButtonDown)
                    BrowserHandler.onLeftClick(element)
                else if(event.isSecondaryButtonDown)
                    BrowserHandler.onRightClick(element)
            }
            is ImageView -> {
                if (event.isPrimaryButtonDown && element.id == "delete")
                    // ImageView > HBox (left) > BorderPane (top) > BorderPane (main frame)
                    DeleteButtonHandler.onLeftClick((element.parent.parent.parent as BorderPane), taskList)
            }
        }
    }

}