package de.ott.ivy.ui.overview.impl

import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.data.enums.TaskStatus
import de.ott.ivy.html.MarkdownParser
import de.ott.ivy.ui.overview.IvyLee
import javafx.scene.paint.Color
import tornadofx.*

/**
 * TODO: Insert Description!
 * Project: ivy-lee
 * Package: de.ott.ivy.ui.overview.impl
 * Created: 19.10.2020 14:56
 * @author = manuel.ott
 * @since = 19. Oktober 2020
 */
object TaskCellUpdater {

    fun updateTaskCell(task: IvyLeeTask, cellContainer: TaskCellContainer) {
        cellContainer.titleLabel.text = task.name
        if (cellContainer.descLabel.engine.userStyleSheetLocation.isNullOrBlank())
            cellContainer.descLabel.engine.userStyleSheetLocation = javaClass.getResource("/de/ott/ivy/css/style.css").toString()
        cellContainer.timeLabel.text = "${task.estTimeSeconds / 60.0} m"

        cellContainer.progressBar.progress = task.timeInvestedSeconds * 1.0 / task.estTimeSeconds
        cellContainer.statusIndicator.progress = task.timeInvestedSeconds * 1.0 / task.estTimeSeconds
        if (task.status == TaskStatus.DONE) cellContainer.statusIndicator.progress = 1.0

        if (task.timeInvestedSeconds >= task.estTimeSeconds)
            cellContainer.progressBarAdditional.progress = (task.timeInvestedSeconds - task.estTimeSeconds) * 1.0 / task.estTimeSeconds
        else
            cellContainer.progressBarAdditional.progress = 0.0

        cellContainer.borderPane.style {
            backgroundColor += task.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }

        if (Thread.currentThread().name == IvyLee.MAIN_THREAD_NAME)
            cellContainer.descLabel.engine.loadContent(MarkdownParser.convertHtml(task.descr), "text/html")
    }

}