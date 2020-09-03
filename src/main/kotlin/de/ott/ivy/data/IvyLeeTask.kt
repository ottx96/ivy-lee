package de.ott.ivy.data

import de.ott.ivy.data.enum.TaskStatus
import javafx.scene.paint.Color
import kotlinx.serialization.Serializable

/**
 * Container (data) classes
 *
 * Project: ivy-lee
 * Package: de.mait.ott
 * Created: 28.01.2020 16:58
 * @author = manuel.ott
 * @since = 28. Januar 2020
 */
@Serializable
data class IvyLeeTask(var name: String = "", var descr: String = "", var estTimeSeconds: Int = 0, var status: TaskStatus = TaskStatus.EMPTY, var timeInvestedSeconds: Int = 0, var frog: Boolean = false){
    val REVISION = 1
}