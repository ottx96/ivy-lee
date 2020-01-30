package de.mait.ott

import javafx.scene.control.*
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
    val titleLabelTopLeft: Label by fxid("task_title_top_left")
    val descAreaTopLeft: TextArea by fxid("task_desc_top_left")
    val timeTopLeft: Label by fxid("time_top_left")
    val statusTopLeft: ProgressIndicator by fxid("status_top_left")
    val progressTopLeft: ProgressBar by fxid("progress_top_left")
    val progressAdditionalTopLeft: ProgressBar by fxid("progress_addditional_top_left")

    val topRight: BorderPane by fxid("bp_topright")
    val middleLeft: BorderPane by fxid("bp_middleleft")
    val middleRight: BorderPane by fxid("bp_middleright")
    val bottomLeft: BorderPane by fxid("bp_bottomright")
    val bottomRight: BorderPane by fxid("bp_bottomleft")

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
                with(tasks[this]!!){
                    titleLabelTopLeft.text = name
                    descAreaTopLeft.text = descr
                    timeTopLeft.text = "$estTime m"

                    progressTopLeft.progress = timeInvestedMin * 1.0 / estTime
                    statusTopLeft.progress = timeInvestedMin * 1.0 / estTime
                    if(timeInvestedMin >= estTime){
                        progressAdditionalTopLeft.progress = (timeInvestedMin - estTime) * 1.0 / estTime
                    }else
//                        progressTopLeft.isIndeterminate = false
                        progressAdditionalTopLeft.progress = 0.0
                }
            }
        }
    }
}

class Main: App(IvyLee::class)