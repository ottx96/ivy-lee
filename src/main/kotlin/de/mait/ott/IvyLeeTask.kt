package de.mait.ott

import javafx.scene.paint.Color
import kotlinx.serialization.Serializable

/**
 * TODO: Insert Description!
 * Project: ivy-lee
 * Package: de.mait.ott
 * Created: 28.01.2020 16:58
 * @author = manuel.ott
 * @since = 28. Januar 2020
 */

@Serializable
data class IvyLeeTask(var name: String = "", var descr: String = "", var estTime: Int = 0, var status: TaskStatus = TaskStatus.EMPTY, var timeInvestedMin: Int = 0, var frog: Boolean = false)

enum class TaskStatus(var color: Color){
    EMPTY(Color.valueOf("#dbdbdb")), UNDONE(Color.valueOf("#ff9933")), IN_WORK(Color.valueOf("#ffdd22")), DONE(Color.valueOf("#84ee3f"))
}