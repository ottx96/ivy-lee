package de.mait.ott

import javafx.scene.control.ProgressIndicator
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Polygon
import tornadofx.*
import java.lang.Thread.sleep

/**
 * TODO: Insert Description!
 * Project: ivy-lee
 * Package:
 * Created: 28.01.2020 15:55
 * @author = manuel.ott
 * @since = 28. Januar 2020
 */
class IvyLee : View("Ivy-Lee Tracking") {
    override val root: GridPane by fxml("/views/IvyLee.fxml")

    val COLOR_NOT_DEFINED = Color.valueOf("#ffe5cc")
    val COLOR_UNDONE = Color.valueOf( "#ff9933")
    val COLOR_DONE = Color.valueOf( "#84ee3f")

    val topLeft: BorderPane by fxid("bp_topleft")
    val topRight: BorderPane by fxid("bp_topright")
    val middleLeft: BorderPane by fxid("bp_middleleft")
    val middleRight: BorderPane by fxid("bp_middleright")
    val bottomLeft: BorderPane by fxid("bp_bottomright")
    val bottomRight: BorderPane by fxid("bp_bottomleft")

    val progressTopLeft: ProgressIndicator by fxid("pr_top_left")

    init {
        root.add(Polygon().apply {
            runAsync {
                while(topLeft.width == 0.0 || topLeft.height == 0.0) sleep(5)
                points.addAll(
                    topLeft.layoutX + topLeft.width, topLeft.layoutY,
                    topLeft.width - topLeft.height / 2, topLeft.layoutY,
                    topLeft.width, topLeft.layoutY + topLeft.height / 2
//                0.0, 0.0,
//                200.0,200.0,
//                0.0, 200.0
                )
            }
            println(layoutX)
            println(layoutY)
            layoutX = topLeft.width - topLeft.height / 2.0
            layoutY = topLeft.layoutY
            println(layoutX)
            println(layoutY)
        })
    }

    val tasks = mapOf(
         topLeft to IvyLeeTask(),
         topRight to IvyLeeTask(),
         middleLeft to IvyLeeTask(),
         middleRight to IvyLeeTask(),
         bottomLeft to IvyLeeTask(),
         bottomRight to IvyLeeTask()
    ).toMutableMap()

    fun mark(event: MouseEvent){
        (event.target as BorderPane).style {
            borderColor += CssBox(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
            backgroundColor += if(tasks[event.target as BorderPane]!!.done) COLOR_DONE.desaturate() else COLOR_UNDONE.desaturate()
        }
    }

    fun unmark(event: MouseEvent){
        (event.target as BorderPane).style {
            backgroundColor += if(tasks[event.target as BorderPane]!!.done) COLOR_DONE else COLOR_UNDONE
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }
    }

    fun onClick(event: MouseEvent){

        if(event.isPrimaryButtonDown)
            (event.target as BorderPane).apply {
                tasks[this]!!.done = !tasks[this]!!.done
                mark(event)
            }

        if(event.isSecondaryButtonDown){
            // open custom dialog
            (event.target as BorderPane).apply {
                TaskDialog.showDialog(tasks[this]!!)
            }
        }
    }
}

class Main: App(IvyLee::class)