package de.ott.ivy.data

import de.ott.ivy.data.enums.Priorities
import de.ott.ivy.data.enums.TaskStatus
import de.ott.ivy.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

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
data class IvyLeeTask(var name: String = "", var descr: String = "",
                      var estTimeSeconds: Int = 0, var status: TaskStatus = TaskStatus.EMPTY,
                      var timeInvestedSeconds: Int = 0, var favorite: Boolean = false,
                      @Serializable(with = LocalDateSerializer::class) var dueDate: LocalDate = LocalDate.now().plusDays(7),
                      var priority: Priorities = Priorities.LOWEST) {

    companion object {
        const val REVISION = 3
    }
}
