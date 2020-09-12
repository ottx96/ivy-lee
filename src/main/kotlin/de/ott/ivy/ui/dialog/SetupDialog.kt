package de.ott.ivy.ui.dialog

import javafx.scene.Scene
import javafx.scene.control.Pagination
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.View

class SetupDialog : View("Setup") {

    override val root: AnchorPane by fxml("/views/SetupDialog.fxml")

    val pagination: Pagination by fxid("pagination")

    companion object {
        fun showDialog(){
            Stage().apply {
                scene = Scene(SetupDialog().root)
            }.showAndWait()
        }
    }

    init {
        val pages = mutableListOf<BorderPane>()

        // Max 5 pages
        repeat(5){
            val cRes = javaClass.getResource("/views/pages/page_${it}.fxml") ?: return@repeat
            val x = object: View(""){
                override val root: BorderPane by fxml(cRes.openStream())
            }.root
            pages.add(x)
        }

        if(pages.isNullOrEmpty()) throw Exception("empty pages!")
        pagination.pageCount = pages.size
        pagination.setPageFactory { pages[it] }
    }

}